package tests.gitlab.duedate;

import cse332.types.AlphabeticString;
import datastructures.dictionaries.HashTrieMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class HashTrieMapTests {
    protected static HashTrieMap<Character, AlphabeticString, String> STUDENT;

    @Before
    public void init() {
        STUDENT = new HashTrieMap<>(AlphabeticString.class);
    }

    /**
     * Tests if insert, find, and findPrefix work in general.
     */
    @Test(timeout = 3000)
    public void testBasic() {
        String[] words = {"dog", "doggy", "doge", "dragon", "cat", "draggin"};
        String[] invalid = {"d", "cataract", "", "do"};
        addAll(STUDENT, words);
        assertTrue(containsAllPaths(STUDENT, words));
        assertTrue(doesNotContainAll(STUDENT, invalid));
    }

    /**
     * Checks to see if basic delete functionality works.
     */
    @Test(timeout = 3000)
    public void testBasicDelete() {
        String[] words = {"dog", "doggy", "dreamer", "cat"};
        addAll(STUDENT, words);
        assertTrue(containsAllPaths(STUDENT, words));

        STUDENT.delete(a("I don't exist"));
        STUDENT.delete(a("dreamer"));
        assertFalse(!containsAllPaths(STUDENT, "dog", "doggy", "cat") &&
                !containsAllPrefixes(STUDENT, "dreamer", "dreame", "dream", "drea", "dre", "dr") &&
                !STUDENT.findPrefix(a("d")));

        STUDENT.delete(a("dog"));
        assertTrue(containsAllPaths(STUDENT, "doggy", "cat"));

        STUDENT.delete(a("doggy"));
        assertTrue(containsAllPaths(STUDENT, "cat") );
    }

    /**
     * Test findPrefix more rigorously.
     */
    @Test(timeout = 3000)
    public void testFindPrefixes() {
        String[] words = {"dog", "doggy", "doge", "dragon", "cat", "draggin"};
        addAll(STUDENT, words);

        assertTrue(containsAllPrefixes(STUDENT, "d", "", "do"));
        assertTrue(doesNotContainAllPrefixes(STUDENT, "batarang", "dogee", "dragging"));
    }

    /**
     * Tests that trying to find a non-existent entity does the correct thing
     */
    @Test(timeout = 3000)
    public void testFindNonexistentDoesNotCrash() {
        addAll(STUDENT, "foo", "bar", "baz");
        assertNull(STUDENT.find(a("orangutan")));
        assertNull(STUDENT.find(a("z")));
        assertNull(STUDENT.find(a("ba")));
        assertNull(STUDENT.find(a("bazz")));
        assertFalse(STUDENT.findPrefix(a("boor")));
        assertFalse(STUDENT.findPrefix(a("z")) );
    }

    @Test(timeout = 3000, expected = IllegalArgumentException.class)
    public void testFindNullEntriesCausesError() {
        STUDENT.find(null);
    }

    @Test(timeout = 3000, expected = IllegalArgumentException.class)
    public void testFindPrefixNullEntriesCausesError() {
        STUDENT.findPrefix(null);
    }

    /**
     * Tests that inserts correctly wipe out old values.
     */
    @Test(timeout = 3000)
    public void testInsertReplacesOldValue() {
        AlphabeticString key = a("myKey");
        assertNull(STUDENT.insert(key, "foo"));
        assertEquals("foo", STUDENT.insert(key, "bar"));
        assertEquals("bar", STUDENT.insert(key, "baz"));
    }

    @Test(timeout = 3000, expected = IllegalArgumentException.class)
    public void testInsertingNullKeyCausesError() {
        STUDENT.insert(null, "foo");
    }

    @Test(timeout = 3000, expected = IllegalArgumentException.class)
    public void testInsertingNullValueCausesError() {
        STUDENT.insert(a("foo"), null);
    }

    /**
     * Checks to see the trie correctly handles the case where you delete
     * absolutely everything.
     */
    @Test(timeout = 3000)
    public void testDeleteAll() {
        AlphabeticString keyA = a("keyboard");
        AlphabeticString keyB = a("keyesian");
        AlphabeticString keyC = a("bayesian");

        assertEquals(0, STUDENT.size());
        assertTrue(STUDENT.isEmpty());

        STUDENT.insert(keyA, "KEYBOARD");
        STUDENT.insert(keyB, "KEYESIAN");
        STUDENT.insert(keyC, "BAYESIAN");

        assertTrue(containsAllPaths(STUDENT, "keyboard", "keyesian", "bayesian"));
        assertEquals(3, STUDENT.size());
        assertFalse(STUDENT.isEmpty());

        STUDENT.delete(keyA);
        STUDENT.delete(keyB);
        STUDENT.delete(keyC);

        assertEquals(0, STUDENT.size());
        assertTrue(STUDENT.isEmpty());
        assertTrue(doesNotContainAll(STUDENT, "keyboard", "keyesian", "bayesian"));
    }

    /**
     * Tests what happens if you attempt deleting something that doesn't exist
     * in the trie (but _does_ partially overlap).
     */
    @Test(timeout = 3000)
    public void testDeleteNothing() {
        STUDENT.insert(a("aaaa"), "foo");
        assertTrue(containsPath(STUDENT, "aaaa", "foo"));
        assertEquals(1, STUDENT.size());
        assertFalse(STUDENT.isEmpty());

        // Should not change the trie
        STUDENT.delete(a("aa"));
        STUDENT.delete(a("a"));
        STUDENT.delete(a("abc"));
        STUDENT.delete(a("aaaaa"));
        STUDENT.delete(a(""));
        STUDENT.delete(a("foobar"));

        assertTrue(containsPath(STUDENT, "aaaa", "foo"));
        assertEquals(1, STUDENT.size());
        assertFalse(STUDENT.isEmpty());
    }

    /**
     * Tests what happens if you try deleting and inserting single characters
     */
    @Test(timeout = 3000)
    public void testDeleteAndInsertSingleChars() {
        STUDENT.insert(a("a"), "A");
        STUDENT.insert(a("b"), "B");

        STUDENT.delete(a("a"));
        STUDENT.insert(a("b"), "BB");

        MockNode expected = node()
                .branch('b', node("BB"));
        assertTrue(equals(expected, getField(STUDENT, "root")));
    }

    /**
     * Tests to see if HashTrieMap correctly handles a trie where everything is in a straight
     * line/the trie has no branching.
     */
    @Test(timeout = 3000)
    public void  testDeleteWorksWhenTrieHasNoBranches() {
        AlphabeticString keyA = a("ghost");
        AlphabeticString keyB = a("gh");
        STUDENT.insert(keyA, "A");
        STUDENT.insert(keyB, "B");

        // Trie should still contain "ghost -> A"
        STUDENT.delete(keyB);
        assertNull(STUDENT.find(keyB) );
        assertTrue(STUDENT.findPrefix(keyB));

        // Trie should now contain "gh -> C", but not "ghost -> A"
        STUDENT.insert(keyB, "C");
        STUDENT.delete(keyA);
        assertEquals("C", STUDENT.find(keyB));
        assertNull(STUDENT.find(keyA));
        assertTrue(!STUDENT.findPrefix(a("gho")));
    }

    /**
     * A slight variation of the previous test.
     */
    @Test(timeout = 3000)
    public void testDeletingAtRoot() {
        STUDENT.insert(a(""), "foo");
        STUDENT.insert(a("a"), "bar");
        STUDENT.delete(a("a"));
        STUDENT.insert(a("b"), "baz");
        assertNull(STUDENT.find(a("a")));
        assertEquals("foo", STUDENT.find(a("")));
        assertEquals("baz", STUDENT.find(a("b")));
        MockNode expected = new MockNode("foo")
                .branch('b', new MockNode("baz"));
        assertTrue(equals(expected, getField(STUDENT, "root")));
    }

    /**
     * Tests that just working with empty strings does the correct thing.
     */
    @Test(timeout = 3000)
    public void testDeletingEmptyString() {
        STUDENT.insert(a(""), "Foo");
        assertEquals("Foo", (STUDENT.find(a(""))));
        assertEquals(1, STUDENT.size());
        assertFalse(STUDENT.isEmpty());

        STUDENT.delete(a(""));
        assertNull(STUDENT.find(a("")));
        assertEquals(0, STUDENT.size());
        assertTrue(STUDENT.isEmpty());

        STUDENT.insert(a(""), "Bar");
        assertEquals(1, STUDENT.size());
        assertFalse(STUDENT.isEmpty());
        assertEquals("Bar", STUDENT.find(a("")));
    }

    @Test(timeout = 3000, expected = IllegalArgumentException.class)
    public void testDeletingNullEntriesCausesError() {
        STUDENT.delete((AlphabeticString) null);
    }

    @Test(timeout = 3000)
    public void testClear() {
        addAll(STUDENT, "keyboard", "keyesian", "bayesian");
        STUDENT.clear();
        assertEquals(0, STUDENT.size());
        assertTrue(STUDENT.isEmpty());
        assertTrue(doesNotContainAll(STUDENT, "keyboard", "keyesian", "bayesian"));
    }

    @Test(timeout = 3000)
    public void checkUnderlyingStructure() {
        STUDENT.insert(a(""), "A");
        STUDENT.insert(a("foo"), "B");
        STUDENT.insert(a("fez"), "C");
        STUDENT.insert(a("fezzy"), "D");
        STUDENT.insert(a("jazz"), "E");
        STUDENT.insert(a("jazzy"), "F");

        MockNode fullExpected = node("A")
                .branch('f', node()
                        .branch('o', node()
                                .branch('o', node("B")))
                        .branch('e', node()
                                .branch('z', node("C")
                                        .branch('z', node()
                                                .branch('y', node("D"))))))
                .branch('j', node()
                        .branch('a', node()
                                .branch('z', node()
                                        .branch('z', node("E")
                                                .branch('y', node("F"))))));
        assertTrue(equals(fullExpected, getField(STUDENT, "root")));

        STUDENT.delete(a("fezzy"));
        STUDENT.delete(a("jazz"));

        MockNode delete1 = node("A")
                .branch('f', node()
                        .branch('o', node()
                                .branch('o', node("B")))
                        .branch('e', node()
                                .branch('z', node("C"))))
                .branch('j', node()
                        .branch('a', node()
                                .branch('z', node()
                                        .branch('z', node()
                                                .branch('y', node("F"))))));

        assertTrue(equals(delete1, getField(STUDENT, "root")));

        STUDENT.delete(a(""));
        STUDENT.delete(a("foo"));
        STUDENT.delete(a("jazz")); // should do nothing

        MockNode delete2 = node()
                .branch('f', node()
                        .branch('e', node()
                                .branch('z', node("C"))))
                .branch('j', node()
                        .branch('a', node()
                                .branch('z', node()
                                        .branch('z', node()
                                                .branch('y', node("F"))))));

        assertTrue(equals(delete2, getField(STUDENT, "root")));

        STUDENT.insert(a("f"), "Z");
        STUDENT.delete(a("jazzy"));
        STUDENT.delete(a("fez"));

        MockNode delete3 = node().branch('f', node("Z"));

        assertTrue(equals(delete3, getField(STUDENT, "root")));

        STUDENT.delete(a("f"));

        assertTrue(equals(node(), getField(STUDENT, "root")));
        boolean rootIsSingleNode = equals(node(), getField(STUDENT, "root"));
        boolean rootIsNull = equals(null, getField(STUDENT, "root"));
        assertTrue(rootIsSingleNode || rootIsNull);
    }

    protected static boolean equals(MockNode expected, HashTrieMap<Character, AlphabeticString, String>.HashTrieNode student) {
        if (expected == null && student == null) {
            return true;
        } else if (expected == null || student == null) {
            // If only one of the two is null
            return false;
        } else if (expected.value != null && !expected.value.equals(student.value)) {
            // If values don't match
            return false;
        } else if (expected.value == null && student.value != null) {
            // If only one of the values are null
            return false;
        } else if (expected.pointers.size() != student.pointers.size()) {
            // If number of pointers is not the same
            return false;
        } else {
            // If student doesn't contain the given char, 'equals' will fail one level down
            // in one of the base cases
            for (char c : expected.pointers.keySet()) {
                boolean result = equals(expected.pointers.get(c), student.pointers.get(c));
                if (!result) {
                    return false;
                }
            }
            return true;
        }
    }

    protected static MockNode node() {
        return new MockNode();
    }

    protected static MockNode node(String value) {
        return new MockNode(value);
    }

    protected static class MockNode {
        public Map<Character, MockNode> pointers;
        public String value;

        public MockNode() {
            this(null);
        }

        public MockNode(String value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        public MockNode branch(char c, MockNode child) {
            this.pointers.put(c, child);
            return this;
        }
    }

    @Test(timeout = 3000)
    public void stressTest() {
        // Should contain 30 characters
        char[] symbols = "abcdefghijklmnopqrstuvwxyz!@#$".toCharArray();
        long i = 0;
        for (char a : symbols) {
            for (char b : symbols) {
                for (char c : symbols) {
                    for (char d : symbols) {
                        Character[] word = new Character[]{a, b, c, d};
                        STUDENT.insert(new AlphabeticString(word), "" + i);
                        i += 1;
                    }
                }
            }
        }

        for (char a : symbols) {
            for (char b : symbols) {
                assertTrue(STUDENT.findPrefix(new AlphabeticString(new Character[]{a, b})));
            }
        }

        i = 0;
        for (char a : symbols) {
            for (char b : symbols) {
                for (char c : symbols) {
                    for (char d : symbols) {
                        Character[] word = new Character[]{a, b, c, d};
                        assertEquals("" + i,  STUDENT.find(new AlphabeticString(word)));
                        i += 1;
                    }
                }
            }
        }
    }

    /**
     * Converts a String into an AlphabeticString
     */
    private static AlphabeticString a(String s) {
        return new AlphabeticString(s);
    }

    /**
     * Checks if the trie contains the word and the expected value, and that all prefixes of
     * the word exist in the trie.
     */
    private static boolean containsPath(HashTrieMap<Character, AlphabeticString, String> trie, String word, String expectedValue) {
        AlphabeticString key = a(word);

        boolean valueCorrect = expectedValue.equals(trie.find(key));
        boolean fullWordIsPrefix = trie.findPrefix(key);
        boolean invalidWordDoesNotExist = trie.find(a(word + "$")) == null;

        if (!valueCorrect || !fullWordIsPrefix || !invalidWordDoesNotExist) {
            return false;
        }

        return allPrefixesExist(trie, word);
    }

    /**
     * Checks if the trie contains the word, and that all prefixes of the word exist in the trie.
     *
     * Assumes that the expected value is word.toUpperCase().
     */
    private static boolean containsPath(HashTrieMap<Character, AlphabeticString, String> trie, String word) {
        return containsPath(trie, word, word.toUpperCase());
    }

    /**
     * Returns true if all prefixes of a word exist in the trie.
     *
     * That is, if we do `trie.insert(new AlphabeticString("dog"), "some-value")`, this method
     * would check to see if "dog", "do", "d", and "" are all prefixes of the trie.
     */
    private static boolean allPrefixesExist(HashTrieMap<Character, AlphabeticString, String> trie, String word) {
        String accum = "";
        for (char c : word.toCharArray()) {
            accum += c;
            if (!trie.findPrefix(a(accum))) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsAllPaths(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            if (!containsPath(trie, word)) {
                return false;
            }
        }
        return true;
    }

    private static boolean doesNotContainAll(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            if (trie.find(a(word)) != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsAllPrefixes(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            if (!trie.findPrefix(a(word))) {
                return false;
            }
        }
        return true;
    }

    private static boolean doesNotContainAllPrefixes(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            if (trie.findPrefix(a(word))) {
                return false;
            }
        }
        return true;
    }

    private static void addAll(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            trie.insert(a(word), word.toUpperCase());
        }
    }

    protected <T> T getField(Object o, String fieldName) {
        try {
            Field field = o.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object f = field.get(o);
            return (T) f;
        } catch (Exception var6) {
            try {
                Field field = o.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object f = field.get(o);
                return (T) f;
            } catch (Exception var5) {
                return null;
            }
        }
    }
}
