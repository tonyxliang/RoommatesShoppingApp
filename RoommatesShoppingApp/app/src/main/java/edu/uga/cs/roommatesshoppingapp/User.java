package edu.uga.cs.roommatesshoppingapp;

/**
 * This POJO class represents a user
 * email, fullName, and address
 *
 * Tony Liang
 * Sadiq Salewala
 */

public class User {

    private String email;
    private String fullName;
    private String address;

    public User() {
        this.email = null;
        this.fullName = null;
        this.address = null;
    }

    public User(String email, String fullName, String address) {
        this.email = email;
        this.fullName = fullName;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
