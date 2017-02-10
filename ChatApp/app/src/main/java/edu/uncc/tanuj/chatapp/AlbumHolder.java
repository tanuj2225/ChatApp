package edu.uncc.tanuj.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vinay on 11/20/2016.
 */

public class AlbumHolder extends RecyclerView.ViewHolder {
    TextView date_pushed;
    ImageView albumPic;
    public AlbumHolder(View itemView) {
        super(itemView);
        date_pushed= (TextView) itemView.findViewById(R.id.picture_date);
        albumPic= (ImageView) itemView.findViewById(R.id.picture);
    }
}
