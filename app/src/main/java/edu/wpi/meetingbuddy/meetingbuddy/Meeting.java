package edu.wpi.meetingbuddy.meetingbuddy;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Anh on 2/17/2018.
 */

public class Meeting {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mAttended;
    //List of accounts - attendants
    //Account - teacher

    public Meeting() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean hasAttended() {
        return mAttended;
    }

    public void setAttended(boolean attended) {
        mAttended = attended;
    }
}
