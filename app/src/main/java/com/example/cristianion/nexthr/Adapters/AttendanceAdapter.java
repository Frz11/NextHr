package com.example.cristianion.nexthr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Attendance;
import com.example.cristianion.nexthr.R;

import java.util.ArrayList;

public class AttendanceAdapter extends BaseAdapter {

    private ArrayList<Integer> days;
    private Context mContext;


    public AttendanceAdapter(ArrayList<Integer> days,Context context){
        this.days = days;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Integer day = days.get(position);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.layout_attendance_item,null);
        }
        final TextView attendanceDay = convertView.findViewById(R.id.attendanceDay);
        final TextView duration = convertView.findViewById(R.id.attendanceDuration);
        final RelativeLayout rl = convertView.findViewById(R.id.relativeLayoutAux);
        attendanceDay.setText(day+"");

        return  convertView;
    }
}
