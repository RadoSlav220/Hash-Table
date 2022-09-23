import static org.junit.jupiter.api.Assertions.*;

class SeparateChainingHashTableTest extends HashTableTest<SeparateChainingHashTable> {

    @Override
    protected SeparateChainingHashTable createTable() {
        return new SeparateChainingHashTable();
    }

}