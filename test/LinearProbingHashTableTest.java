import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class LinearProbingHashTableTest extends HashTableTest<LinearProbingHashTable> {

    @Override
    protected LinearProbingHashTable createTable() {
        return new LinearProbingHashTable();
    }

    @Override
    protected LinearProbingHashTable createTable(Function<Integer, Integer> hashFunction) {
        return new LinearProbingHashTable(hashFunction);
    }

}