package edu.wpi.meetingbuddy.meetingbuddy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jtgaulin on 2/20/18.
 */

public class Account implements Serializable {
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

    public void fromJSON(JSONObject json) throws JSONException {
        //Parse from json
        this.accountID = json.getInt("accountID");
        this.username = json.getString("username");
        this.password = json.getString("password");
        this.phoneNum = json.getString("phoneNum");
        this.firstName = json.getString("firstName");
        this.lastName = json.getString("lastName");
        this.schedule = json.getString("schedule");

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
