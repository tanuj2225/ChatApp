package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {
EditText uFname,uLname,uStatus;
ImageView profile_pic;
    String pushToAlbum;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    User loggedInUser;
    FirebaseStorage storage;
    Uri selectedImage,downloadUrl;
    final int SELECT_IMAGE = 1234;
    StorageReference storageRef;
    StorageReference imagesRef;
    UploadTask uploadTask;
    Album album;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        uFname= (EditText) findViewById(R.id.fUName);
        uLname= (EditText) findViewById(R.id.lUName);
        uStatus= (EditText) findViewById(R.id.uStatus);
        profile_pic= (ImageView) findViewById(R.id.profile_pic);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef=database.getReference();
        profile_pic.setOnClickListener(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://chatapp-7fe3c.appspot.com");
        Query query=dbRef.child("Users").child(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedInUser=dataSnapshot.getValue(User.class);
                Log.d("USER",loggedInUser.toString());
                pushToAlbum=loggedInUser.getProfilepicUri();
                setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void setData(){
        uFname.setText(loggedInUser.getFirstname());
        uLname.setText(loggedInUser.getLastname());
        Log.d("USER",loggedInUser.toString());
        uStatus.setText(loggedInUser.getStatus());
        StorageReference imagesRef=storageRef.child("images/"+loggedInUser.getProfilepicUri());
        Glide.with(UpdateProfile.this)
                .using(new FirebaseImageLoader())
                .load(imagesRef)
                .into(profile_pic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.back:
            Intent in=new Intent(UpdateProfile.this,HomeScreen.class);
            startActivity(in);
            break;
        case R.id.update:
            if(CheckFieldValidator.checkField(uFname)&&CheckFieldValidator.checkField(uLname)) {
                if(uStatus.getText().equals("")){loggedInUser.setStatus("No Status");}
                else{
                    loggedInUser.setStatus(uStatus.getText().toString());
                }
                loggedInUser.setFirstname(uFname.getText().toString());
                loggedInUser.setLastname(uLname.getText().toString());
                dbRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(loggedInUser);
                String key = dbRef.child("Albums").push().getKey();
                album=new Album();
                album.setImageKey(key);
                album.setDate(new Date());
                album.setImageUri(pushToAlbum);
                album.setUserID(mAuth.getCurrentUser().getUid());

                Map<String, Object> postValues = album.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Albums/" + mAuth.getCurrentUser().getUid()+"/"+key, postValues);
                dbRef.updateChildren(childUpdates);

                Intent im=new Intent(UpdateProfile.this,HomeScreen.class);
                startActivity(im);
            }
            break;


    }
        return(super.onOptionsItemSelected(item));
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    loggedInUser.setProfilepicUri(selectedImage.getLastPathSegment());
                    profile_pic.setImageURI(selectedImage);
                    imagesRef=storageRef.child("images/"+loggedInUser.getProfilepicUri());
                    uploadTask= imagesRef.putFile(selectedImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("downloadUrl",downloadUrl+"hey");

                        }
                    });
                }
        }

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.profile_pic:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"),SELECT_IMAGE);
                break;
        }
    }
}
