package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailMessage extends AppCompatActivity {
Message msgObj;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    TextView sender,subject,messageText,msgImg;
    ImageView attachedSenderImage;
    User user;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);
        msgObj= (Message) getIntent().getSerializableExtra(HomeScreen.MESSAGE);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef=database.getReference();
        storage=FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://chatapp-7fe3c.appspot.com");
sender= (TextView) findViewById(R.id.SenderDetails);
        subject= (TextView) findViewById(R.id.MSGsubject);
        messageText= (TextView) findViewById(R.id.MessageHolder);
        msgImg= (TextView) findViewById(R.id.attachedSenderText);
        attachedSenderImage= (ImageView) findViewById(R.id.attachedSenderImage);
        sender.setText(msgObj.getSender());
        subject.setText(msgObj.getMsgSub());
        messageText.setText(msgObj.getMessageText());
        if(msgObj.getMsgType().equalsIgnoreCase("multimedia")){
            StorageReference imagesRef=storageRef.child("images/"+msgObj.getImageUri());

            msgImg.setVisibility(View.INVISIBLE);
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(imagesRef)
                    .into(attachedSenderImage);
        }
        getSender();
    }
public void getSender(){
    Query query=dbRef.child("Users");
    query.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if(dataSnapshot.getValue(User.class).getEmail().equalsIgnoreCase(msgObj.getSender())){
                user=dataSnapshot.getValue(User.class);
            }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailmsg, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.backToInbox:
                Intent in=new Intent(DetailMessage.this,HomeScreen.class);
                startActivity(in);
                break;
            case R.id.reply:

                Intent replyIntent=new Intent(DetailMessage.this,ComposeMessage.class);
                replyIntent.putExtra(ContactsActivity.RECEIVER,user);
                replyIntent.putExtra(ContactsActivity.MESSAGER,"fromDetailsActivity");
                startActivity(replyIntent);
                break;


        }
        return(super.onOptionsItemSelected(item));
    }
}
