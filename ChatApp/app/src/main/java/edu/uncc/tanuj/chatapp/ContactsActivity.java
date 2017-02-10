package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements ContactAdapter.LinkData{
RecyclerView contactsView;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    ArrayList<User> usersList;
    ContactAdapter adapter;
    TextView contactsLabel;

    public static final String RECEIVER="receiver";
    public static final String CONTACT="contact";
    public static final String MESSAGER="fromContactsActivity";
    EditText userSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactsLabel= (TextView) findViewById(R.id.contactsLabel);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef=database.getReference();
        contactsView= (RecyclerView) findViewById(R.id.contactsView);
        usersList=new ArrayList<>();
        userSearch= (EditText) findViewById(R.id.search);
        adapter=new ContactAdapter(usersList,this);
        contactsView.setAdapter(adapter);

        contactsView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(ContactsActivity.this, LinearLayoutManager.VERTICAL, false);
        contactsView.setLayoutManager(horizontalLayoutManagaer);
        adapter.notifyDataSetChanged();
        getContactsList();
        Log.d("user",usersList.toString());
        userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

  public void getContactsList(){
      Query query=dbRef.child("Users");
      query.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user=dataSnapshot.getValue(User.class);
              if(!user.getEmail().equalsIgnoreCase(mAuth.getCurrentUser().getEmail())){
                  usersList.add(user);
                  Log.d("user",usersList.toString());
                  contactsLabel.setText(usersList.size()+" users");
                  adapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.actionbar_contacts, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.contacts:
            Intent in=new Intent(ContactsActivity.this,HomeScreen.class);
            startActivity(in);
            break;
        case R.id.editProfile:
            Intent editIntent=new Intent(ContactsActivity.this,UpdateProfile.class);
            startActivity(editIntent);
            break;
        case R.id.logout:
            mAuth.signOut();
            Intent intent=new Intent(ContactsActivity.this,MainActivity.class);
            startActivity(intent);
            break;

    }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void sendReceiverData(User user) {
        //Toast.makeText(this, "pos:"+user.toString(), Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(ContactsActivity.this,ComposeMessage.class);
        intent.putExtra(RECEIVER,user);
        intent.putExtra(MESSAGER,"fromContactsActivity");
        startActivity(intent);
    }

    @Override
    public void sendContactData(User user) {
        Intent intent=new Intent(ContactsActivity.this,ViewContact.class);
        intent.putExtra(CONTACT,user);
        startActivity(intent);
    }
}
