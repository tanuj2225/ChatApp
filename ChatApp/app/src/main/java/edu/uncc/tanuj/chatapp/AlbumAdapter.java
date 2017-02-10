package edu.uncc.tanuj.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by vinay on 11/20/2016.
 */

public class AlbumAdapter extends RecyclerView.Adapter {
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ArrayList<Album> albumList;
    ViewContact activity;
    PrettyTime time;

    public AlbumAdapter(ArrayList<Album> albumList, ViewContact activity) {
        this.albumList = albumList;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout, parent, false);
        AlbumHolder albumHolder=new AlbumHolder(v);
        return albumHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        storage=FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://chatapp-7fe3c.appspot.com");
        Collections.sort(albumList, new Comparator<Album>() {
            public int compare(Album m1, Album m2) {
                return m2.getDate().compareTo(m1.getDate());
            }
        });
        time=new PrettyTime();
        Album album=albumList.get(position);
        AlbumHolder holderObj= (AlbumHolder) holder;
        holderObj.date_pushed.setText(time.format(album.getDate()));
        StorageReference imagesRef=storageRef.child("images/"+album.getImageUri());
        Glide.with(activity)
                .using(new FirebaseImageLoader())
                .load(imagesRef)
                .into(holderObj.albumPic);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
