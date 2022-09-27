import java.util.function.Function;

public abstract class AbstractHashTable implements HashTable {
	public static final int RESIZE_FACTOR = 2;
	public static final int DEFAULT_CAPACITY = 16;
	protected int size;
    protected Function<Integer, Integer> hash;

    public AbstractHashTable() {
        size = 0;
        hash = HashFunctions::customHash;
    }

    /**
     *
     * @param hashFunction expects a function that returns a non-negative number
     */
    public AbstractHashTable(Function<Integer, Integer> hashFunction) {
        size = 0;
        hash = hashFunction;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean empty() {
        return size == 0;
    }

}
