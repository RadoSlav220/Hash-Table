import java.util.Iterator;
import java.util.function.Function;

public class LinearProbingHashTable extends AbstractHashTable {
    public LinearProbingHashTable() {
        throw new UnsupportedOperationException("not implemented");
    }

    public LinearProbingHashTable(Function<Integer, Integer> hashFunction) {
        throw new UnsupportedOperationException("not implemented");
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
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean remove(int value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean contains(int value) {
        throw new UnsupportedOperationException("not implemented");
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
