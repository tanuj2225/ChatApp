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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComposeMessage extends AppCompatActivity implements View.OnClickListener {
TextView receiverLabel;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    EditText subj,msg;
    final int SELECT_IMAGE = 1234;
    ImageView attachImage;
    Uri selectedImage,downloadUrl;
    Message message;
    Date date;
    boolean msgFlag=false;
    FirebaseStorage storage;
    User receiver;
    String backState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef=database.getReference();
        storage = FirebaseStorage.getInstance();
        receiver= (User) getIntent().getSerializableExtra(ContactsActivity.RECEIVER);
        backState= (String) getIntent().getStringExtra(ContactsActivity.MESSAGER);
        receiverLabel= (TextView) findViewById(R.id.ReceiverDetails);
        receiverLabel.setText(receiver.getFirstname()+" "+receiver.getLastname());
        subj= (EditText) findViewById(R.id.msgSubject);
        msg= (EditText) findViewById(R.id.messageText);
        attachImage= (ImageView) findViewById(R.id.attachedImage);
        attachImage.setOnClickListener(this);
        message=new Message();
        date=new Date();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.composemsg, menu);
        return true;
    }
    public boolean checkField(TextView field){
        String fieldData=field.getText().toString();
        if(fieldData.equalsIgnoreCase("") && fieldData!=null){field.setError("Please fill this field");return false;}
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.sendMsg:
            if(checkField(subj)&&checkField(msg)){
                message.setSender(mAuth.getCurrentUser().getEmail());
                message.setMessageText(msg.getText().toString());
                message.setMsgSub(subj.getText().toString());
                message.setDate(date);
                message.setReceiver(receiver.getEmail());
                if(msgFlag) {
                    message.setMsgType("multimedia");
                }
                else{
                    message.setMsgType("text");
                    message.setImageUri("No Image Attachements");
                }
                String key = dbRef.child("Messages").push().getKey();
                message.setMsgKey(key);
                message.setSenderKey(mAuth.getCurrentUser().getUid());
                Map<String, Object> postValues = message.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Messages/" +receiver.getUserKey()+ "/" + key, postValues);
                dbRef.updateChildren(childUpdates);
                if(msgFlag) {

                storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://chatapp-7fe3c.appspot.com");
                //Log.d("YO",storageRef.child("images").getBucket());
                StorageReference imagesRef = storageRef.child("images/" + selectedImage.getLastPathSegment());
                UploadTask uploadTask = imagesRef.putFile(selectedImage);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("downloadUrl", downloadUrl + "hey");

                }
                });
                }
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
                Intent inte=new Intent(ComposeMessage.this,ContactsActivity.class);
                startActivity(inte);
                finish();
                }
            break;
        case R.id.backToContacts:
            Intent back_contacts=new Intent();
            if(backState.equalsIgnoreCase("fromContactsActivity")){
                back_contacts =new Intent(ComposeMessage.this,ContactsActivity.class);
            }
            else if(backState.equalsIgnoreCase("fromDetailsActivity")){
                back_contacts =new Intent(ComposeMessage.this,HomeScreen.class);
            }
            startActivity(back_contacts);
            finish();
            break;

    }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.attachedImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"),SELECT_IMAGE);
                break;

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    attachImage.setImageURI(selectedImage);
                    message.setImageUri(selectedImage.getLastPathSegment());
                    msgFlag=true;

                }
        }

    }
}
