package id.co.bcaf.solvr.model.account;

public class UserEmployee {
    private int userId;
    private String name;
    private String nip;
    private String department;

    public UserEmployee() {
    }

    public UserEmployee(int userId, String name, String nip, String department) {
        this.userId = userId;
        this.name = name;
        this.nip = nip;
        this.department = department;
    }

}