package application;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StaffTest {

    private static StaffManager manager;

    @BeforeAll
    public static void setup() throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();
        manager = new StaffManager(db);
    }

    @Test
    public void testAddAndDeleteStaff() throws SQLException {
        User staff = new User("teststaff", "Test@12345", "teststaff@email.com", "staff");

        // Add staff if it doesn't exist
        if (!manager.exists(staff.getUserName())) {
            manager.addStaff(staff);
        }

        // Check that it exists
        ArrayList<User> allStaff = manager.getAllStaff();
        boolean exists = allStaff.stream()
                .anyMatch(u -> u.getUserName().equals("teststaff"));
        assertTrue(exists);

        // Delete staff
        manager.deleteStaff("teststaff");

        // Ensure it's deleted
        boolean stillExists = manager.getAllStaff().stream()
                .anyMatch(u -> u.getUserName().equals("teststaff"));
        assertFalse(stillExists);
    }
}
