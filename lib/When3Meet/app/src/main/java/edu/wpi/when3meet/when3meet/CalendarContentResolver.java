package edu.wpi.when3meet.when3meet;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jfakult on 2/24/18.
 */

public class CalendarContentResolver {
	public static final String[] FIELDS = {
			CalendarContract.Calendars.NAME,
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
			CalendarContract.Calendars.CALENDAR_COLOR,
			CalendarContract.Calendars.VISIBLE
	};
	
	public static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
	private Context context;
	
	ContentResolver contentResolver;
	Set<String> calendars = new HashSet<String>();
	
	public  CalendarContentResolver(Context ctx) {
		context = ctx;
		contentResolver = ctx.getContentResolver();
	}
	
	public JSONArray getCalendars(int startTime, int endTime)
	{
		JSONArray events = new JSONArray();
		
		long lastSunday = 0;
		long thisSunday = 0;
		
		Calendar c=Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		lastSunday = c.getTimeInMillis();
		c.add(Calendar.DATE,7);
		thisSunday = c.getTimeInMillis();
		
		Cursor cur = null;
		ContentResolver cr = contentResolver;
		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		
		Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(eventsUriBuilder, lastSunday);
		ContentUris.appendId(eventsUriBuilder, thisSunday);
		
		Uri eventsUri = eventsUriBuilder.build();
		
		final String[] projection = { CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME,
				CalendarContract.Calendars.OWNER_ACCOUNT, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };
		
		String[] proj = new String[] { CalendarContract.Events._ID, CalendarContract.Events.DTSTART,
				CalendarContract.Events.DTEND, CalendarContract.Events.RRULE, CalendarContract.Events.TITLE };
		
		try
		{
			cur = cr.query(CalendarContract.Events.CONTENT_URI, proj, null, null, null);
			//cur = contentResolver.query(eventsUri, new String[] {CalendarContract.Instances.DTSTART, CalendarContract.Instances.TITLE}, CalendarContract.Instances.DTSTART + " >= " + lastSunday + " and " + CalendarContract.Instances.DTSTART + " <= " + thisSunday + " and " + CalendarContract.Instances.VISIBLE + " = 1", null, CalendarContract.Instances.DTSTART + " ASC");
		} catch(SecurityException e){}
		
		while (cur.moveToNext())
		{
			JSONObject event = new JSONObject();
			
			long eventID = cur.getLong(0);
			long beginVal = cur.getLong(1);
			
			if (beginVal < lastSunday) continue;
			
			long endVal = cur.getLong(2);
			String title = cur.getString(4);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(beginVal);
			
			double st = getHour(calendar);
			calendar.setTimeInMillis(endVal);
			double et = getHour(calendar);
			
			if (st == 0 && et == 0) continue;
			if (st < startTime || et > endTime){ Log.d("time", st + " " + et); continue;}
			
			try
			{
				event.put("title", title);
				event.put("startTime", st);
				event.put("endTime", et);
				event.put("day", calendar.get(Calendar.DAY_OF_WEEK)-1);
				
				events.put(event);
			}
			catch (Exception e){}
		}
		
		return events;
	}
	
	double getHour(Calendar ca)
	{
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		int minute = ca.get(Calendar.MINUTE);
		
		return hour + Math.floor(((minute *2) / 30.0)) / 2;
	}
}