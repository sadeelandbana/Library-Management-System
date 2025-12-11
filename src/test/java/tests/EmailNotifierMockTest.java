package tests;
import com.library.model.UserAccount;
import com.library.service.EmailNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class EmailNotifierMockTest {
    private EmailNotifier mockNotifier; // mock object
    private UserAccount user;

    @BeforeEach
    void setup(){
        mockNotifier = mock(EmailNotifier.class);

        user = new UserAccount(
                "U1", "Test User", "test@example.com",
                "123", "pass", "User"
        );
    }

    @Test
    void testNotifyCalledWithCorrectArgs(){
        mockNotifier.notify(user, "Overdue Book!");

        verify(mockNotifier, times(1))
                .notify(eq(user), eq("Overdue Book!"));
    }

}