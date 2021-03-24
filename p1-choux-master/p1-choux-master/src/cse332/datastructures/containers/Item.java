package cse332.datastructures.containers;

/**
 * Simple class to hold a piece of data and its value.
 *
 * @param <K>
 *            type of the key
 * @param <V>
 *            type of the value
 */
 // test commit 
public class Item<K, V> {
    /**
     * The key whose value we are recording.
     */
    public K key;

    /**
     * The value we are recording.
     */
    public V value;

    /**
     * Create a data count.
     *
     * @param key
     *            the key we are recording.
     * @param value
     *            the value of the key we are recording.
     */
    public Item(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Item(Item<K, V> item) {
        this(item.key, item.value);
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value + "";
    }
}
