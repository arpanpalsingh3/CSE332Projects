package cse332.types;

import cse332.interfaces.misc.BString;
import cse332.interfaces.worklists.FixedSizeFIFOWorkList;

public class AlphabeticString extends BString<Character> { 
    public AlphabeticString(String s) {
        super(BString.wrap(s.toCharArray()));
    }

    public AlphabeticString(FixedSizeFIFOWorkList<Character> q) {
        super(q);
    }
    
    public AlphabeticString(Character[] s) {
        super(s);
    }
    
    public static Class<Character> getLetterType() { return Character.class; }
}
