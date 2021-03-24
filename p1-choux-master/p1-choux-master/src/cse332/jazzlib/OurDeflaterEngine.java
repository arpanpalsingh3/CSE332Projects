/* net.sf.jazzlib.DeflaterEngine
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


import cse332.interfaces.worklists.FixedSizeFIFOWorkList;
import datastructures.worklists.CircularArrayFIFOQueue;

class OurDeflaterEngine extends DeflaterConstants {

    /** The input data for compression. */
    private byte[] inputBuf;

    /** The offset into inputBuf, where input data starts. */
    private int inputOff;

    /** The end offset of the input data. */
    private int inputEnd;

    private int blockStart;


    private DeflaterPending pending;
    private HuffmanCompressor huffman;

    private FixedSizeFIFOWorkList<Byte> buf;
    private LZ77Compressor lz77;

    /** The adler checksum */
    private Adler32 adler;

    OurDeflaterEngine(DeflaterPending pending) {
        this.pending = pending;

        this.buf = new CircularArrayFIFOQueue<Byte>(1024);
        this.huffman = new HuffmanCompressor(pending);
        this.adler = new Adler32();
        this.lz77 = new LZ77Compressor(DeflaterConstants.BUFFER_LENGTH);
    }

    public void reset() {
        this.huffman.reset();
        this.adler.reset();
        this.lz77.reset();
    }

    public final void resetAdler() {
        adler.reset();
    }

    public final int getAdler() {
        int chksum = (int) adler.getValue();
        return chksum;
    }

    private boolean deflateSlow(boolean flush, boolean finish) {
        boolean progress = false;
        while (buf.hasWork() && !flush) {
            progress = true;
            lz77.findNextMatch(buf, huffman);

            if (huffman.isFull()) {
                lz77.reset();
                boolean lastBlock = finish && !buf.hasWork();
                huffman.flushBlock(inputBuf, blockStart, inputOff - blockStart + buf.size(), lastBlock);

                blockStart = inputOff - blockStart + buf.size();
                return !lastBlock;
            }
        }
        return progress;
    } 

    public boolean deflate(boolean flush, boolean finish)  {
        boolean progress;
        do {
            int numNew = 0;
            while (!buf.isFull() && inputOff < inputEnd) {
                buf.add(inputBuf[inputOff++]);
                numNew++;
            }
            adler.update(inputBuf, inputOff - numNew, numNew);
            boolean canFlush = flush && inputOff == inputEnd;
            progress = deflateSlow(canFlush, finish);
        }
        while (pending.isFlushed() && progress);

        if (blockStart != inputEnd) {
            huffman.flushBlock(inputBuf, blockStart, inputOff, finish);
        }
        return progress;
    }

    public void setInput(byte[] buf, int off, int len) {
        if (inputOff < inputEnd)
            throw new IllegalStateException
                ("Old input was not completely processed");

        int end = off + len;

        /* We want to throw an ArrayIndexOutOfBoundsException early.  The
         * check is very tricky: it also handles integer wrap around.  
         */
        if (0 > off || off > end || end > buf.length)
            throw new ArrayIndexOutOfBoundsException();

        inputBuf = buf;
        inputOff = off;
        inputEnd = end;
    }
    public final boolean needsInput() {
        return inputEnd == inputOff;
    }

}
