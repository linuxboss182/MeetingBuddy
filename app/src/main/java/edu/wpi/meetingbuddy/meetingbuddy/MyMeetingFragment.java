package edu.wpi.meetingbuddy.meetingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh on 2/17/2018.
 */

public class MyMeetingFragment extends Fragment{

    private RecyclerView mMeetingRecyclerView;
    private MeetingAdapter mAdapter;
    private Button add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_meetings, container, false);

        //Add new meeting
        add = (Button) view.findViewById(R.id.add_meeting);
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
        updateUI();
    }

    private void updateUI() {
        MeetingList meetingList = MeetingList.get(getActivity());
        List<Meeting> meetings = meetingList.getMeetings();

        if (mAdapter == null) {
            mAdapter = new MeetingAdapter(meetings);
            mMeetingRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
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
            mTitleTextView.setText(mMeeting.getTitle());
            mDateTextView.setText(mMeeting.getDate().toString());
            mSolvedImageView.setVisibility(meeting.hasAttended() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), StudentMeetingActivity.class);
            startActivity(intent);
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


}
