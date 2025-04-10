package QuestionAnswerSystem;
/**
 * Staff class represents a single staff member.
 */
public class Staff {
    private int staffId;
    private String name;
    private String email;
    private String role;
    private String department;

    public Staff(int staffId, String name, String email, String role, String department) {
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
