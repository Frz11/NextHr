package com.example.cristianion.nexthr.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Message;
import com.example.cristianion.nexthr.R;

import java.util.List;

import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private Context context;
    private List<Message> messages;


    public MessagesAdapter(Context context,List<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from( context);
        View view = inflater.inflate(R.layout.message_item,viewGroup,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Message message =  messages.get(i);
        if (!message.from.equals(currentEmployee.id)){
            viewHolder.sender.setVisibility(View.GONE);
            viewHolder.receiver.setVisibility(View.VISIBLE);
            viewHolder.messageReceiver.setText(message.message);
            viewHolder.messageTimeReceiver.setText(message.sentAt);

        } else {
            viewHolder.sender.setVisibility(View.VISIBLE);
            viewHolder.receiver.setVisibility(View.GONE);
            viewHolder.messageSender.setText(message.message);
            viewHolder.messageTimeSender.setText(message.sentAt);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView messageSender;
        TextView messageTimeSender;
        TextView messageReceiver;
        TextView messageTimeReceiver;
        View receiver;
        View sender;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageSender = itemView.findViewById(R.id.messageSender);
            messageTimeSender = itemView.findViewById(R.id.messageTimeSender);
            messageReceiver = itemView.findViewById(R.id.messageReceiver);
            messageTimeReceiver = itemView.findViewById(R.id.messageTimeReceiver);
            receiver = itemView.findViewById(R.id.receiverLayout);
            sender = itemView.findViewById(R.id.senderLayout);

        }
    }
}
