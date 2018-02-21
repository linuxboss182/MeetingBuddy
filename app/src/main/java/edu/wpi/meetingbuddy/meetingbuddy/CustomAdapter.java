package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Deepak kolhe on 2/7/2017.
 */

public class CustomAdapter extends ArrayAdapter<Account> {

    Context context;
    int layoutResourceId;
    List<Account> data=null;


    public CustomAdapter(Context context, int resource, List<Account> objects) {
        super(context, resource, objects);

        this.layoutResourceId = resource;
        this.context = context;
        this.data = objects;
    }

    static class DataHolder
    {
        //ImageView ivFlag;
        TextView userName;

    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DataHolder holder =null;

        if(convertView==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            convertView = inflater.inflate(layoutResourceId,parent,false);

            holder = new DataHolder();
            //holder.ivFlag = (ImageView)convertView.findViewById(R.id.ivCountry);
            holder.userName = (TextView)convertView.findViewById(R.id.userNameTV);

            convertView.setTag(holder);
        }
        else
        {
            holder = (DataHolder)convertView.getTag();
        }

        Account dataItem = data.get(position);
        holder.userName.setText(dataItem.getUsername());
        //holder.ivFlag.setImageResource(dataItem.resIdThumbnail);

        return convertView;
    }
}
