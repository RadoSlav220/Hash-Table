import static org.junit.jupiter.api.Assertions.*;

class LinearProbingHashTableTest extends HashTableTest<LinearProbingHashTable> {

    @Override
    protected LinearProbingHashTable createTable() {
        return new LinearProbingHashTable();
    }

}