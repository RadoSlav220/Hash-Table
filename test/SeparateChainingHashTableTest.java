import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class SeparateChainingHashTableTest extends HashTableTest<SeparateChainingHashTable> {

    @Override
    protected SeparateChainingHashTable createTable() {
        return new SeparateChainingHashTable();
    }

    @Override
    protected SeparateChainingHashTable createTable(Function<Integer, Integer> hashFunction) {
        return new SeparateChainingHashTable(hashFunction);
    }

}