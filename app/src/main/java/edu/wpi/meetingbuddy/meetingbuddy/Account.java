package edu.wpi.meetingbuddy.meetingbuddy;

import org.json.JSONObject;

/**
 * Created by jtgaulin on 2/20/18.
 */

public class Account {
    private int accountID;
    private String username;
    private String password;
    private String phoneNum;
    private String firstName;
    private String lastName;
    private String schedule;


    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        return obj;
    }

    public void fromJSON(String json){
        //Parse from string
        JSONObject obj = new JSONObject();
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
