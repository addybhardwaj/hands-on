import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Custom hashmap implementation.
 *
 * @author Aditya Bhardwaj
 */
public class CustomHashMap<K, V> {

    private static final int DEFAULT_BUCKET_SIZE = 100;

    private Entry<K, V>[] buckets;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public CustomHashMap() {
        this(DEFAULT_BUCKET_SIZE);
    }

    public CustomHashMap(int bucketSize) {
        this.buckets = new Entry[bucketSize];
    }

    private int getBucketIndex(K key) {
        return key.hashCode() % getBucketSize();
    }

    private int getBucketSize() {
        return buckets.length;
    }

    private void checkKey(K key) {
        if (key == null) throw new IllegalArgumentException("Key of map cannot be null");
    }

    public void put(K key, V value) {
        checkKey(key);

        //TODO locks all bucket entries
        Entry<K, V> entry = null;
        w.lock();
        try {
            int bucketIndex = getBucketIndex(key);

            entry = buckets[bucketIndex];
            if (entry == null) {
                buckets[bucketIndex] = new Entry<K, V>(key, value);
                w.unlock();
            }
        } finally {
            w.unlock();
        }

        if (entry != null) {
            //following should only lock 1 bucket level entries
            synchronized (entry) {
                boolean done = false;
                while (!done) {
                    if (entry.getKey().equals(key)) {
                        entry.setKey(key);
                        entry.setValue(value);
                        break;
                    } else if (entry.getNext() == null) {
                        entry.setNext(new Entry<K, V>(key, value));
                        break;
                    }
                    entry = entry.getNext();
                }
            }
        }
    }

    public V get(K key) {
        checkKey(key);

        Entry<K, V> entry = buckets[getBucketIndex(key)];
        while (entry != null && !key.equals(entry.getKey()))
            entry = entry.getNext();
        return entry != null ? entry.getValue() : null;
    }

    private void resize() {

    }


    public static class Entry<K, V> {
        private K key;
        private V value;
        private Entry<K, V> next;

        public Entry() {
        }

        public Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Entry<K, V> getNext() {
            return next;
        }

        public void setNext(Entry<K, V> next) {
            this.next = next;
        }
    }
}
