import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

abstract class HashTableTest<T extends AbstractHashTable> {
    protected T table;

    protected abstract T createTable();

    protected abstract T createTable(Function<Integer, Integer> hashFunction);

    @BeforeEach
    public void setUp() {
        table = createTable();
    }

    @Test
    void constructor_whenCreated_hashTableHasZeroSize() {
        assertEquals(0, table.size(),
                "HashTable created with default constructor should have zero size!");
    }

    @Test
    void constructorTakingFunction_whenCreated_hashTableHasZeroSize() {
        assertEquals(0, (createTable(HashFunctions::customHash)).size(),
                "HashTable created with constructor taking a function should have zero size!");
    }

    @Test
    void empty_anEmptyHashTable_emptyReturnsTrue() {
        //Act, Assert
        assertTrue(table.empty(), "A hash table should be empty after creation!");
    }

    @Test
    void empty_aHashTableWithOneElement_emptyReturnsFalse() {
        //Arrange
        table.add(3);

        //Act, Assert
        assertFalse(table.empty(), "A hash table should not be empty after addition!");
    }

    @Test
    void clear_aHashTableWithOneElement_afterClearTheHashTableIsEmpty() {
        //Arrange
        int value = 3;
        table.add(value);

        //Act
        table.clear();

        //Assert
        assertTrue(table.empty(), "After clear the hash table should be empty!");
        assertFalse(table.contains(value), "After clear the hash table shouldn't contain old elements!");
    }

    @Test
    void swap_aNullArgumentPassed_itThrowsAnError() {
        //Act, Assert
        assertThrows(IllegalArgumentException.class, () -> table.swap(null), "Swap should throw an error when the argument is null!");
    }

    @Test
    void swap_emptyAndNonEmptyHashTable_swapExchangesTheirContent() {
        //Arrange
        for (int i = 0; i < 3; i++) {
            table.add(i);
        }
        int sizeBeforeSwap = table.size();
        T other = createTable();

        //Act
        table.swap(other);

        //Assert
        assertTrue(table.empty(), "After the swap the non-empty hashtable should be empty!");
        assertEquals(sizeBeforeSwap, other.size(), "The sizes should swap as well");
        for (int i = 0; i < 3; i++) {
            assertTrue(other.contains(i), "After swap the previously empty hashtable should have the content of the other one!");
        }
    }

    @Test
    void contains_emptyHashTable_itShouldNotContainFive() {
        //Act, Assert
        assertFalse(table.contains(5), "An empty array shouldn't contain 5");
    }

    @Test
    void contains_aHashTable_itShouldContainAllInsertedElements() {
        //Arrange
        for (int i = 0; i < 3; i++) {
            table.add(i);
        }

        //Assert
        for (int i = 0; i < 3; i++) {
            assertTrue(table.contains(i), "The table should contained the inserted element: " + i);
        }
    }

    @Test
    void add_ValueNotInTable_ValueAdded() {
        //Act
        boolean hasChanged = table.add(3);

        //Assert
        assertTrue(hasChanged);
        assertEquals(1, table.size());
        assertTrue(table.contains(3));
    }

    @Test
    void add_ValueInTable_NoChange() {
        //Arrange
        table.add(3);

        //Act
        boolean hasChanged = table.add(3);

        //Assert
        assertFalse(hasChanged);
        assertEquals(1, table.size());
    }

    @Test
    void add_MillionRandomNumbers_AllNumbersInHashTable() {
        //Arrange
        Set<Integer> numbers = new HashSet<>();
        Random random = new Random();

        //Act
        while (numbers.size() < 1000000) {
            int num = random.nextInt();
            numbers.add(num);
            table.add(num);
        }

        //Assert
        assertEquals(1000000, table.size());
        for (Integer i : numbers) {
            assertTrue(table.contains(i));
        }
    }

    @Test
    void remove_ValueInTable_ValueRemoved() {
        //Arrange
        table.add(3);

        //Act
        boolean hasChanged = table.remove(3);

        //Assert
        assertTrue(hasChanged);
        assertFalse(table.contains(3));
        assertTrue(table.empty());
    }

    @Test
    void remove_ValueNotInTable_NoChange() {
        //Act
        boolean hasChanged = table.remove(3);

        //Assert
        assertFalse(hasChanged);
        assertTrue(table.empty());
    }

    @Test
    void remove_AddMillionNumbersRemoveOddNumbers_TableContainsOnlyEvenNumbers() {
        //Arrange
        fill(0, 999999, 1);

        //Act
        for (int i = 1; i < 1000000; i += 2) {
            table.remove(i);
        }

        //Assert
        assertEquals(500000, table.size());
        for (int i = 0; i < 1000000; ++i) {
            boolean isEven = i % 2 == 0;
            assertEquals(isEven, table.contains(i));
        }
    }

