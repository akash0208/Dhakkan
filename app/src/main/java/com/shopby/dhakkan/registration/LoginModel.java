package com.shopby.dhakkan.registration;

/**
 * Created by ashiq on 8/27/2017.
 */

public class LoginModel {

    private String userId;
    private String name;
    private String profilePic;
    private String email;
    private String phone;

    public LoginModel(String userId, String email, String phone) {
        this.userId = userId;
        this.email = email;
        this.phone = phone;
    }

    public LoginModel(String userId, String name, String profilePic, String email) {
        this.userId = userId;
        this.name = name;
        this.profilePic = profilePic;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
