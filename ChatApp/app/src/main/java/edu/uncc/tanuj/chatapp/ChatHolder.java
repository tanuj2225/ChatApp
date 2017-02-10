package edu.uncc.tanuj.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by vinay on 11/18/2016.
 */

public class ChatHolder extends RecyclerView.ViewHolder {
    TextView sender_name,sender_time,message_subject;
    ImageView delete;
    LinearLayout layout;
    public ChatHolder(View itemView) {
        super(itemView);
        sender_name= (TextView) itemView.findViewById(R.id.Sender_name);
        sender_time= (TextView) itemView.findViewById(R.id.Sender_time);
        message_subject= (TextView) itemView.findViewById(R.id.message_subject);
        delete= (ImageView) itemView.findViewById(R.id.delete);
        layout= (LinearLayout) itemView.findViewById(R.id.chatLayout);
    }
}
