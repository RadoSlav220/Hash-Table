import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SeparateChainingHashTableTest extends HashTableTest<SeparateChainingHashTable> {

    @Override
    protected SeparateChainingHashTable createTable() {
        return new SeparateChainingHashTable();
    }

    @Override
    protected SeparateChainingHashTable createTable(Function<Integer, Integer> hashFunction) {
        return new SeparateChainingHashTable(hashFunction);
    }

    @Test
    void swap_withLinearProbing_swapsContents() {
        //Arrange
        AbstractHashTable table1 = new LinearProbingHashTable();
        SeparateChainingHashTable table2 = new SeparateChainingHashTable();

        for (int i = 0; i < 10; i++) {
            table1.add(i);
            table2.add(i + 10);
        }

        //Act
        table2.swap(table1);

        //Assert
        for (int i = 0; i < 10; i++) {
            assertTrue(table1.contains(i + 10), "The separate chaining table should contain: " + (i + 10));
            assertTrue(table2.contains(i), "The linear probing table should contain: " + i);
            assertFalse(table1.contains(i), "The separate chaining table should not contain: " + i);
            assertFalse(table2.contains(i + 10), "The linear probing table should not contain: " + (i + 10));
        }
    }

    @Test
    void swap_withLinearProbingWithDifferentContentSize_swapsSizesAndContents() {
        //Arrange
        AbstractHashTable table1 = new LinearProbingHashTable();
        SeparateChainingHashTable table2 = new SeparateChainingHashTable();

        for (int i = 0; i < 10; i++) {
            table1.add(i);
        }
        table2.add(10);

        //Act
        table2.swap(table1);

        //Assert
        for (int i = 0; i < 10; i++) {
            assertTrue(table2.contains(i), "The linear probing table should contain: " + i);
            assertFalse(table1.contains(i), "The separate chaining table should not contain: " + i);
        }
        assertTrue(table1.contains(10));
        assertEquals(table1.size(), 1);
        assertEquals(table2.size(), 10);
    }


}