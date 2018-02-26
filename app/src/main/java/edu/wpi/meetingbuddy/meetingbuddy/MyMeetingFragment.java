package edu.wpi.meetingbuddy.meetingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

/**
 * Created by Anh on 2/17/2018.
 */

public class MyMeetingFragment extends Fragment{

    private RecyclerView mMeetingRecyclerView;
    private MeetingAdapter mAdapter;
    private Account account;
    private FloatingActionButton add;

    private NetworkManager networkManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_meetings, container, false);

        networkManager = ((ApplicationManager) this.getActivity().getApplication()).getNetworkManager();

        //Add new meeting
        add = view.findViewById(R.id.add_meeting);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CreateMeetingActivity.class);
                startActivity(i);
            }
        });

        //Scroll through the list of meetings
        mMeetingRecyclerView = (RecyclerView) view
                .findViewById(R.id.meeting_recycler_view);
        mMeetingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        networkManager = ((ApplicationManager) this.getActivity().getApplication()).getNetworkManager();
        updateUI();
    }

    private void updateUI() {
        networkManager.post(NetworkManager.url+"/getMyMeetings", "", new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("Failed to connect");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Lost Connection", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String responseStr = response.body().string();
                final int statusCode = response.code();
                try {

                    //Get meetings from server
                    final JSONArray jsonMeetings = new JSONArray(responseStr); //TODO FIX THIS
                    ArrayList<Meeting> meetings = new ArrayList<>();
                    for(int i=0;i<jsonMeetings.length();i++){
                        Meeting newMeeting = new Meeting();
                        newMeeting.fromJSON(jsonMeetings.getJSONObject(i));
                        meetings.add(newMeeting);
                    }

                    //update UI
                    final ArrayList<Meeting> uimeeting = meetings;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //List my meetings
                            if (mAdapter == null) {
                                mAdapter = new MeetingAdapter(uimeeting);
                                mMeetingRecyclerView.setAdapter(mAdapter);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class MeetingHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Meeting mMeeting;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public MeetingHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_meeting_item, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.meeting_name);
            mDateTextView = (TextView) itemView.findViewById(R.id.meeting_time);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.attended);
        }

        public void bind(Meeting meeting) {
            mMeeting = meeting;
            mTitleTextView.setText(mMeeting.getName());
            mDateTextView.setText(mMeeting.getDate());
            JSONObject creds = new JSONObject();
            try {
                creds.put("meetingID", mMeeting.getMeetingID());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Check if I have arrived at this meeting
            networkManager.post(NetworkManager.url+"/getAttendance", creds.toString(), new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    System.out.println("Failed to connect");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Lost Connection", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    final String responseStr = response.body().string();
                    String status = "";
                    try {
                        final JSONObject jsonRes = new JSONObject(responseStr);
                        status = jsonRes.getString("status");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String uistatus = status;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        if(uistatus.equals("arrived")){
                            mSolvedImageView.setVisibility(View.VISIBLE);
                        }else{
                            mSolvedImageView.setVisibility(View.GONE);
                        }
                        }
                    });

                }
            });
        }

        @Override
        public void onClick(View view) {

            // Determine if we are the organizer
            int organizer = mMeeting.getOrganizer();
            int accountID = account.getAccountID();

            // Open corresponding activity
            if(organizer == accountID){
                Intent intent = new Intent(getActivity(), OrganizerMeetingActivity.class);
                intent.putExtra("Meeting", mMeeting);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getActivity(), StudentMeetingActivity.class);
                intent.putExtra("Meeting", mMeeting);
                startActivity(intent);
            }
        }
    }

    private class MeetingAdapter extends RecyclerView.Adapter<MeetingHolder> {

        private List<Meeting> mMeetings;

        public MeetingAdapter(List<Meeting> meetings) {
            mMeetings = meetings;
        }

        @Override
        public MeetingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MeetingHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MeetingHolder holder, int position) {
            Meeting crime = mMeetings.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mMeetings.size();
        }
    }

    public void loadAccount(Account acc){
        this.account = acc;
    }

}
