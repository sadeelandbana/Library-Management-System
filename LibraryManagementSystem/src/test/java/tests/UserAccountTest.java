
package tests;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.library.model.UserAccount;

public class UserAccountTest {

    @Test
    public void testConstructorAndGetters() {
        UserAccount user = new UserAccount(
            "U001",
            "Bana Aloul",
            "bana@example.com",
            "0599000000",
            "secret123",
            "User"
        );

        assertEquals("U001", user.getId());
        assertEquals("Bana Aloul", user.getName());
        assertEquals("bana@example.com", user.getEmail());
        assertEquals("0599000000", user.getPhone());
        assertEquals("secret123", user.getPassword());
        assertEquals("User", user.getRole());
        assertEquals(0.0, user.getFineBalance(), 0.001, "Fine balance should start at 0.0");
    }

    @Test
    public void testAddFine() {
        UserAccount user = new UserAccount("U002", "Sedil", "sedil@example.com", "0599888888", "pwd", "User");

        user.addFine(10.0);
        assertEquals(10.0, user.getFineBalance(), 0.001, "Fine balance should be 10.0 after first fine");

        user.addFine(20.0);
        assertEquals(30.0, user.getFineBalance(), 0.001, "Fine balance should be 30.0 after adding another fine");
    }
}