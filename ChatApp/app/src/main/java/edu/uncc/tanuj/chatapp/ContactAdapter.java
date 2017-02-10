package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay on 11/18/2016.
 */

public class ContactAdapter extends RecyclerView.Adapter {
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ArrayList<User> contactsList=new ArrayList<>();
    ContactsActivity activity;
    ContactsFilter contactsFilter;
    List<User> filteredUserList;
    boolean performFilter=false;

    public ContactAdapter(ArrayList<User> contactsList, ContactsActivity activity) {
        this.contactsList = contactsList;
        this.activity = activity;
        this.filteredUserList=contactsList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout, parent, false);
        ContactsHolder contactsHolder = new ContactsHolder(v);
        return contactsHolder;
    }

    public Filter getFilter() {
        if(contactsFilter == null)
            contactsFilter = new ContactsFilter(this, contactsList);
        return contactsFilter;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final User user;
        int pos;
        pos=position;
        user=filteredUserList.get(position);
        mAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://chatapp-7fe3c.appspot.com");
        final ContactsHolder holderObj = (ContactsHolder) holder;
        holderObj.UserName.setText(user.firstname+" "+user.lastname);
        holderObj.status.setText(user.getStatus());
        StorageReference imagesRef=storageRef.child("images/"+user.getProfilepicUri());
        Glide.with(activity)
                .using(new FirebaseImageLoader())
                .load(imagesRef)
                .into(holderObj.userPic);
        holderObj.startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sendReceiverData(user);
            }
        });
        holderObj.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sendContactData(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }
    public  interface LinkData{
        void sendReceiverData(User user);
        void sendContactData(User user);
    }
}
