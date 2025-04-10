package application;

import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * StaffManager class manages CRUD operations for users with role "staff"
 */
public class StaffManager {
    private final DatabaseHelper dbHelper;

    public StaffManager(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public ArrayList<User> getAllStaff() {
        ArrayList<User> staffList = new ArrayList<>();
        try {
            ArrayList<ArrayList<String>> data = dbHelper.getTable();
            for (ArrayList<String> row : data) {
                if (row.get(4).equalsIgnoreCase("staff")) {
                    User staff = new User(row.get(1), row.get(2), row.get(3), row.get(4));
                    staffList.add(staff);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staffList;
    }

    public void addStaff(User staff) throws SQLException {
        dbHelper.register(staff);
    }

    public void deleteStaff(String username) throws SQLException {
        dbHelper.deleteUser(username);
    }

    public void updateStaff(User staff) throws SQLException {
        dbHelper.updateUser(staff);
    }

    public boolean exists(String username) {
        return dbHelper.doesUserExist(username);
    }
}
