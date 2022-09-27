import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.LinkedList;

public class SeparateChainingHashTable extends AbstractHashTable {

    public static double MAX_LOAD_FACTOR = 0.75;
    public static double MIN_LOAD_FACTOR = 0.25;

    private Bucket[] table;
    private int modCount;

    public SeparateChainingHashTable() {
        this(HashFunctions::FNVhash);
    }

    public SeparateChainingHashTable(Function<Integer, Integer> hashFunction) {
        super(hashFunction);
        table = getBuckets(DEFAULT_CAPACITY);
        modCount = 0;
    }

    @Override
    public void clear() {
        Bucket[] temp;
        try {
            temp = getBuckets(table.length);
            table = temp;
        } catch (OutOfMemoryError e) {
            for (int i = 0; i < table.length; ++i) {
                table[i] = null;
            }
        }
        modCount = 0;
        size = 0;
    }

    @Override
    public void swap(HashTable other) {
        if (other == null) {
            throw new IllegalArgumentException("Null argument passed.");
        } else if (other instanceof SeparateChainingHashTable otherSC) {
            swap(otherSC);
        } else {
            swapContent(other);
        }
    }

    private void swap(SeparateChainingHashTable other) {
        Bucket[] tempRef = this.table;
        this.table = other.table;
        other.table = tempRef;

        int tempSize = this.size;
        this.size = other.size;
        other.size = tempSize;

        Function<Integer, Integer> tempHash = this.hash;
        this.hash = other.hash;
        other.hash = tempHash;

        int tempModCnt = modCount;
        modCount = other.modCount;
        other.modCount = tempModCnt;
    }

    private void swapContent(HashTable other) {
        SeparateChainingHashTable temp = new SeparateChainingHashTable();

        for (Integer integer : other) {
            temp.add(integer);
        }

        other.clear();
        for (Integer integer : this) {
            other.add(integer);
        }

        swap(temp);
    }

    @Override
    public boolean add(int value) {
        if (this.contains(value)) {
            return false;
        }

        table[hash.apply(value) % table.length].add(value);
        ++size;
        ++modCount;

        if (checkResize()) {
            resize(table.length * RESIZE_FACTOR);
        }
        return true;
    }

    @Override
    public boolean remove(int value) {
        if (!this.contains(value)) {
            return false;
        }

        table[hash.apply(value) % table.length].remove(value);
        --size;
        ++modCount;

        if (checkShrink()) {
            resize(table.length / RESIZE_FACTOR);
        }
        return true;
    }

    @Override
    public boolean contains(int value) {
        return table[hash.apply(value) % table.length].contains(value);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new SCHashIterator();
    }

    /**
     * @return Returns a reverse iterator, iterating the elements of the hashmap in reverse order.
     */
    @Override
    public ReverseIterator reverseIterator() {
        return new SCHashReverseIterator();
    }

    private class Bucket {
        private LinkedList<Integer> bucket;

        public Bucket() {
            bucket = new LinkedList<Integer>();
        }

        public LinkedList<Integer> getList() {
            return bucket;
        }

        public boolean add(Integer newElement) {
            return bucket.add(newElement);
        }

        public boolean remove(Integer element) {
            return bucket.remove(element);
        }

        public boolean contains(Integer element) {
            return bucket.contains(element);
        }
    }

    private Bucket[] getBuckets(int capacity) {
        Bucket[] buckets = new Bucket[capacity];
        for (int i = 0; i < capacity; ++i) {
            buckets[i] = new Bucket();
        }
        return buckets;
    }

    private void resize(int newCapacity) {
        Bucket[] newTable = getBuckets(newCapacity);

        for (Integer i : this) {
            newTable[hash.apply(i) % newCapacity].add(i);
        }

        table = newTable;
    }

    private boolean checkResize() {
        return (double) size / table.length >= MAX_LOAD_FACTOR;
    }

    private boolean checkShrink() {
        return ((double) size / table.length) <= MIN_LOAD_FACTOR;
    }

    private class SCHashIterator implements Iterator<Integer> {
        int elementsBeforeIndex;
        int index;
        private Iterator<Integer> listIterator;

        Integer lastReturned;
        int lastModCount;

        public SCHashIterator() {
            lastModCount = SeparateChainingHashTable.this.modCount;
            elementsBeforeIndex = 0;
            index = -1;
            lastReturned = null;
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

            moveToNext();
            ++elementsBeforeIndex;
            lastReturned = listIterator.next();
            return lastReturned;
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
         *                               or the remove method has already been called after the last call to the next method.
         */
        @Override
        public void remove() {
            checkWhetherModCountHasNotChanged();

            if (lastReturned == null) {
                throw new IllegalStateException("Next wasn't called or remove was already called after the last next");
            }

            SeparateChainingHashTable.this.remove(lastReturned);
            --elementsBeforeIndex;

            lastModCount = SeparateChainingHashTable.this.modCount;
            lastReturned = null;
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
         * @throws NullPointerException            if the specified action is null
         * @throws ConcurrentModificationException There have been a structural modification made that
         *                                         might cause the current operation to give incorrect results.
         */
        @Override
        public void forEachRemaining(Consumer<? super Integer> action) {
            checkWhetherModCountHasNotChanged();

            while (hasNext()) {
                action.accept(next());
            }
        }

        private void checkWhetherModCountHasNotChanged() {
            if (SeparateChainingHashTable.this.modCount != lastModCount) {
                throw new ConcurrentModificationException(
                        "There have been a structural modification made that " +
                                " might cause the current operation to give incorrect results."
                );
            }
        }

        private void moveToNext() {
            if (listIterator != null && listIterator.hasNext()) {
                return;
            }
            Iterator<Integer> temp;

            for (int i = index + 1; i < table.length; i++) {
                temp = SeparateChainingHashTable.this.table[i].getList().iterator();
                if (temp.hasNext()) {
                    index = i;
                    listIterator = temp;
                    return;
                }
            }
        }
    }


    private class SCHashReverseIterator implements ReverseIterator {
        int elementsAfterIndex;
        int index;
        private Iterator<Integer> listIterator;
        Integer lastReturned;
        int lastModCount;

        public SCHashReverseIterator() {
            lastModCount = SeparateChainingHashTable.this.modCount;
            elementsAfterIndex = 0;
            index = SeparateChainingHashTable.this.table.length;
            lastReturned = null;
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
            return elementsAfterIndex < size;
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


            moveToPrevious();
            ++elementsAfterIndex;
            lastReturned = listIterator.next();
            return lastReturned;
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

            if (lastReturned == null) {
                throw new IllegalStateException("Next wasn't called or remove was already called after the last next");
            }

            SeparateChainingHashTable.this.remove(lastReturned);
            --elementsAfterIndex;

            lastModCount = SeparateChainingHashTable.this.modCount;
            lastReturned = null;
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
            if (SeparateChainingHashTable.this.modCount != lastModCount) {
                throw new ConcurrentModificationException(
                        "There have been a structural modification made that " +
                                " might cause the current operation to give incorrect results."
                );
            }
        }

        private void moveToPrevious() {
            if (listIterator != null && listIterator.hasNext()) {
                return;
            }
            Iterator<Integer> temp;

            for (int i = index - 1; i >= 0; --i) {
                temp = SeparateChainingHashTable.this.table[i].getList().iterator();
                if (temp.hasNext()) {
                    index = i;
                    listIterator = temp;
                    return;
                }
            }
        }
    }
}
