package edu.weber.cs.w01353438.duckduckjeep.Authentication;

public class User_Details {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    //These will be used across many fragments
    public String username;
    public String userId;

}
