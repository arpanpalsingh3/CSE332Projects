package aboveandbeyond;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.misc.BString;
import cse332.interfaces.trie.TrieMap;

import java.util.Iterator;
import java.util.Map;

public class CompressedHashTrieMap<A extends Comparable<A>, K extends BString<A>, V> extends TrieMap<A, K, V> {
    public class CompressedHashTrieNode extends TrieNode<Map<A, CompressedHashTrieNode>, CompressedHashTrieNode> {
        public CompressedHashTrieNode() {
            this(null);
        }

        public CompressedHashTrieNode(V value) {
            throw new NotYetImplementedException();
        }

        @Override
        public Iterator<Map.Entry<A, CompressedHashTrieNode>> iterator() {
            return this.pointers.entrySet().iterator();
        }
    }

    public CompressedHashTrieMap(Class<K> KClass) {
        super(KClass);
        this.root = new CompressedHashTrieNode();
    }

    @Override
    public V insert(K key, V value) {
        throw new NotYetImplementedException();
    }

    @Override
    public V find(K key) {
        throw new NotYetImplementedException();
    }

    @Override
    public boolean findPrefix(K key) {
        throw new NotYetImplementedException();
    }

    @Override
    public void delete(K key) {
        throw new NotYetImplementedException();
    }

    @Override
    public void clear() {
        throw new NotYetImplementedException();
    }
}
