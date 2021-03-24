package main;


import cse332.interfaces.trie.TrieMap;
import cse332.interfaces.trie.TrieSet;
import cse332.types.*;
import datastructures.dictionaries.HashTrieMap;
import datastructures.dictionaries.HashTrieSet;

public class TrieTest {
    public static void main(String[] args) {
        System.out.println("Here we go...");

        TrieMap<Character, AlphabeticString, String> map = new HashTrieMap<Character, AlphabeticString, String>(AlphabeticString.class);
        for (char a : new char[]{'a', 'b', 'c'}) {
            for (char b : new char[]{'a', 'b', 'c'}) {
                for (char c : new char[]{'a', 'b', 'c'}) {
                    String s = "" + a + b + c;
                    map.insert(new AlphabeticString(s), s.toUpperCase());
                }
            }
        }

        System.out.println(map);

        for (char a : new char[]{'a', 'b', 'c', 'd'}) {
            for (char b : new char[]{'a', 'b', 'c', 'd'}) {
                for (char c : new char[]{'a', 'b', 'c', 'd'}) {
                    String s = "" + a + b + c;
                    System.out.println("find(" + s + "): " + map.find(new AlphabeticString(s)));
                    System.out.println("size: " + map.size());
                    map.delete(new AlphabeticString(s));
                    System.out.println("find(" + s + "): " + map.find(new AlphabeticString(s)));
                    System.out.println("size: " + map.size());
                }
            }
        }

        TrieSet<Byte, ByteString> trie = new HashTrieSet<Byte, ByteString>(ByteString.class);
        trie.add(new ByteString("adam"));
        trie.add(new ByteString("ada"));
        trie.add(new ByteString("apple"));
        trie.add(new ByteString("app"));
        trie.add(new ByteString("add"));
        trie.add(new ByteString("ads"));
        System.out.println(trie);

        for (ByteString s : trie) {
            System.out.println(s);
        }
    }

}
