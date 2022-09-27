import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

public class LinearProbingHashTable extends AbstractHashTable {
    /**
     * When an instance is created it has the following default capacity
     */
    public static int DEFAULT_CAPACITY = 16;
    public static double MAX_LOAD_FACTOR = 0.5;
    public static double MIN_LOAD_FACTOR = 0.125;
    private Node[] table;
    private int modCount;

    public LinearProbingHashTable() {
        table = new Node[DEFAULT_CAPACITY];
        modCount = 0;
    }

    public LinearProbingHashTable(Function<Integer, Integer> hashFunction) {
        super(hashFunction);
        table = new Node[DEFAULT_CAPACITY];
        modCount = 0;
    }

    @Override
    public void clear() {
        table = new Node[DEFAULT_CAPACITY];
        size = 0;
    }

    @Override
    public void swap(HashTable other) {
        if (other == null) {
            throw new IllegalArgumentException("The argument is null!");
        }
        if (other instanceof LinearProbingHashTable linearOther) {
            swap(linearOther);
        }
        else {
            swapContent(other);
        }
    }

    private void swap(LinearProbingHashTable other) {
        Node[] tempArray = table;
        Function<Integer, Integer> tempFunc = hash;
        int temp = size;

        table = other.table;
        other.table = tempArray;

        hash = other.hash;
        other.hash = tempFunc;

        size = other.size;
        other.size = temp;

        temp = modCount;
        modCount = other.modCount;
        other.modCount = temp;
    }

    private void swapContent(HashTable other) {
        LinearProbingHashTable temp = new LinearProbingHashTable();

        for (Integer integer : other) {
            temp.add(integer);
        }

        other.clear();
        for (Integer integer : temp) {
            other.add(integer);
        }

        swap(temp);
    }

