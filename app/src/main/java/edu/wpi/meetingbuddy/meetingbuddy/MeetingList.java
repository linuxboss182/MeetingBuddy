package edu.wpi.meetingbuddy.meetingbuddy;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Anh on 2/17/2018.
 */

public class MeetingList {

    private static MeetingList meetingList;

    private List<Meeting> myList;

    public static MeetingList get(Context context) {
        if (meetingList == null) {
            meetingList = new MeetingList(context);
        }

        return meetingList;
    }

    private MeetingList(Context context) {
        myList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Meeting m = new Meeting();
//            crime.setTitle("Meeting #" + i);
//            crime.setAttended(i % 2 == 0);
            myList.add(m);
        }
    }

    public List<Meeting> getMeetings() {
        return myList;
    }

    public Meeting getMeeting(int id) {
        for (Meeting crime : myList) {
            if (crime.getMeetingID() == (id)) {
                return crime;
            }
        }

        return null;
    }


}
