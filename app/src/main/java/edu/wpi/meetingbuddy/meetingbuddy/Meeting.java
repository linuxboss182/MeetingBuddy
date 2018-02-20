package edu.wpi.meetingbuddy.meetingbuddy;

import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Anh on 2/17/2018.
 */

public class Meeting {

    private int meetingID;
    private int organizer;
    private String time;
    private String date;
    private String place;
    private float longitude;
    private float latitude;
    private int classSize;
    private int attendanceID;


    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        return obj;
    }

    public void fromJSON(String json){
        //Parse from string
        JSONObject obj = new JSONObject();
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

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
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
