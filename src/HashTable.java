import java.util.Iterator;

public interface HashTable extends Iterable<Integer> {
    /**
     * @return the number of elements stored in this hash table
     */
    int size();

    /**
     * @return true if this map contains no elements
     * @implSpec Returns size() == 0
     */
    boolean empty();

    /**
     * Removes all elements from the hash table.
     * The hash table is empty after the call.
     */
    void clear();

    /**
     * Exchanges the contents of two hash tables. Performs a simple exchange of references/pointers.
     *
     * @param other the hash table with which to exchange the contents
     * @throws IllegalArgumentException if the argument is null
     */
    void swap(HashTable other);

    /**
     * Ensures that this hash table contains the specified value.
     *
     * @param value element whose presence in this hash table is to be ensured
     * @return true if this hash table changed as a result of the call
     */
    boolean add(int value);

    /**
     * Ensures that this hash table doesn't contain the specified value/
     *
     * @param value element to be removed from this hash table, if present
     * @return true if this hash table changed as a result of the call
     */
    boolean remove(int value);

    /**
     * @param value element whose presence in this hash table is to be tested
     * @return true if this hash table contains the specified element
     */
    boolean contains(int value);

    /**
     * @return Returns an iterator over the elements contained in this hash table
     */
    Iterator<Integer> iterator();
}
