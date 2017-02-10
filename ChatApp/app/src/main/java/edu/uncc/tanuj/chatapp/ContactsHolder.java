package edu.uncc.tanuj.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;

/**
 * Created by vinay on 11/18/2016.
 */

public class ContactsHolder extends RecyclerView.ViewHolder {
    TextView UserName,status;
    ImageView startChat;
    CircularImageView userPic;
    LinearLayout layout;

    public ContactsHolder(View itemView) {
        super(itemView);
        userPic= (CircularImageView) itemView.findViewById(R.id.user_pic);
        UserName= (TextView) itemView.findViewById(R.id.userName);
        status= (TextView) itemView.findViewById(R.id.status);
        startChat= (ImageView) itemView.findViewById(R.id.startChat);
        layout= (LinearLayout) itemView.findViewById(R.id.bindLayout);
    }
}
