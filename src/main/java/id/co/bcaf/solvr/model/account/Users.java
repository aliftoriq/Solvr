package id.co.bcaf.solvr.model.account;

public class Users {
    private int id;
    private String name;
    private String username;
    private String role;
    private String password;

    public Users() {
    }

    public Users(int id, String name, String username, String role, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Users getUsers() {
        return this;
    }
}