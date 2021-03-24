/* net.sf.jazzlib.HuffmanCompressor
   Copyright (C) 2001 Free Software Foundation, Inc.

   This file is part of GNU Classpath.

   GNU Classpath is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   GNU Classpath is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with GNU Classpath; see the file COPYING.  If not, write to the
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
   02111-1307 USA.

   Linking this library statically or dynamically with other modules is
   making a combined work based on this library.  Thus, the terms and
   conditions of the GNU General Public License cover the whole
   combination.

   As a special exception, the copyright holders of this library give you
   permission to link this library with independent modules to produce an
   executable, regardless of the license terms of these independent
   modules, and to copy and distribute the resulting executable under
   terms of your choice, provided that you also meet, for each linked
   independent module, the terms and conditions of the license of that
   module.  An independent module is a module which is not derived from
   or based on this library.  If you modify this library, you may extend
   this exception to your version of the library, but you are not
   obligated to do so.  If you do not wish to do so, delete this
   exception statement from your version. */

package cse332.jazzlib;

import java.util.*;

import cse332.datastructures.containers.Item;
import cse332.types.BitString;
import datastructures.dictionaries.HashTrieMap;

/**
 * This is the HuffmanCompressor class.
 *
 * This class is <i>not</i> thread safe.  This is inherent in the API, due
 * to the split of deflate and setInput.
 *
 * @author Jochen Hoenicke
 * @date Jan 6, 2000
 */
public class HuffmanCompressor
{
    private static final int BUFSIZE = 1 << (DeflaterConstants.DEFAULT_MEM_LEVEL + 6);
    private static final int LITERAL_NUM = 286;
    private static final int DIST_NUM = 30;
    private static final int BITLEN_NUM = 19;
    private static final int REP_3_6    = 16;
    private static final int REP_3_10   = 17;
    private static final int REP_11_138 = 18;
    private static final int EOF_SYMBOL = 256;
    private static final int[] BL_ORDER =
            { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };

    private final static String bit4Reverse =
            "\000\010\004\014\002\012\006\016\001\011\005\015\003\013\007\017";

    class Tree extends HashTrieMap<Boolean, BitString, Integer>{
        short[] freqs;
        short[] codes;
        byte[]  length;
        int[]   bl_counts;
        int     minNumCodes, numCodes;
        int     maxLength;

        Tree(int elems, int minCodes, int maxLength) {
            super(BitString.class);

            this.minNumCodes = minCodes;
            this.maxLength  = maxLength;
            freqs  = new short[elems];
            bl_counts = new int[maxLength];
        }

        public int getOverflow() {
            return getOverflow(0, (Node)this.root);
        }

        private int getOverflow(int height, Node node) {
            if (node == null) {
                return 0;
            }

            if (node.pointers.size() == 0) {
                return 0;
            }

            int result = 0;
            if (node.pointers.containsKey(true)) {
                if (height + 1 > this.maxLength) {
                    result++;
                }
                result += getOverflow(height + 1, (Node)node.pointers.get(false));
                result += getOverflow(height + 1, (Node)node.pointers.get(true));
            }
            return result;
        }

        class Node extends HashTrieNode implements Comparable<Node> {
            int freq;
            int size;
            int height;

            public Node(int value, int freq) {
                this(null, null);
                this.freq = freq;
                this.value = value;
                this.size = 1;
            }

            public Node(Node zero, Node one) {
                this.freq = (zero != null ? zero.freq : 0) + (one != null ? one.freq : 0);
                this.pointers = new HashMap<Boolean, HashTrieNode>(2);
                this.size = (zero != null ? zero.size : 0) + (one != null ? one.size : 0) + 1;

                if (zero != null) {
                    this.pointers.put(false, zero);
                }
                if (one != null) {
                    this.pointers.put(true, one);
                }
            }

            public int compareTo(Node other) {
                return this.freq - other.freq;
            }
        }
        void reset() {
            for (int i = 0; i < freqs.length; i++)
                freqs[i] = 0;
            codes = null;
            length = null;
        }

        final void writeSymbol(int code)
        {
            if (DeflaterConstants.DEBUGGING) {
                freqs[code]--;
            }
            pending.writeBits(codes[code] & 0xffff, length[code]);
        }

        final void checkEmpty()
        {
            boolean empty = true;
            for (int i = 0; i < freqs.length; i++)
                if (freqs[i] != 0)
                {
                    System.err.println("freqs["+i+"] == "+freqs[i]);
                    empty = false;
                }
            if (!empty)
                throw new InternalError();
            System.err.println("checkEmpty suceeded!");
        }

        void setStaticCodes(short[] stCodes, byte[] stLength)
        {
            codes = stCodes;
            length = stLength;
        }

