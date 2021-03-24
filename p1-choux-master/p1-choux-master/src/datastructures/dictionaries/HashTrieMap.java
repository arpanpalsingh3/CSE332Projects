package datastructures.dictionaries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.misc.BString;
import cse332.interfaces.trie.TrieMap;

/**
 * See cse332/interfaces/trie/TrieMap.java
 * and cse332/interfaces/misc/Dictionary.java
 * for method specifications.
 */
public class HashTrieMap<A extends Comparable<A>, K extends BString<A>, V> extends TrieMap<A, K, V> {
    public class HashTrieNode extends TrieNode<Map<A, HashTrieNode>, HashTrieNode> {
        public HashTrieNode() {
            this(null);
        }

        public HashTrieNode(V value) {
            this.pointers = new HashMap<A, HashTrieNode>();
            this.value = value;
        }

        @Override
        public Iterator<Entry<A, HashTrieMap<A, K, V>.HashTrieNode>> iterator() {
            return pointers.entrySet().iterator();
        }
    }

    public HashTrieMap(Class<K> KClass) {
        super(KClass);
        this.root = new HashTrieNode();
    }

    @Override
    public V insert(K key, V value) {
        // checking the exception
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }
        // our current tree, starting at the root node
        HashTrieNode curRoot = (HashTrieMap<A, K, V>.HashTrieNode) root;

        // find the NODE in the tree associated with key
        for (A thisKey : key) {
            if (curRoot.pointers.get(thisKey) == null) {
                curRoot.pointers.put(thisKey, new HashTrieNode());
            }
            curRoot = curRoot.pointers.get(thisKey);
        }
        // store the old value of child
        V oldVal = curRoot.value;

        // Check if node doesn't have child
        if (curRoot.value == null) {
            // increase the size since we are gonna add new child
            size++;
        }
        // add child to node key
        curRoot.value = value;

        return oldVal;

    }

    @Override
    public V find(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        HashTrieNode curRoot = (HashTrieMap<A, K, V>.HashTrieNode) root;

        for ( A thisKey: key) {
            // if the path does not exist, return null
            if (curRoot.pointers.get(thisKey) == null) {
                return null;
            }
            curRoot = curRoot.pointers.get(thisKey);
        }
        return curRoot.value;
        }

    @Override
    public boolean findPrefix(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        HashTrieNode curRoot = (HashTrieMap<A, K, V>.HashTrieNode) root;

        for ( A thisKey: key) {
            if (curRoot.pointers.get(thisKey) == null) {
                return false;
            }
            curRoot = curRoot.pointers.get(thisKey);
        }
        return true;
    }

    @Override
    public void delete(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        if (find(key) != null) {
            // Setting up reference pointer to a Node that:
            //   1) Has a value and at least 1 key
            //   2) Has more than 1 keys
            HashTrieNode curRoot = (HashTrieMap<A, K, V>.HashTrieNode) root;
            HashTrieNode prev = curRoot;
            for (A thisKey : key) {
                if (curRoot.pointers.get(thisKey).value != null &&
                        curRoot.pointers.get(thisKey).pointers.keySet().size() >= 1) {
                    prev = curRoot.pointers.get(thisKey);
                } else if (curRoot.pointers.get(thisKey).pointers.keySet().size() > 1) {
                    prev = curRoot.pointers.get(thisKey);
                }
                curRoot = curRoot.pointers.get(thisKey);
            }

            // Checking if there's any child past the end of the path
            boolean deleteAfter = true;
            if (curRoot.pointers.keySet().size() >= 1) {
                deleteAfter = false;
            }

            // BEFORE CASES
            if (curRoot == prev) {
                curRoot.value = null;
            } else {
                // Starts setting values between prev to curr as null
                boolean startDelete = false;
                HashTrieNode deleter = (HashTrieMap<A, K, V>.HashTrieNode) root;
                for (A thisKey : key) {
                    if (deleter == prev) {
                        startDelete = true;
                    }
                    if (startDelete) {
                        deleter.pointers.get(thisKey).value = null;
                    }
                    if (deleter == curRoot) {
                        startDelete = false;
                    }
                    deleter = deleter.pointers.get(thisKey);
                }
            }

            // AFTER CASES
            if (deleteAfter) {
                HashTrieNode tracker = (HashTrieMap<A, K, V>.HashTrieNode) root;
                boolean atPosition = (tracker == prev);
                int keyCounter = 0;
                A lastKey = null;
                for (A thisKey : key) {
                    keyCounter++;
                    if (atPosition) {
                        tracker.pointers.remove(thisKey);
                        break;
                    }
                    if (tracker.pointers.get(thisKey) == prev) {
                        atPosition = true;
                    }
                    lastKey = thisKey;
                    tracker = tracker.pointers.get(thisKey);
                }
                if (keyCounter == 1) {
                    tracker = (HashTrieMap<A, K, V>.HashTrieNode) root;
                    tracker.pointers.remove(lastKey);
                }
            }
            size--;
        }
    }

    @Override
    public void clear() {
        HashTrieNode curRoot = (HashTrieMap<A, K, V>.HashTrieNode) this.root;
        curRoot.pointers.clear();
        this.size = 0;
    }
}
