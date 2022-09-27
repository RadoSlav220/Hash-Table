import java.util.Iterator;
import java.util.function.Function;
import java.util.LinkedList;

public class SeparateChainingHashTable extends AbstractHashTable implements Iterable {
    
	public static double MAX_LOAD_FACTOR = 0.75;
    public static double MIN_LOAD_FACTOR = 0.25;
    
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
    	if (other instanceof SeparateChainingHashTable) {
    		LinkedList<Integer>[] tempRef = this.table;
    		this.table = ((SeparateChainingHashTable) other).table;
    		((SeparateChainingHashTable) other).table = tempRef;
    		
    		int tempSize = ((SeparateChainingHashTable) other).size;
    		this.size = tempSize;
    		((SeparateChainingHashTable) other).size = tempSize;
    		
    		Function<Integer, Integer> tempHash = this.hash;
    		this.hash = ((SeparateChainingHashTable) other).hash;
    		((SeparateChainingHashTable) other).hash = tempHash;
    	}
    	else {
    		//to be written
    	}
    }

    @Override
    public boolean add(int value) {
        if (this.contains(value)) {
        	return false;
        }
        table[hash.apply(value) % table.length].add(value);
        ++size;
        if (checkResize()) {
        	resize(table.length * RESIZE_FACTOR);
        }
        return true;
    }

    @Override
    public boolean remove(int value) {
    	if (this.contains(value)) {
        	return false;
        }
    	table[hash.apply(value) % table.length].remove(value);
    	--size;
    	if (checkShrink()) {
    		resize(table.length / RESIZE_FACTOR);
    	}
    	return true;
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
    	for (LinkedList<Integer> bucket : this.table) {
    		for (int element : bucket) {
    			newTable[hash.apply(element) % newCapacity].add(element);
    		}
    	}
    	table = newTable;
    }
    
    private boolean checkResize() {
    	return (double) size / table.length >= MAX_LOAD_FACTOR;
    }
    
    private boolean checkShrink() {
        return ((double)size / table.length) <= MIN_LOAD_FACTOR;
    }
}