        public void buildLength() {
            length = new byte[freqs.length];

            for (int i = 0; i < maxLength; i++)
                bl_counts[i] = 0;

            int overflow = this.getOverflow();

            List<BitString> tree = new ArrayList<>(this.size());


            for (Item<BitString, Integer> k : this) {
                tree.add(k.key);
            }

            for (BitString k : tree) {
                int size = Math.min(k.size(), maxLength);
                bl_counts[size - 1]++;
                length[this.find(k)] = (byte)(size);
            }

            if (overflow != 0) {
                int incrBitLen = maxLength - 1;
                do {
                    while (bl_counts[--incrBitLen] == 0);

                    do {
                        bl_counts[incrBitLen]--;
                        bl_counts[++incrBitLen]++;
                        overflow -= 1 << (maxLength - 1 - incrBitLen);
                    } while ((overflow > 0) && (incrBitLen < (maxLength - 1)));
                } while (overflow > 0);

    			/*
    			 * We may have overshot above. Move some nodes from maxLength to
    			 * maxLength-1 in that case.
    			 */
                bl_counts[maxLength - 1] += overflow;
                bl_counts[maxLength - 2] -= overflow;

                Collections.sort(tree, (x, y) -> x.size() - y.size());

                int pointer = -1;
                int remaining = 0;
                for (BitString k : tree) {
                    while (remaining == 0) {
                        pointer++;
                        remaining = bl_counts[pointer];
                    }
                    length[this.find(k)] = (byte)(pointer + 1);
                    remaining--;
                }
            }
        }

        public void buildCodes() {
            int[] nextCode = new int[maxLength];
            int code = 0;
            codes = new short[freqs.length];

            for (int bits = 0; bits < maxLength; bits++) {
                nextCode[bits] = (int)code;
                code += bl_counts[bits] << (15 - bits);
            }

            for (int i = 0; i < length.length; i++) {
                int bits = length[i];
                if (bits > 0) {
                    codes[i] = bitReverse(nextCode[bits-1]);
                    nextCode[bits-1] += 1 << (16 - bits);
                }
            }
        }


        void buildTree() {
            int numSymbols = freqs.length;

            int maxCode = 0;

            Queue<Node> queue = new PriorityQueue<Node>();
            for (int n = 0; n < numSymbols; n++) {
                int freq = freqs[n];
                if (freq != 0) {
                    /* Insert n into heap */
                    queue.add(new Node(n, freq));
                    maxCode = n;
                }
            }

            if (queue.size() == 1) {
                Node real = queue.remove();
                queue.add(new Node(null, real));
            }

            numCodes = Math.max(maxCode + 1, minNumCodes);

            /* Construct the Huffman tree by repeatedly combining the least two
             * frequent nodes.
             */
            while (queue.size() > 1) {
                Node a = queue.remove();
                Node b = queue.remove();
                queue.add(new Node(a, b));
            }

            if (queue.size() > 0) {
                this.root = queue.remove();
                this.size = ((Node)this.root).size;
            }
            else {
                this.root = null;
                this.size = 0;
            }

            buildLength();
        }

        int getEncodedLength()
        {
            int len = 0;
            for (int i = 0; i < freqs.length; i++)
                len += freqs[i] * length[i];
            return len;
        }

        void calcBLFreq(Tree blTree) {
            int max_count;               /* max repeat count */
            int min_count;               /* min repeat count */
            int count;                   /* repeat count of the current code */
            int curlen = -1;             /* length of current code */

            int i = 0;
            while (i < numCodes)
            {
                count = 1;
                int nextlen = length[i];
                if (nextlen == 0)
                {
                    max_count = 138;
                    min_count = 3;
                }
                else
                {
                    max_count = 6;
                    min_count = 3;
                    if (curlen != nextlen)
                    {
                        blTree.freqs[nextlen]++;
                        count = 0;
                    }
                }
                curlen = nextlen;
                i++;

                while (i < numCodes && curlen == length[i])
                {
                    i++;
                    if (++count >= max_count)
                        break;
                }

                if (count < min_count)
                    blTree.freqs[curlen] += count;
                else if (curlen != 0)
                    blTree.freqs[REP_3_6]++;
                else if (count <= 10)
                    blTree.freqs[REP_3_10]++;
                else
                    blTree.freqs[REP_11_138]++;
            }
        }

