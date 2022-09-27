import java.util.Iterator;
import java.util.function.Function;

public class LinearProbingHashTable extends AbstractHashTable {
    /**
     * When an instance is created it has the following default capacity
     */
    public static int DEFAULT_CAPACITY = 16;
    public static double MAX_LOAD_FACTOR = 0.5;
    public static double MIN_LOAD_FACTOR = 0.125;
    private Node[] table;

    public LinearProbingHashTable() {
        table = new Node[DEFAULT_CAPACITY];
    }

    public LinearProbingHashTable(Function<Integer, Integer> hashFunction) {
        super(hashFunction);
        table = new Node[DEFAULT_CAPACITY];
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void swap(HashTable other) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean add(int value) {
        if (contains(value)) {
            return false;
        }

        int index = getIndex(value);
        while (table[index] != null && !table[index].isRemoved()) {
            index = nextIndex(index);
        }
        table[index] = new Node(value);
        ++size;

        if (checkResize()) {
            resize(table.length * 2);
        }
        return true;
    }

    private boolean checkResize() {
        return ((double)size / table.length) >= MAX_LOAD_FACTOR;
    }

    private void resize(int newCapacity) {
        Node[] oldTable = table;
        table = new Node[newCapacity];
        size = 0;
        for (Node node : oldTable) {
            if (node != null && !node.isRemoved()) {
                add(node.getValue());
            }
        }
    }

    @Override
    public boolean remove(int value) {
        int index = getIndex(value);
        while (table[index] != null) {
            if (table[index].getValue() == value) {
                removeValue(index);
                return true;
            }
            index = nextIndex(index);
        }

        return false;
    }

    private boolean checkShrink() {
        return ((double)size / table.length) <= MIN_LOAD_FACTOR;
    }

    private void removeValue(int index) {
        table[index].remove();
        --size;
        if (checkShrink()) {
            resize(table.length / 2);
        }
    }

    @Override
    public boolean contains(int value) {
        int index = getIndex(value);
        while (table[index] != null) {
            if (!table[index].isRemoved() && table[index].getValue() == value) {
                return true;
            }
            index = nextIndex(index);
        }
        return false;
    }

    private int getIndex(int value) {
        return hash.apply(value) % table.length;
    }

    private int nextIndex(int index) {
        return (index + 1) % table.length;
    }

    @Override
    public Iterator<Integer> iterator() {
        throw new UnsupportedOperationException("not implemented");
    }

    private class Node {
        private int value;

        /**
         * Flag is true when the int value was removed from the hashTable and false otherwise
         */
        private boolean isRemoved;

        public Node(int value) {
            reset(value);
        }

        /**
         * @return the value stored in the node
         */
        public int getValue() {
            return value;
        }

        /**
         * Sets the held value to the passed one
         * Sets isDeleted to false
         *
         * @param value the value to which the node is set
         */
        public void reset(int value) {
            this.value = value;
            isRemoved = false;
        }

        /**
         * @return true if the int value was removed from the hashTable and false otherwise
         */
        public boolean isRemoved() {
            return isRemoved;
        }

        /**
         * sets isRemoved to true
         */
        public void remove() {
            isRemoved = true;
        }
    }
}
