package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;

public class ViewContact extends AppCompatActivity {
User user;
    TextView name,status,email,gender;
    CircularImageView contact_pic;
    StorageReference storageRef;
    StorageReference imagesRef;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    ArrayList<Album> albumList;
    RecyclerView AlbumView;
    AlbumAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef=database.getReference();
        albumList=new ArrayList<>();
        setContentView(R.layout.activity_view_contact);
        user= (User) getIntent().getSerializableExtra(ContactsActivity.CONTACT);
        name= (TextView) findViewById(R.id.contactName);
        status= (TextView) findViewById(R.id.contactStatus);
        email= (TextView) findViewById(R.id.contactEmail);
        gender= (TextView) findViewById(R.id.gender);
        contact_pic= (CircularImageView) findViewById(R.id.profilepic);
        name.setText(user.getFirstname()+" "+user.getLastname());
        status.setText(user.getStatus());
        email.setText(user.getEmail());
        gender.setText(user.getGender());
        adapter=new AlbumAdapter(albumList,this);
        AlbumView= (RecyclerView) findViewById(R.id.AlbumView);
        AlbumView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getAlbums();
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(ViewContact.this, LinearLayoutManager.HORIZONTAL, false);
        AlbumView.setLayoutManager(horizontalLayoutManagaer);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://chatapp-7fe3c.appspot.com");
        StorageReference imagesRef=storageRef.child("images/"+user.getProfilepicUri());
        Glide.with(ViewContact.this)
                .using(new FirebaseImageLoader())
                .load(imagesRef)
                .into(contact_pic);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.viewcontact, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.backToContactsActivity:
            Intent in=new Intent(ViewContact.this,ContactsActivity.class);
            startActivity(in);
            break;


    }
        return(super.onOptionsItemSelected(item));
    }
    public void getAlbums(){
        Query query=dbRef.child("Albums").child(user.getUserKey());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Album album=dataSnapshot.getValue(Album.class);
                albumList.add(album);
                Log.d("album",albumList.size()+""+album.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