        void writeTree(Tree blTree)
        {
            int max_count;               /* max repeat count */
            int min_count;               /* min repeat count */
            int count;                   /* repeat count of the current code */
            int curlen = -1;             /* length of current code */

            int i = 0;
            while (i < numCodes)
            {
                count = 1;
                int nextlen = length[i];
                if (nextlen == 0)
                {
                    max_count = 138;
                    min_count = 3;
                }
                else
                {
                    max_count = 6;
                    min_count = 3;
                    if (curlen != nextlen)
                    {
                        blTree.writeSymbol(nextlen);
                        count = 0;
                    }
                }
                curlen = nextlen;
                i++;

                while (i < numCodes && curlen == length[i])
                {
                    i++;
                    if (++count >= max_count)
                        break;
                }

                if (count < min_count)
                {
                    while (count-- > 0)
                        blTree.writeSymbol(curlen);
                }
                else if (curlen != 0)
                {
                    blTree.writeSymbol(REP_3_6);
                    pending.writeBits(count - 3, 2);
                }
                else if (count <= 10)
                {
                    blTree.writeSymbol(REP_3_10);
                    pending.writeBits(count - 3, 3);
                }
                else
                {
                    blTree.writeSymbol(REP_11_138);
                    pending.writeBits(count - 11, 7);
                }
            }
        }
    }



    DeflaterPending pending;
    private Tree literalTree, distTree, blTree;

    private short d_buf[];
    private byte l_buf[];
    private int last_lit;
    private int extra_bits;

    private static short staticLCodes[];
    private static byte  staticLLength[];
    private static short staticDCodes[];
    private static byte  staticDLength[];

    /**
     * Reverse the bits of a 16 bit value.
     */
    static short bitReverse(int value) {
        return (short) (bit4Reverse.charAt(value & 0xf) << 12
                | bit4Reverse.charAt((value >> 4) & 0xf) << 8
                | bit4Reverse.charAt((value >> 8) & 0xf) << 4
                | bit4Reverse.charAt(value >> 12));
    }

    static {
        /* See RFC 1951 3.2.6 */
        /* Literal codes */
        staticLCodes = new short[LITERAL_NUM];
        staticLLength = new byte[LITERAL_NUM];
        int i = 0;
        while (i < 144) {
            staticLCodes[i] = bitReverse((0x030 + i) << 8);
            staticLLength[i++] = 8;
        }
        while (i < 256) {
            staticLCodes[i] = bitReverse((0x190 - 144 + i) << 7);
            staticLLength[i++] = 9;
        }
        while (i < 280) {
            staticLCodes[i] = bitReverse((0x000 - 256 + i) << 9);
            staticLLength[i++] = 7;
        }
        while (i < LITERAL_NUM) {
            staticLCodes[i] = bitReverse((0x0c0 - 280 + i)  << 8);
            staticLLength[i++] = 8;
        }

        /* Distant codes */
        staticDCodes = new short[DIST_NUM];
        staticDLength = new byte[DIST_NUM];
        for (i = 0; i < DIST_NUM; i++) {
            staticDCodes[i] = bitReverse(i << 11);
            staticDLength[i] = 5;
        }
    }

    public HuffmanCompressor(DeflaterPending pending)
    {
        this.pending = pending;

        literalTree = new Tree(LITERAL_NUM, 257, 15);
        distTree    = new Tree(DIST_NUM, 1, 15);
        blTree      = new Tree(BITLEN_NUM, 4, 7);

        d_buf = new short[BUFSIZE];
        l_buf = new byte [BUFSIZE];
    }

    public void reset() {
        last_lit = 0;
        extra_bits = 0;
        literalTree.reset();
        distTree.reset();
        blTree.reset();
    }

    private int l_code(int len) {
        if (len == 255)
            return 285;

        int code = 257;
        while (len >= 8)
        {
            code += 4;
            len >>= 1;
        }
        return code + len;
    }

    private int d_code(int distance) {
        int code = 0;
        while (distance >= 4)
        {
            code += 2;
            distance >>= 1;
        }
        return code + distance;
    }

    public void sendAllTrees(int blTreeCodes) {
        blTree.buildCodes();
        literalTree.buildCodes();
        distTree.buildCodes();
        pending.writeBits(literalTree.numCodes - 257, 5);
        pending.writeBits(distTree.numCodes - 1, 5);
        pending.writeBits(blTreeCodes - 4, 4);
        for (int rank = 0; rank < blTreeCodes; rank++)
            pending.writeBits(blTree.length[BL_ORDER[rank]], 3);
        literalTree.writeTree(blTree);
        distTree.writeTree(blTree);
        if (DeflaterConstants.DEBUGGING)
            blTree.checkEmpty();
    }

