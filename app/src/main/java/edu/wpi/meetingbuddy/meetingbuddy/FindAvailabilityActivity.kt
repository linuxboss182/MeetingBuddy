package edu.wpi.meetingbuddy.meetingbuddy

import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.squareup.okhttp.Callback
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import kotlinx.android.synthetic.main.activity_find_availability.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

private lateinit var networkManager: NetworkManager

class FindAvailabilityActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_availability)

        networkManager = (this.application as ApplicationManager).networkManager

        val groupData : Account? = savedInstanceState?.getSerializable("Account") as Account?; //Should be passed in as bundle data

        var accountId = -1;
        if (groupData != null) accountId = groupData.accountID
        post("getSchedule", "")
        post("getSchedules", accountId.toString())

        //when3meet.removeCalendarConflicts()
        //when3meet.updateCells()
    }

    fun loadSchedulesFromMultipleUsers(groupSchedules : JSONArray)
    {
        //Load a bunch of JSON strings from the database
        if (groupSchedules.length() > 0)
        {
            val schedules = ArrayList<String>()
            for (i in 0..groupSchedules.length())
            {
                schedules.add(groupSchedules.getString(i))
            }
            when3meet.mergeCalendars(schedules)
        }

        when3meet.removeCalendarConflicts()
        when3meet.updateCells()
    }

    fun loadUserSchedule(userSchedule : JSONObject)
    {
        Log.d("hello", userSchedule.length().toString() + " " + userSchedule);
        //Load a single JSON string from the database
        if (userSchedule.length() > 0)
        {
            when3meet.setCells(userSchedule.toString())
        }

        when3meet.removeCalendarConflicts()
        when3meet.updateCells()
    }

    override fun onDestroy() {
        super.onDestroy()

        val userAvailability : JSONObject = when3meet.getCellsJSON()
        post("updateSchedule", "{\"schedule\": " + userAvailability.toString() + "}")

        Log.d("Savin", "{\"schedule\": " + userAvailability.toString() + "}")
    }

    /*fun saveUserAvailability(json : JSONObject)
    {
    }*/

    fun post(url : String, data : String)
    {
        networkManager.post(NetworkManager.url + "/" + url, data,
                object : Callback {
                    override fun onFailure(request: Request, e: IOException) {
                        println("Failed to connect")
                        runOnUiThread(Runnable { Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show() })
                    }
                    @Throws(IOException::class)
                    override fun onResponse(response: Response) {
                        var responseStr = response.body().string()
                        val statusCode = response.code()
                        try {
                            //responseStr = responseStr.replace("\\\"", "\"")
                            Log.d("responseStr", responseStr + "--" + url)
                            lateinit var jsonRes : JSONObject
                            try
                            {
                                jsonRes = JSONObject(responseStr)
                            }
                            catch (e : Exception){Log.d("Error", "eroror"); return;}
                            val status = jsonRes.getString("status")
                            if (status == "success")
                            {
                                if (url == "getSchedule")
                                {
                                    Log.d("Res", jsonRes.toString());
                                    val jsonDataStr = jsonRes.getString("schedule")
                                    loadUserSchedule(JSONObject(jsonDataStr))
                                }
                                else if (url == "getSchedules")
                                {
                                    val jsonData = jsonRes.getJSONArray("schedule")
                                    loadSchedulesFromMultipleUsers(jsonData)
                                }
                                else
                                {
                                    //pass
                                }
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                })
    }
}
