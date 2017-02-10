package edu.uncc.tanuj.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class HomeScreen extends AppCompatActivity implements ChatAdapter.chatData {
    FirebaseDatabase database;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    RecyclerView chatsView;
    TextView chatsLabel,unRead;
    ArrayList<Message> messagesList;
    ChatAdapter adapter;
    int count;
    public static final String MESSAGE="message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef=database.getReference();
        chatsView= (RecyclerView) findViewById(R.id.chatsView);
        chatsLabel= (TextView) findViewById(R.id.chatLabel);
        unRead= (TextView) findViewById(R.id.unRead);
        messagesList=new ArrayList<>();
        adapter=new ChatAdapter(messagesList,this);
        chatsView.setAdapter(adapter);
        Paint paint = new Paint();
        paint.setStrokeWidth(15);
        count=0;
        paint.setColor(Color.parseColor("#eeeeee"));
        chatsLabel.setText(messagesList.size()+" messages in total");
        unRead.setText(count+" unread");
        chatsView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).paint(paint).build());
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(HomeScreen.this, LinearLayoutManager.VERTICAL, false);
        chatsView.setLayoutManager(horizontalLayoutManager);
        adapter.notifyDataSetChanged();
        getChats();
    }
    public void getChats(){
        Query query=dbRef.child("Messages").child(mAuth.getCurrentUser().getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message msgObj=dataSnapshot.getValue(Message.class);
                messagesList.add(msgObj);
                if(!msgObj.isRead()){
                    count=count+1;
                }
                Log.d("listSize",messagesList.size()+"");
                chatsLabel.setText(messagesList.size()+" messages in total");
                unRead.setText(count+" unread");
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.contacts:
            Intent in=new Intent(HomeScreen.this,ContactsActivity.class);
            startActivity(in);
            break;
        case R.id.editProfile:
Intent editIntent=new Intent(HomeScreen.this,UpdateProfile.class);
            startActivity(editIntent);
            break;
        case R.id.logout:
mAuth.signOut();
            Intent intent=new Intent(HomeScreen.this,MainActivity.class);
            startActivity(intent);
            break;

    }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void deleteMessage(Message message) {
        dbRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(message.getMsgKey()).removeValue();
        messagesList.remove(message);
        if(!message.isRead()){
            count=count-1;
            unRead.setText(count+" unread");
        }
        adapter.notifyDataSetChanged();
        chatsLabel.setText(messagesList.size()+" messages in total");
        Toast.makeText(this, "Message Deleted from "+message.getSender(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void detailMessage(Message message) {
        Intent detailMessage=new Intent(HomeScreen.this,DetailMessage.class);
    if(!message.isRead()){
        message.setRead(true);
        dbRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(message.getMsgKey()).setValue(message);
    }
        detailMessage.putExtra(MESSAGE,message);
startActivity(detailMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
