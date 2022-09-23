import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class HashTableTest<T extends HashTable> {
    private T table;
    protected abstract T createTable();

    @BeforeEach
    public void setUp() {
        table = createTable();
    }
    
}