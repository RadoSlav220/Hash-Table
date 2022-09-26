import java.util.Iterator;
import java.util.function.Function;
import java.util.LinkedList;

public class SeparateChainingHashTable extends AbstractHashTable {
    
	public static int DEFAULT_CAPACITY = 16;
	public static double MAX_LOAD_FACTOR = 0.75;
    public static double MIN_LOAD_FACTOR = 0.125;
    
	private LinkedList<Integer>[] table;
	private int size;
	
	@SuppressWarnings("unchecked")
	public SeparateChainingHashTable() {
		table = (LinkedList<Integer>[]) new Object[DEFAULT_CAPACITY];
		hash = HashFunctions::FNVhash;
    }

	@SuppressWarnings("unchecked")
	public SeparateChainingHashTable(Function<Integer, Integer> hashFunction) {
        super(hashFunction);
        table = (LinkedList<Integer>[]) new Object[DEFAULT_CAPACITY];
    }

    @SuppressWarnings("unchecked")
	@Override
    public void clear() {
    	LinkedList<Integer>[] temp;
    	try {
    		temp = (LinkedList<Integer>[]) new Object[table.length];
    		table = temp;
    	} catch (OutOfMemoryError e) {
    		for (int i=0; i<table.length; ++i) {
    			table[i] = null;
    		}
    	}
    	size = 0;
    }

    @Override
    public void swap(HashTable other) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean add(int value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean remove(int value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean contains(int value) {
    	LinkedList<Integer> bucket = table[hash.apply(value) % table.length];
    	for (int i : bucket) {
    		if (i == value) {
    			return true;
    		}
    	}
    	return false;
    }

    @Override
    public Iterator<Integer> iterator() {
        throw new UnsupportedOperationException("not implemented");
    }
    
    @SuppressWarnings("unchecked")
	private void resize(int newCapacity) {
    	LinkedList<Integer>[] newTable = (LinkedList<Integer>[]) new Object[newCapacity];
    	//for (int i : this) {
    	//	
    	//}
    }
    
    private boolean checkResize() {
    	return (double) size / table.length >= MAX_LOAD_FACTOR;
    }
    
    private boolean checkShrink() {
        return ((double)size / table.length) <= MIN_LOAD_FACTOR;
    }
}
