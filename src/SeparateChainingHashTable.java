import java.util.Iterator;
import java.util.function.Function;
import java.util.LinkedList;

public class SeparateChainingHashTable extends AbstractHashTable {
    
	public static double MAX_LOAD_FACTOR = 0.75;
    public static double MIN_LOAD_FACTOR = 0.25;
    
	private Bucket[] table;
	
	public SeparateChainingHashTable() {
		this(HashFunctions::FNVhash);
    }

	public SeparateChainingHashTable(Function<Integer, Integer> hashFunction) {
        super(hashFunction);
        table = getBuckets(DEFAULT_CAPACITY);
	}
	
    @Override
    public void clear() {
    	Bucket[] temp;
    	try {
    		temp = getBuckets(table.length);
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
    		throw new IllegalArgumentException("Null argument passed.");
    	}
    	else if (other instanceof SeparateChainingHashTable otherSC) {
    		swap(otherSC);
    	}
    	else {
    		swapContent(other);
    	}
    }
    
    private void swap(SeparateChainingHashTable other) {
    	Bucket[] tempRef = this.table;
		this.table = other.table;
		other.table = tempRef;
		
		int tempSize = this.size;
		this.size = other.size;
		other.size = tempSize;
		
		Function<Integer, Integer> tempHash = this.hash;
		this.hash = other.hash;
		other.hash = tempHash;
    }
    
    private void swapContent(HashTable other) {
        SeparateChainingHashTable temp = new SeparateChainingHashTable();

        for (Integer integer : other) {
            temp.add(integer);
        }

        other.clear();
        for (Integer integer : this) {
            other.add(integer);
        }

        swap(temp);
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
    	return table[hash.apply(value) % table.length].contains(value); 
    }

    @Override
    public Iterator<Integer> iterator() {
        throw new UnsupportedOperationException("not implemented");
    }

	/**
	 * @return Returns a reverse iterator, iterating the elements of the hashmap in reverse order.
	 */
	@Override
	public ReverseIterator reverseIterator() {
		return null;
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
	
	private Bucket[] getBuckets(int capacity) {
		Bucket[] buckets = new Bucket[capacity];
        for (int i=0; i<capacity; ++i) {
			buckets[i] = new Bucket();
		}
        return buckets;
	}
	
	private void resize(int newCapacity) {
    	Bucket[] newTable = getBuckets(newCapacity); 
    	
    	//-------------------------------------------//
    	
    	//This cycle can be replaced by the commented one below when iterator is written//
    	//Bucket::getList() will no longer be needed.
    	for (Bucket bucket : this.table) {
    		for (int element : bucket.getList()) {
    			newTable[hash.apply(element) % newCapacity].add(element);
    		}
    	}
    	//------------------------------------------//
    	
    	//for (Integer i : this) {
    	//	newTable[hash.apply(i) % newCapacity].add(i);
    	//}
    	
    	//------------------------------------------//
    	table = newTable;
    }
    
    private boolean checkResize() {
    	return (double) size / table.length >= MAX_LOAD_FACTOR;
    }
    
    private boolean checkShrink() {
        return ((double)size / table.length) <= MIN_LOAD_FACTOR;
    }
}
