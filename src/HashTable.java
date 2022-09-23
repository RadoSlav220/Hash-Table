import java.util.Iterator;

public interface HashTable {

    int size();
    boolean empty();
    void clear();
    void swap(HashTable other);
    void add(int value);
    void remove(int value);
    boolean contains(int value);
    Iterator<Integer> iterator();

}
