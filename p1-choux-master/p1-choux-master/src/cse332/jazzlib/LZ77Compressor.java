package cse332.jazzlib;

import cse332.interfaces.worklists.FIFOWorkList;
import datastructures.SuffixTrie;


/**
 * LZ77Compressor compresses an input stream conforming to the DEFLATE format.  (The format used
 * in Zip and GZip.) 
 * 
 * @author Adam Blank
 *
 */
public class LZ77Compressor {
    private SuffixTrie suffixes;
    public static final int MAX_MATCH_LENGTH = 257;

    public LZ77Compressor(int bufferLen) {
        this.suffixes = new SuffixTrie(bufferLen, MAX_MATCH_LENGTH);
    }

    /**
     * Returns the terminal SuffixTrieNode representing the match.  There are three cases:
     *      Case 1: We found normal a match.  Then, this method will return a SuffixTrieNode that has a value.
     *      Case 2: We found a special match.  Then, this method will return a SuffixTrieNode with NO VALUE. 
     *              This SuffixTrieNode can be used to find the value by traversing all paths to find any leaf.
     *      Case 3: We found no match.  Then, this method will return null.
     * @param buffer
     * @return  see above
     */
    public void findNextMatch(FIFOWorkList<Byte> buffer, HuffmanCompressor huffman) {
        // Try and find an initial match
        int matchLength = suffixes.startNewMatch(buffer);

        // If we've found a match, take care of any repeating sequences
        int extensionLength = 0;
        if (matchLength > 0) {
            extensionLength = suffixes.extendMatch(buffer);
        }

        /* We only want to encode matches that have length at least 3, because otherwise
         * it's a waste of space. (e.g., it doesn't actually compress anything)
         */
        FIFOWorkList<Byte> match = suffixes.getMatch();
        if (match.size() > 2) {
            int back = (extensionLength > 0 ? matchLength : match.size()) + suffixes.getDistanceToLeaf();
            huffman.tallyDist(back, match.size());
            if (buffer.hasWork()) {
                byte b = buffer.next();
                suffixes.addToMatch(b);
                huffman.tallyLit(((int)b) & 0xff);
            }
        }
        else {
            // Make sure that we progress at least one character...
            if (!match.hasWork() && buffer.hasWork()) {
                byte b = buffer.next();
                suffixes.addToMatch(b);
                match.add(b);
            }

            int len = match.size();
            for (int i = 0; i < len; i++) {
                byte x = match.next();
                huffman.tallyLit(((int)x) & 0xff);
            }
        }

        // Update the data structures for each of the characters we've passed through the window
        suffixes.advance();
    }

    public void reset() {
        this.suffixes.clear();
    }
}
