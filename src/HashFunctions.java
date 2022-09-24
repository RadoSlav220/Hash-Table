
public class HashFunctions {
	public static int customHash(int x) {
		x = ((x >>> 16) ^ x) * 0x45d9f3b;
	    x = ((x >>> 16) ^ x) * 0x45d9f3b;
	    x = (x >>> 16) ^ x;
	    
	    return x >= 0 ? x : x ^ 0x80000000;
	}
	
	private static final int FNV_32_INIT = 0x811c9dc5;
    private static final int FNV_32_PRIME = 0x01000193;

    public static int FNVhash(int key) {
    	int[] arr = {key & 0x000000ff, 
    				 key & 0x0000ff00,
    				 key & 0x00ff0000,
    				 key & 0xff000000};
        
    	int rv = FNV_32_INIT;
        for(int i = 0; i < arr.length; i++) {
            rv ^= arr[i];
            rv *= FNV_32_PRIME;
        }
        return rv >= 0 ? rv : rv ^ 0x80000000;
    }
	
	public static int idHash(int x) {
		return x;
	}
}
