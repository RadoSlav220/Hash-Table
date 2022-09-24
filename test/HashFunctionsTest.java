import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class HashFunctionsTest {
	
	@Test
	void customHash_EqualElements_ShouldHaveSameHash() {
		assertEquals(HashFunctions.customHash(-44), HashFunctions.customHash(-44));		
	}
	
	@Test
	void customHash_DifferentElements_ShouldHaveDifferentHashes() {
		assertNotEquals(HashFunctions.customHash(100), HashFunctions.customHash(0));
	}
	
	@Test
	void FNVhash_EqualElements_ShouldHaveSameHash() {
		assertEquals(HashFunctions.FNVhash(12), HashFunctions.FNVhash(12));		
	}

	@Test
	void FNVhash_DifferentElements_ShouldHaveDifferentHashes() {
		assertNotEquals(HashFunctions.FNVhash(5), HashFunctions.FNVhash(-2783));
	}
	
	@Test
	void idHash_EqualElements_ShouldHaveSameHash() {
		assertEquals(HashFunctions.idHash(12), HashFunctions.idHash(12));
	}
	
	@Test
	void idHash_DifferentElements_ShouldHaveDifferentHashes() {
		assertNotEquals(HashFunctions.idHash(4), HashFunctions.idHash(5));
	}
}