    @Override
    public boolean add(int value) {
        if (contains(value)) {
            return false;
        }

        int index = getIndex(value);
        while (isNodeValid(table[index])) {
            index = nextIndex(index);
        }
        table[index] = new Node(value);
        ++size;

        if (checkResize()) {
            resize(table.length * 2);
        }
        ++modCount;
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
            if (isNodeValid(node)) {
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
                ++modCount;
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
        return new LPHashIterator();
    }

    /**
     * @return Returns a reverse iterator, iterating the elements of the hashmap in reverse order
     */
    @Override
    public ReverseIterator reverseIterator() {
        return new LPHashReverseIterator();
    }

    private boolean isNodeValid(Node node) {
        return !(node == null || node.isRemoved);
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


    private class LPHashIterator implements Iterator<Integer> {
        int elementsBeforeIndex;
        int index;
        int lastReturned;
        int lastModCount;

        public LPHashIterator() {
            lastModCount = LinearProbingHashTable.this.modCount;
            elementsBeforeIndex = 0;
            index = -1;
            lastReturned = -1;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            checkWhetherModCountHasNotChanged();
            return elementsBeforeIndex < size;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Integer next() {
            checkWhetherModCountHasNotChanged();

            if (!hasNext()) {
                throw new NoSuchElementException("The iteration has no more elements");
            }

            lastReturned = index;
            moveIndexToNextElement();
            return LinearProbingHashTable.this.table[index].getValue();
        }

        /**
         * Removes from the underlying collection the last element returned
         * by this iterator (optional operation).  This method can be called
         * only once per call to {@link #next}.
         * <p>
         * The behavior of an iterator is unspecified if the underlying collection
         * is modified while the iteration is in progress in any way other than by
         * calling this method, unless an overriding class has specified a
         * concurrent modification policy.
         * <p>
         * The behavior of an iterator is unspecified if this method is called
         * after a call to the {@link #forEachRemaining forEachRemaining} method.
         *
         * @throws IllegalStateException if the {@code next} method has not yet been called,
         * or the remove method has already been called after the last call to the next method.
         */
        @Override
        public void remove() {
            checkWhetherModCountHasNotChanged();

            if (lastReturned < 0) {
                throw new IllegalStateException("Next wasn't called or remove was already called after the last next");
            }

            LinearProbingHashTable.this.remove(LinearProbingHashTable.this.table[lastReturned].getValue());
            --elementsBeforeIndex;

            lastModCount = LinearProbingHashTable.this.modCount;
            lastReturned = -1;
        }

        /**
         * Performs the given action for each remaining element until all elements
         * have been processed or the action throws an exception.  Actions are
         * performed in the order of iteration, if that order is specified.
         * Exceptions thrown by the action are relayed to the caller.
         * <p>
         * The behavior of an iterator is unspecified if the action modifies the
         * collection in any way (even by calling the {@link #remove remove} method
         * or other mutator methods of {@code Iterator} subtypes),
         * unless an overriding class has specified a concurrent modification policy.
         * <p>
         * Subsequent behavior of an iterator is unspecified if the action throws an
         * exception.
         *
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         * @throws ConcurrentModificationException There have been a structural modification made that
         * might cause the current operation to give incorrect results.
         */
        @Override
        public void forEachRemaining(Consumer<? super Integer> action) {
            checkWhetherModCountHasNotChanged();

            while (hasNext()) {
                action.accept(next());
            }
        }

        private void checkWhetherModCountHasNotChanged() {
            if (LinearProbingHashTable.this.modCount != lastModCount) {
                throw new ConcurrentModificationException(
                        "There have been a structural modification made that " +
                                " might cause the current operation to give incorrect results."
                );
            }
        }

        private void moveIndexToNextElement() {
            for (int i = index + 1; i < size; i++) {
                if (isNodeValid(LinearProbingHashTable.this.table[i])) {
                    index = i;
                    ++elementsBeforeIndex;
                    return;
                }
            }
        }
    }


    private class LPHashReverseIterator implements ReverseIterator {
        int elementsBeforeIndex;
        int index;
        int lastReturned;
        int lastModCount;

        public LPHashReverseIterator() {
            lastModCount = LinearProbingHashTable.this.modCount;
            elementsBeforeIndex = 0;
            index = -1;
            lastReturned = -1;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #previous} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasPrevious() {
            checkWhetherModCountHasNotChanged();
            return index >= 0;
        }

        /**
         * Returns the previous element in the iteration.
         *
         * @return the previous element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Integer previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException("The iteration has no previous element");
            }
            checkWhetherModCountHasNotChanged();

            lastReturned = index;

            moveIndexToPreviousElement();
            return LinearProbingHashTable.this.table[index--].getValue();
        }

        /**
         * Removes from the underlying collection the last element returned
         * by this iterator (optional operation).  This method can be called
         * only once per call to {@link #previous()}.
         * <p>
         * The behavior of an iterator is unspecified if the underlying collection
         * is modified while the iteration is in progress in any way other than by
         * calling this method, unless an overriding class has specified a
         * concurrent modification policy.
         * <p>
         * The behavior of an iterator is unspecified if this method is called
         * after a call to the {@link #forEachRemaining forEachRemaining} method.
         *
         * @throws IllegalStateException if the {@code previous} method has not yet been called,
         *                               or the remove method has already been called after the last call to the next method.
         */
        @Override
        public void remove() {
            checkWhetherModCountHasNotChanged();

            if (lastReturned < 0) {
                throw new IllegalStateException("Next wasn't called or remove was already called after the last next");
            }

            LinearProbingHashTable.this.remove(LinearProbingHashTable.this.table[lastReturned].getValue());
            --elementsBeforeIndex;

            lastModCount = LinearProbingHashTable.this.modCount;
            lastReturned = -1;
        }

        /**
         * Performs the given action for each remaining element until all elements
         * have been processed or the action throws an exception. Actions are
         * performed in the order of iteration, if that order is specified.
         * Exceptions thrown by the action are relayed to the caller.
         * <p>
         * The behavior of an iterator is unspecified if the action modifies the
         * collection in any way (even by calling the {@link #remove()} method
         * or other mutator methods of {@code Iterator} subtypes),
         * unless an overriding class has specified a concurrent modification policy.
         * <p>
         * Subsequent behavior of an iterator is unspecified if the action throws an
         * exception.
         *
         * @param action The action to be performed for each element
         * @throws NullPointerException            if the specified action is null
         * @throws ConcurrentModificationException There have been a structural modification made that
         *                                         might cause the current operation to give incorrect results.
         */
        @Override
        public void forEachRemaining(Consumer<? super Integer> action) {
            checkWhetherModCountHasNotChanged();

            while (hasPrevious()) {
                action.accept(previous());
            }
        }

        private void checkWhetherModCountHasNotChanged() {
            if (LinearProbingHashTable.this.modCount != lastModCount) {
                throw new ConcurrentModificationException(
                        "There have been a structural modification made that " +
                                " might cause the current operation to give incorrect results."
                );
            }
        }

        private void moveIndexToPreviousElement() {
            for (int i = index - 1; i >= 0; --i) {
                if (isNodeValid(LinearProbingHashTable.this.table[i])) {
                    index = i;
                    --elementsBeforeIndex;
                    return;
                }
            }
        }
    }
}


