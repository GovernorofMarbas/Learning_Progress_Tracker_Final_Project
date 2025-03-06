package tracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class UserTest {


    static class DummyUser extends User {
        public DummyUser(String firstName, String lastName, String email) {
            super(firstName, lastName, email);
        }

        @Override
        public String getRole() {
            return "dummy";
        }
    }


    @Test
    public void testValidUserCreation() {
        DummyUser user = new DummyUser("John", "Doe", "john.doe@example.com");
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    // Тест: проверка, что имя не может быть null
    @Test
    public void testFirstNameCannotBeNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DummyUser(null, "Doe", "john.doe@example.com");
        });
        assertEquals("First name cannot be null or empty", exception.getMessage());
    }

    // Тест: проверка, что имя не может быть пустым или состоять только из пробелов
    @Test
    public void testFirstNameCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DummyUser("   ", "Doe", "john.doe@example.com");
        });
        assertEquals("First name cannot be null or empty", exception.getMessage());
    }

    // Тест: проверка, что фамилия не может быть null
    @Test
    public void testLastNameCannotBeNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DummyUser("John", null, "john.doe@example.com");
        });
        assertEquals("Last name cannot be null or empty", exception.getMessage());
    }

    // Тест: проверка, что фамилия не может быть пустой или состоять только из пробелов
    @Test
    public void testLastNameCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DummyUser("John", "   ", "john.doe@example.com");
        });
        assertEquals("Last name cannot be null or empty", exception.getMessage());
    }

    // Тест: проверка, что email не может быть null
    @Test
    public void testEmailCannotBeNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DummyUser("John", "Doe", null);
        });
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    // Тест: проверка, что email не может быть пустым или состоять только из пробелов
    @Test
    public void testEmailCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DummyUser("John", "Doe", "   ");
        });
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    // Тест: проверка, что email имеет корректный формат
    @Test
    public void testInvalidEmailFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DummyUser("John", "Doe", "john.doeexample.com");
        });
        assertEquals("Invalid email format", exception.getMessage());
    }

    // Тест: проверка, что email корректно обрезается (trim)
    @Test
    public void testEmailIsTrimmed() {
        DummyUser user = new DummyUser("John", "Doe", "  john.doe@example.com  ");
        assertEquals("john.doe@example.com", user.getEmail());
    }
}
