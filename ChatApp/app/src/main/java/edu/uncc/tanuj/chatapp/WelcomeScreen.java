package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {
User loggedInUser;
    TextView welcomeLabel;
    EditText status;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ImageButton setProfilePic;
    ImageView profilePic;
    final int SELECT_IMAGE = 1234;
    Uri selectedImage,downloadUrl;
    boolean profilePicFlag=false;
    Button next;
    String statusText="no status";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        setProfilePic= (ImageButton) findViewById(R.id.setProfilePic);
        profilePic= (ImageView) findViewById(R.id.profilePic);
        setProfilePic.setOnClickListener(this);
        welcomeLabel= (TextView) findViewById(R.id.WelcomeLabel);
        status= (EditText) findViewById(R.id.status);
        next= (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();
        DatabaseReference query=myRef.child("Users").child(auth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedInUser=dataSnapshot.getValue(User.class);
                welcomeLabel.setText("Welcome "+loggedInUser.getFirstname()+" "+loggedInUser.getLastname());
                loggedInUser.setProfilepicUri("default-profile.png");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    loggedInUser.setProfilepicUri(selectedImage.getLastPathSegment());
                    profilePicFlag=true;
                    profilePic.setBackgroundColor(Color.parseColor("#ffffff"));
                    profilePic.setImageURI(selectedImage);
                    storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://chatapp-7fe3c.appspot.com");
                    //Log.d("YO",storageRef.child("images").getBucket());
                    StorageReference imagesRef=storageRef.child("images/"+selectedImage.getLastPathSegment());
                    UploadTask uploadTask= imagesRef.putFile(selectedImage);

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
    case R.id.setProfilePic:
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"),SELECT_IMAGE);
        break;
    case R.id.next:
        if(!status.getText().toString().equalsIgnoreCase("")) {
            statusText = status.getText().toString();
        }
        loggedInUser.setStatus(statusText);
        myRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(loggedInUser);
        Intent in = new Intent(WelcomeScreen.this,HomeScreen.class);
        startActivity(in);
        break;
}
    }
}
