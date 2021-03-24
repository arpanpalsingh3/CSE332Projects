package cse332.interfaces.trie;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map.Entry;

import cse332.datastructures.containers.Item;
import cse332.interfaces.misc.BString;
import cse332.interfaces.misc.Dictionary;
import cse332.interfaces.misc.SimpleIterator;
import cse332.interfaces.worklists.LIFOWorkList;
import cse332.interfaces.worklists.WorkList;
import datastructures.worklists.ArrayStack;
import datastructures.worklists.ListFIFOQueue;

/**
 * An object that maps keys (made up of characters of a bounded type) to values.
 * A TrieMap cannot contain duplicate keys; each key can map to at most one
 * value.
 *
 * TrieMaps may not contain null keys or values.
 *
 * @param <A>
 *            the type of the characters of the BString key type
 * @param <K>
 *            the type of BString keys maintained by this map
 * @param <V>
 *            the type of mapped values
 *
 * @author Adam Blank
 */
public abstract class TrieMap<A extends Comparable<A>, K extends BString<A>, V> extends Dictionary<K, V> {
    protected TrieNode<?, ?> root;

    /**
     * This variable is a hack to get the type of the key at runtime. It is
     * initialized when the first key is inserted or removed from the map.
     */
    protected Class<K> KClass;

    /**
     * The constructor for the TrieMap class must take an instance of the key
     * class. Such a variable looks like <class name>.class and is necessary
     * because Java's generics are implemented with type erasure. If you are
     * really interested in why this is necessary, please come and talk to the
     * instructor.
     *
     * @param KClass
     *            a reflection variable representing the key class for this
     *            particular instance of TrieMap
     */
    public TrieMap(Class<K> KClass) {
        this.KClass = KClass;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for which the key
     * starts with the specified key prefix.
     *
     * @param keyPrefix
     *            The prefix of a key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping whose key starts
     *         with the specified key prefix.
     * @throws IllegalArgumentException
     *             if the key is null.
     */
    public abstract boolean findPrefix(K keyPrefix);

    /**
     * This class represents a single node of the Trie. Crazy generics are
     * necessary to make the alphebetic Strings as generic as possible.
     *
     * @author Adam Blank
     *
     * @param <PType>
     *            the type of the pointers in the node
     * @param <X>
     *            the type of the node itself
     */
    protected abstract class TrieNode<PType, X extends TrieNode<PType, X>>
            implements Iterable<Entry<A, X>> {
        public PType pointers;
        public V value;

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            WorkList<Entry<A, X>> entries = new ListFIFOQueue<Entry<A, X>>();
            for (Entry<A, X> entry : this) {
                entries.add(entry);
            }

            boolean isSmall = entries.size() == 0;

            for (Entry<A, X> entry : this) {
                A idx = entry.getKey();
                X node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key
     *            key whose mapping is to be removed from the map
     * @throws IllegalArgumentException
     *             if either key is null.
     */
    public void delete(A[] key) {
        delete(keyFromLetters(key));
    }

    private class TrieMapIterator extends SimpleIterator<Item<K, V>> {
        private final WorkList<K> keys;

        public TrieMapIterator() {
            this.keys = new ListFIFOQueue<>();
            initialize(new ArrayStack<A>(), TrieMap.this.root);
        }

        @SuppressWarnings("unchecked")
        protected K makeKeyFromLIFOWorkList(LIFOWorkList<A> list) {
            A[] letters = (A[]) Array
                    .newInstance(BString.getLetterType(TrieMap.this.KClass), list.size());

            int i = letters.length - 1;
            while (list.hasWork()) {
                letters[i--] = list.next();
            }

            for (i = 0; i < letters.length; i++) {
                list.add(letters[i]);
            }

            return keyFromLetters(letters);
        }

        @SuppressWarnings("unchecked")
        private void initialize(LIFOWorkList<A> acc, TrieNode<?, ?> current) {
            if (current != null) {
                if (current.value != null) {
                    this.keys.add(makeKeyFromLIFOWorkList(acc));
                }
                for (Entry<A, ?> entry : current) {
                    if (entry != null) {
                        acc.add(entry.getKey());
                        initialize(acc, (TrieNode<?, ?>) entry.getValue());
                        acc.next();
                    }
                }
            }
        }

        @Override
        public Item<K, V> next() {
            K key = this.keys.next();
            return new Item<K, V>(key, find(key));
        }

        @Override
        public boolean hasNext() {
            return this.keys.hasWork();
        }
    }

    @Override
    public Iterator<Item<K, V>> iterator() {
        return new TrieMapIterator();
    }

    /**
     * Returns a new key instance from an array of letters instances
     *
     * @param letters
     *            the underlying array of the new key instance
     * @return a new key instance with the same letters as letters
     */
    public K keyFromLetters(A[] letters) {
        try {
            return this.KClass.getConstructor(letters.getClass())
                    .newInstance((Object) letters);
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | SecurityException
                | NoSuchMethodException e) {
            throw new RuntimeException("this should never happen!");
        }
    }
}
