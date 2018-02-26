package edu.wpi.meetingbuddy.meetingbuddy

import android.graphics.Color
import android.graphics.RectF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_find_availability.*
import org.json.JSONObject

class FindAvailabilityActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_availability)

        val groupSchedules = loadSchedulesFromMultipleUsers()
        val userSchedule = loadUserSchedule()

        if (groupSchedules.size > 0)
        {
            when3meet.mergeCalendars(groupSchedules)
        }
        if (userSchedule.length > 0)
        {
            when3meet.setCells(userSchedule)
        }

        when3meet.removeCalendarConflicts()
        when3meet.updateCells()
    }

    fun loadSchedulesFromMultipleUsers() : ArrayList<String>
    {
        val schedules = ArrayList<String>()

        //Load a bunch of JSON strings from the database

        return schedules
    }

    fun loadUserSchedule() : String
    {
        val schedule = ""

        //Load a single JSON strings from the database

        return schedule
    }

    override fun onDestroy() {
        super.onDestroy()

        val userAvailability : JSONObject = when3meet.getCellsJSON()
        saveUserAvailability(userAvailability)
    }

    fun saveUserAvailability(json : JSONObject)
    {
        val userAvailabilityStr = json.toString()

        //Upload to server
    }
}
