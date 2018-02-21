package edu.wpi.meetingbuddy.meetingbuddy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Anh on 2/17/2018.
 */

public class Meeting implements Serializable {

    private int meetingID;
    private int organizer;
    private String name;
    private String time;
    private String date;
    private String place;
    private double longitude;
    private double latitude;
    private int classSize;
    private int attendanceID;


    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        return obj;
    }

    public void fromJSON(JSONObject json) throws JSONException {
        //Parse from json
        this.meetingID = json.getInt("meetingID");
        this.organizer = json.getInt("organizer");
        this.name = json.getString("name");
        this.time = json.getString("time");
        this.date = json.getString("date");
        this.place = json.getString("place");
        this.longitude = json.getDouble("longitude");
        this.latitude = json.getDouble("latitude");
        this.classSize = json.getInt("classSize");
        this.attendanceID = json.getInt("aid");

        System.out.println(name);

    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public int getOrganizer() {
        return organizer;
    }

    public void setOrganizer(int organizer) {
        this.organizer = organizer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public int getClassSize() {
        return classSize;
    }

    public void setClassSize(int classSize) {
        this.classSize = classSize;
    }

    public int getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(int attendanceID) {
        this.attendanceID = attendanceID;
    }
}
