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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class signupActivity extends AppCompatActivity implements View.OnClickListener {
    EditText firstName,lastName,sEmail,cPassword,rPassword;
    Button signup,back;
    String email,password;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth auth;
    CircularImageView profilePic;
    ImageButton setProfilePic;
    Switch gender;
    User user;
    boolean profilePicFlag;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        lastName= (EditText) findViewById(R.id.lastName);
        firstName= (EditText) findViewById(R.id.firstName);
        sEmail= (EditText) findViewById(R.id.email);
        gender= (Switch) findViewById(R.id.Gender);
        cPassword= (EditText) findViewById(R.id.password);
        rPassword= (EditText) findViewById(R.id.rPassword);
        signup= (Button) findViewById(R.id.signup);
        back= (Button) findViewById(R.id.back);
        signup.setOnClickListener(this);
        back.setOnClickListener(this);
        myRef=database.getReference();
        user=new User();
        gender.setText(gender.getTextOff());
        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender.setText(gender.getTextOn());
                }
                else{
                    gender.setText(gender.getTextOff());
                }
            }
        });

    }

    public boolean passwordValidation(String cPass,String rPass){
        if(cPass.equals(rPass)){
            return true;
        }
        else{
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent in =new Intent(signupActivity.this,MainActivity.class);
                startActivity(in);
                break;
            case R.id.signup:

               if(CheckFieldValidator.checkField(lastName)&& CheckFieldValidator.checkField(sEmail)&& CheckFieldValidator.checkField(cPassword)&&CheckFieldValidator.checkField(firstName)&&CheckFieldValidator.checkField(rPassword)){
                    if(passwordValidation(cPassword.getText().toString(),rPassword.getText().toString())){
                        auth.createUserWithEmailAndPassword(sEmail.getText().toString(),cPassword.getText().toString()).addOnCompleteListener(signupActivity.this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(signupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    user.setEmail(sEmail.getText().toString());user.setFirstname(firstName.getText().toString());
                                    user.setLastname(lastName.getText().toString());
                                    //myRef.child(auth.getCurrentUser().getUid()).child("details").setValue(user);
                                    String key = myRef.child("Users").push().getKey();
                                    user.setUserKey(auth.getCurrentUser().getUid());
                                    user.setGender(gender.getText().toString());
                                        user.setProfilepicUri("default-profile.png");

                                    Map<String, Object> postValues = user.toMap();
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("/Users/" + auth.getCurrentUser().getUid(), postValues);
                                    myRef.updateChildren(childUpdates);


                                } startActivity(new Intent(signupActivity.this, WelcomeScreen.class));

                                finish();
                            }
                        });
                    }
                }

                break;


        }

    }
}
