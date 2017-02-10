package edu.uncc.tanuj.chatapp;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by vinay on 11/18/2016.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ArrayList<Message> msgList;
    HomeScreen activity;
    PrettyTime time;
    public ChatAdapter(ArrayList<Message> messageList, HomeScreen activity) {
        this.msgList = messageList;
        this.activity = activity;
        Log.d("message",msgList.size()+"");
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
        ChatHolder chatHolder=new ChatHolder(v);
        return chatHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatHolder holderObj=(ChatHolder)holder;
        Collections.sort(msgList, new Comparator<Message>() {
            public int compare(Message m1, Message m2) {
                return m2.getDate().compareTo(m1.getDate());
            }
        });
        final Message message= msgList.get(position);
        time=new PrettyTime();
        if(message.isRead()){
            holderObj.layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        String subject="Subject: "+message.getMsgSub();
        holderObj.sender_time.setText(time.format(message.getDate()));
        holderObj.sender_name.setText(message.getSender());
        holderObj.message_subject.setText(subject);
        holderObj.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteMessage(message);
            }
        });
        holderObj.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.detailMessage(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
    public  interface chatData{
        void deleteMessage(Message message);
        void detailMessage(Message message);
    }
}
