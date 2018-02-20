package edu.wpi.meetingbuddy.meetingbuddy;

import org.json.JSONObject;

/**
 * Created by jtgaulin on 2/20/18.
 */

public class Attendance {
    private int aid;
    private int accountID;
    private int meetingID;
    private String status;


    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        return obj;
    }

    public void fromJSON(String json){
        //Parse from string
        JSONObject obj = new JSONObject();
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
