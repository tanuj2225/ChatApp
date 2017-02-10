package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    CallbackManager callbackManager;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    private static final int RC_SIGN_IN=001;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUSERID = mRootRef.child("Users");
    Button login,signup;
    EditText inputEmail, inputPassword;
    FirebaseAuth mAuth;
    String email,password;
    CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    boolean flag=false;

    ArrayList<User> userList;
    FirebaseStorage storage;
    int count=0;
    boolean fbFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        dbRef=database.getReference();
        login= (Button) findViewById(R.id.login);
        signup= (Button) findViewById(R.id.signup);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.g_signIn).setOnClickListener(this);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        userList=new ArrayList<>();
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeScreen.class));
            finish();
        }
        else{
            LoginManager.getInstance().logOut();
        }

        Log.d("UserList Size",userList.size()+"MM");
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Tanuj", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Tanuj", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Tanuj", "facebook:onError");
                // ...
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient= new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("aakash", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("aakash", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG","handle SignInResult:"+result.isSuccess());
        if(result.isSuccess()){
            GoogleSignInAccount acct=result.getSignInAccount();

            Log.d("aakash",acct.getEmail()+" "+acct.getDisplayName()+ " ");
            firebaseAuthWithGoogle(acct);
            //DatabaseReference newRef = mUSERID.push();

        }else{

        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("tanuj", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("aakash", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("aakash", "signInWithCredential authentication failed"+task.getException());

                        }
                        else{


                                User user = new User();
                                user.setFirstname(acct.getGivenName());
                                user.setLastname(acct.getFamilyName());
                                user.setEmail(acct.getEmail());
                            user.setProfilepicUri("gmailuser.png");
                                user.setUserKey(mAuth.getCurrentUser().getUid());
                                Map<String, Object> postValues = user.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/Users/" + mAuth.getCurrentUser().getUid(), postValues);
                                mRootRef.updateChildren(childUpdates);
                                Log.d("Google User", acct.getFamilyName() + "" + acct.getGivenName());
                                Intent welcomeActivity=new Intent(MainActivity.this,HomeScreen.class);
                                startActivity(welcomeActivity);
                                finish();




                        }
                    }
                });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("tanuj", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("tanuj", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("tanuj", "signInWithCredential", task.getException());

                        }

                       else{

                            GraphRequest request = GraphRequest.newMeRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {

                                        @Override
                                        public void onCompleted(JSONObject object, final GraphResponse response) {
                                            try {
                                            final String mail=response.getJSONObject().getString("email").toString();
                                            User user = new User();
                                            try {
                                            user.setFirstname(response.getJSONObject().getString("first_name").toString());
                                            user.setLastname(response.getJSONObject().getString("last_name").toString());
                                            user.setEmail(response.getJSONObject().getString("email").toString());
                                            user.setUserKey(mAuth.getCurrentUser().getUid());
                                                user.setProfilepicUri("fbuser.png");
                                            Map<String, Object> postValues = user.toMap();
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put("/Users/" + mAuth.getCurrentUser().getUid(), postValues);
                                            mRootRef.updateChildren(childUpdates);
                                            Intent welcomeActivity=new Intent(MainActivity.this,HomeScreen.class);
                                            startActivity(welcomeActivity);
                                            } catch (JSONException e) {
                                            e.printStackTrace();
                                            }
                                            } catch (JSONException e) {
                                            e.printStackTrace();
                                            }
                                            }
                                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,email,first_name,gender,last_name,link,locale,name,picture.type(large),timezone,updated_time,verified,age_range,friends");
                            request.setParameters(parameters);
                            request.executeAsync();


                        }
                    }
                });
    }



    private void signIn(){
        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                mAuth.getInstance().signOut();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.g_signIn:
                signIn();
                break;
            case R.id.login:
                email=inputEmail.getText().toString().trim();
                password=inputPassword.getText().toString().trim();
                if(CheckFieldValidator.checkField(inputEmail)&&CheckFieldValidator.checkField(inputPassword)){
                    mAuth.signInWithEmailAndPassword(inputEmail.getText().toString(),inputPassword.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {
                                // there was an error
                                if (inputPassword.length() < 6) {
                                    inputPassword.setError("Password must be six length in minimum");
                                } else {
                                    Toast.makeText(MainActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();
                                }
                            } else {

                                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                break;
            case R.id.signup:
                Intent intent=new Intent(MainActivity.this,signupActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("aakash","onConnectionFailed"+connectionResult);
    }
}
