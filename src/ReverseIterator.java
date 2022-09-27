import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public interface ReverseIterator {
    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #previous} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    boolean hasPrevious();

    /**
     * Returns the previous element in the iteration.
     *
     * @return the previous element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    Integer previous();


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
     * or the remove method has already been called after the last call to the next method.
     */
    void remove();


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
     * @throws NullPointerException if the specified action is null
     * @throws ConcurrentModificationException There have been a structural modification made that
     * might cause the current operation to give incorrect results.
     */
    void forEachRemaining(Consumer<? super Integer> action);
}
