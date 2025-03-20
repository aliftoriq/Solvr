package id.co.bcaf.solvr.model.account;

import java.util.Objects;

public class UserCustomer {
    private int id;
    private int customerId;
    private String address;
    private String phone;

    public UserCustomer() {
    }

    public UserCustomer(int id, int customerId, String address, String phone) {
        this.id = id;
        this.customerId = customerId;
        this.address = address;
        this.phone = phone;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserCustomer id(int id) {
        setId(id);
        return this;
    }

    public UserCustomer customerId(int customerId) {
        setCustomerId(customerId);
        return this;
    }

    public UserCustomer address(String address) {
        setAddress(address);
        return this;
    }

    public UserCustomer phone(String phone) {
        setPhone(phone);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserCustomer)) {
            return false;
        }
        UserCustomer userCustomer = (UserCustomer) o;
        return id == userCustomer.id && customerId == userCustomer.customerId
                && Objects.equals(address, userCustomer.address) && Objects.equals(phone, userCustomer.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, address, phone);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", customerId='" + getCustomerId() + "'" +
                ", address='" + getAddress() + "'" +
                ", phone='" + getPhone() + "'" +
                "}";
    }

}
