import java.util.Iterator;
import java.util.function.Function;
import java.util.LinkedList;

public class SeparateChainingHashTable extends AbstractHashTable {
    
	public static double MAX_LOAD_FACTOR = 0.75;
    public static double MIN_LOAD_FACTOR = 0.25;
    
	private Bucket[] table;
	
	public SeparateChainingHashTable() {
		table = new Bucket[DEFAULT_CAPACITY];
		for (int i=0; i<DEFAULT_CAPACITY; ++i) {
			table[i] = new Bucket();
		}
		hash = HashFunctions::FNVhash;
    }

	public SeparateChainingHashTable(Function<Integer, Integer> hashFunction) {
        super(hashFunction);
        table = new Bucket[DEFAULT_CAPACITY];
        for (int i=0; i<DEFAULT_CAPACITY; ++i) {
			table[i] = new Bucket();
		}
	}

    @Override
    public void clear() {
    	Bucket[] temp;
    	try {
    		temp = new Bucket[table.length];
    		for (int i=0; i<table.length; ++i) {
    			temp[i] = new Bucket();
    		}
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
    	if (other == null) {
    		throw new IllegalArgumentException("Null argument passed");
    	}
    	else if (other instanceof SeparateChainingHashTable otherSC) {
    		Bucket[] tempRef = this.table;
    		this.table = otherSC.table;
    		otherSC.table = tempRef;
    		
    		int tempSize = this.size;
    		this.size = otherSC.size;
    		otherSC.size = tempSize;
    		
    		Function<Integer, Integer> tempHash = this.hash;
    		this.hash = otherSC.hash;
    		otherSC.hash = tempHash;
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
    	if (!this.contains(value)) {
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
    	Bucket bucket = table[hash.apply(value) % table.length];
    	return bucket.contains(value); 
    }

    @Override
    public Iterator<Integer> iterator() {
        throw new UnsupportedOperationException("not implemented");
    }

	/**
	 * @return Returns a reverse iterator, iterating the elements of the hashmap in reverse order
	 */
	@Override
	public ReverseIterator reverseIterator() {
		return null;
	}

	private void resize(int newCapacity) {
    	Bucket[] newTable = new Bucket[newCapacity];
    	for (int i=0; i<newCapacity; ++i) {
    		newTable[i] = new Bucket();
    	}
    	for (Bucket bucket : this.table) {
    		for (int element : bucket.getList()) {
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
    
    private class Bucket {
    	private LinkedList<Integer> bucket;
    	
    	public Bucket() {
    		bucket = new LinkedList<Integer>();
    	}
    	
    	public LinkedList<Integer> getList() {
    		return bucket;
    	}
    	
    	public boolean add(Integer newElement) {
    		return bucket.add(newElement);
    	}
    	
    	public boolean remove(Integer element) {
    		return bucket.remove(element);
    	}
    	
    	public boolean contains(Integer element) {
    		return bucket.contains(element);
    	}
    }
}