    public void compressBlock() {
        for (int i = 0; i < last_lit; i++)
        {
            int litlen = l_buf[i] & 0xff;
            int dist = d_buf[i];
            if (dist-- != 0)
            {
                if (DeflaterConstants.DEBUGGING)
                    System.err.print("["+(dist+1)+","+(litlen+3)+"]: ");

                int lc = l_code(litlen);
                literalTree.writeSymbol(lc);

                int bits = (lc - 261) / 4;
                if (bits > 0 && bits <= 5)
                    pending.writeBits(litlen & ((1 << bits) - 1), bits);

                int dc = d_code(dist);
                distTree.writeSymbol(dc);

                bits = dc / 2 - 1;
                if (bits > 0)
                    pending.writeBits(dist & ((1 << bits) - 1), bits);
            }
            else
            {
                if (DeflaterConstants.DEBUGGING)
                {
                    if (litlen > 32 && litlen < 127)
                        System.err.print("("+(char)litlen+"): ");
                    else
                        System.err.print("{"+litlen+"}: ");
                }
                literalTree.writeSymbol(litlen);
            }
        }
        if (DeflaterConstants.DEBUGGING)
            System.err.print("EOF: ");
        literalTree.writeSymbol(EOF_SYMBOL);
        if (DeflaterConstants.DEBUGGING)
        {
            literalTree.checkEmpty();
            distTree.checkEmpty();
        }
    }

    public void flushStoredBlock(byte[] stored,
                                 int stored_offset, int stored_len,
                                 boolean lastBlock) {
        if (DeflaterConstants.DEBUGGING)
            System.err.println("Flushing stored block "+ stored_len);
        pending.writeBits((DeflaterConstants.STORED_BLOCK << 1)
                + (lastBlock ? 1 : 0), 3);
        pending.alignToByte();
        pending.writeShort(stored_len);
        pending.writeShort(~stored_len);
        pending.writeBlock(stored, stored_offset, stored_len);
        reset();
    }

    public void flushBlock(byte[] stored, int stored_offset, int stored_len,
                           boolean lastBlock) {
        literalTree.freqs[EOF_SYMBOL]++;

        /* Build trees */
        literalTree.buildTree();
        distTree.buildTree();

        /* Calculate bitlen frequency */
        literalTree.calcBLFreq(blTree);
        distTree.calcBLFreq(blTree);

        /* Build bitlen tree */
        blTree.buildTree();

        int blTreeCodes = 4;
        for (int i = 18; i > blTreeCodes; i--)
        {
            if (blTree.length[BL_ORDER[i]] > 0)
                blTreeCodes = i+1;
        }
        int opt_len = 14 + blTreeCodes * 3 + blTree.getEncodedLength()
                + literalTree.getEncodedLength() + distTree.getEncodedLength()
                + extra_bits;

        int static_len = extra_bits;
        for (int i = 0; i < LITERAL_NUM; i++)
            static_len += literalTree.freqs[i] * staticLLength[i];
        for (int i = 0; i < DIST_NUM; i++)
            static_len += distTree.freqs[i] * staticDLength[i];
        if (opt_len >= static_len)
        {
            /* Force static trees */
            opt_len = static_len;
        }

        if (stored_offset >= 0 && stored_len+4 < opt_len >> 3)
        {
            /* Store Block */
            if (DeflaterConstants.DEBUGGING)
                System.err.println("Storing, since " + stored_len + " < " + opt_len
                        + " <= " + static_len);
            flushStoredBlock(stored, stored_offset, stored_len, lastBlock);
        }
        else if (opt_len == static_len)
        {
            /* Encode with static tree */
            pending.writeBits((DeflaterConstants.STATIC_TREES << 1)
                    + (lastBlock ? 1 : 0), 3);
            literalTree.setStaticCodes(staticLCodes, staticLLength);
            distTree.setStaticCodes(staticDCodes, staticDLength);
            compressBlock();
            reset();
        }
        else
        {
            /* Encode with dynamic tree */
            pending.writeBits((DeflaterConstants.DYN_TREES << 1)
                    + (lastBlock ? 1 : 0), 3);
            sendAllTrees(blTreeCodes);
            compressBlock();
            reset();
        }
    }

    public boolean isFull()
    {
        return last_lit == BUFSIZE;
    }

    public boolean tallyLit(int lit)
    {
        if (DeflaterConstants.DEBUGGING)
        {
            if (lit > 32 && lit < 127)
                System.err.println("("+(char)lit+")");
            else
                System.err.println("{"+lit+"}");
        }
        d_buf[last_lit] = 0;
        l_buf[last_lit++] = (byte) lit;
        literalTree.freqs[lit]++;
        return last_lit == BUFSIZE;
    }

    public boolean tallyDist(int dist, int len)
    {
        if (DeflaterConstants.DEBUGGING)
            System.err.println("[" + dist + ", " + len + "]");

        d_buf[last_lit] = (short) dist;
        l_buf[last_lit++] = (byte) (len - 3);

        int lc = l_code(len-3);
        literalTree.freqs[lc]++;
        if (lc >= 265 && lc < 285)
            extra_bits += (lc - 261) / 4;

        int dc = d_code(dist-1);
        distTree.freqs[dc]++;
        if (dc >= 4)
            extra_bits += dc / 2 - 1;
        return last_lit == BUFSIZE;
    }
}
