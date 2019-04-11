package com.example.cristianion.nexthr.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Day;
import com.example.cristianion.nexthr.Models.Holiday;
import com.example.cristianion.nexthr.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Day> days;
    private int lastPosition = -1;


    public HolidaysAdapter(Context context,ArrayList<Day> days){
        this.context = context;
        this.days = days;

    }
    public ArrayList<Day> getSelectedDays(){
        ArrayList<Day> selectedDays = new ArrayList<>();
        for (Day day : days){
            if(day.selected){
                selectedDays.add(day);
            }
        }
        return selectedDays;
    }

    @NonNull
    @Override
    public HolidaysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.day_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final Day current = days.get(i);
        setAnimation(viewHolder.itemView,i);
        viewHolder.dayNumber.setText(current.day);
        viewHolder.dayText.setText(current.dayText);
        if(!current.status.isEmpty()){
            viewHolder.status.setText(current.status);
            viewHolder.dayView.setBackgroundResource(R.drawable.border_unselected);
        } else if(!current.freeDay){
            viewHolder.dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Day newDay = days.get(i);
                    if(newDay.selected){
                        viewHolder.dayView.setBackgroundResource(R.drawable.border_unselected);
                        newDay.selected = false;
                    } else {
                        viewHolder.dayView.setBackgroundResource(R.drawable.border);
                        newDay.selected = true;
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        View dayView;
        TextView dayNumber;
        TextView dayText;
        TextView status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayView = itemView.findViewById(R.id.day_layout);
            dayNumber = itemView.findViewById(R.id.dayNumeric);
            dayText = itemView.findViewById(R.id.dayText);
            status = itemView.findViewById(R.id.status);
        }
    }
    private void setAnimation(View view,int position){
        if(lastPosition < position){
            ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            anim.setDuration(new Random().nextInt(501));
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

}