    @Test
    void iterator_AddNumbers_IteratorGoesThroughAllOfThem() {
        //Arrange
        HashSet<Integer> stdHash = new HashSet<Integer>();
        int[] numbers = {1, 2, 3, 4, 5, 3};

        for (int i = 0; i < numbers.length; ++i) {
            stdHash.add(numbers[i]);
            table.add(numbers[i]);
        }

        //Act
        for (int i : table) {
            assertTrue(stdHash.contains(i), "The iterator should iterate through all added elements, current: " + i);
            stdHash.remove(i);
        }

        //Assert
        assertTrue(stdHash.isEmpty(), "The iterator should have iterated through all elements!");
    }

    @Test
    void iteratorRemove_CallRemoveWithoutCallingNext_ExceptionIsThrown() {
        //Arrange
        fill(1, 5, 1);
        Iterator<Integer> it = table.iterator();

        //Act, Assert
        assertThrows(IllegalStateException.class, () -> it.remove(), "Without calling next, the iterator must not be able to remove anything.");
    }

    @Test
    void iteratorRemove_RemoveANumber_SizeIsDecreased() {
        //Arrange
        fill(1, 5, 1);
        Iterator<Integer> it = table.iterator();
        it.next();

        //Act
        it.remove();

        //Assert
        assertEquals(4, table.size(), "After removal, size must be desreased.");
    }

    @Test
    void iteratorNext_callNextWhenThereIsNoNext_ExceptionIsThrown() {
        //Arrange
        fill(1, 5, 1);
        Iterator<Integer> it = table.iterator();
        for (int i = 0; i < 5; ++i) {
            it.next();
        }

        //Act, Assert
        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void iteratorHasNext_callHasNextWhenThereIsNext_ReturnTrue() {
        //Arrange
        fill(1, 5, 1);
        Iterator<Integer> it = table.iterator();
        it.next();

        //Act, Assert
        for (int i = 0; i < 4; ++i) {
            assertTrue(it.hasNext(), "There must be next!");
            it.next();
        }
    }

    @Test
    void iteratorHasNext_callHasNextWhenThereIsNoNext_ReturnFalse() {
        //Arrange
        fill(1, 5, 1);
        Iterator<Integer> it = table.iterator();
        for (int i = 0; i < 5; ++i) {
            it.next();
        }

        //Act, Assert
        assertFalse(it.hasNext());
    }


    @Test
    void reverseIterator_AddNumbers_IteratorGoesThroughAllOfThem() {
        //Arrange
        HashSet<Integer> stdHash = new HashSet<Integer>();
        int[] numbers = {1, 2, 3, 4, 5, 3};

        for (int i = 0; i < numbers.length; ++i) {
            stdHash.add(numbers[i]);
            table.add(numbers[i]);
        }

        ReverseIterator it = table.reverseIterator();
        int curr;

        //Act
        while (it.hasPrevious()) {
            curr = it.previous();
            assertTrue(stdHash.contains(curr),
                    "The iterator should iterate through all added elements, current: " + curr);
            stdHash.remove(curr);
        }
        //Assert
        assertTrue(stdHash.isEmpty(), "The iterator should have iterated through all elements!");
    }

    @Test
    void reverseIteratorRemove_CallRemoveWithoutCallingPrevious_ExceptionIsThrown() {
        //Arrange
        fill(1, 5, 1);
        ReverseIterator it = table.reverseIterator();

        //Act, Assert
        assertThrows(IllegalStateException.class, it::remove,
                "Without calling previous, the iterator must not be able to remove anything.");
    }

    @Test
    void reverseIteratorRemove_RemoveANumber_SizeIsDecreased() {
        //Arrange
        fill(1, 5, 1);
        ReverseIterator it = table.reverseIterator();
        it.previous();

        //Act
        it.remove();

        //Assert
        assertEquals(4, table.size(), "After removal, size must be decreased.");
    }

    @Test
    void reverseIteratorNext_callPreviousWhenThereIsNoPrevious_ExceptionIsThrown() {
        //Arrange
        fill(1, 5, 1);
        ReverseIterator it = table.reverseIterator();
        for (int i = 0; i < 5; ++i) {
            it.previous();
        }

        //Act, Assert
        assertThrows(NoSuchElementException.class, () -> it.previous(),
                "Call to previous when beginning is reached throws an error");
    }

    @Test
    void reverseIteratorHasPrevious_callHasPreviousWhenThereIsPrevious_ReturnTrue() {
        //Arrange
        fill(1, 5, 1);
        ReverseIterator it = table.reverseIterator();
        it.previous();

        //Act, Assert
        for (int i = 0; i < 4; ++i) {
            assertTrue(it.hasPrevious(), "There must be previous!");
            it.previous();
        }
    }

    @Test
    void reverseIteratorHasPrevious_callHasPreviousWhenThereIsNoPrevious_ReturnFalse() {
        //Arrange
        fill(1, 5, 1);
        ReverseIterator it = table.reverseIterator();
        for (int i = 0; i < 5; ++i) {
            it.previous();
        }

        //Act, Assert
        assertFalse(it.hasPrevious());
    }


    private void fill(int from, int to, int step) {
        for (int i = from; i <= to; i += step) {
            table.add(i);
        }
    }
}