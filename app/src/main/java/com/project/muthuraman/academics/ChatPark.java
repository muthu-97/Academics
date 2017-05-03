package com.project.muthuraman.academics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatPark extends AppCompatActivity {

    private  FirebaseListAdapter<ChatMessages> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_park);
        final SharedPreferences sp = getSharedPreferences("MyAcad" , MODE_PRIVATE);
        setTitle("Group Chat");
        Toast.makeText(this, "Welcome " + sp.getString("name",""), Toast.LENGTH_LONG)
                .show();
        displayChatMessages();

        final Intent i =getIntent();
        FirebaseDatabase.getInstance()
                .getReference().child(i.getStringExtra("name")).addValueEventListener(postListener);
        // Load chat room contents
        displayChatMessages();

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fabs);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference().child(i.getStringExtra("name"))
                        .push()
                        .setValue(new ChatMessages(input.getText().toString(),
                                sp.getString("name","")
                        ));

                // Clear the input
                input.setText("");
            }
        });
    }

    private void displayChatMessages() {


        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessages>(this, ChatMessages.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(getIntent().getStringExtra("name"))) {
            @Override
            protected void populateView(View v, ChatMessages model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    public ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            displayChatMessages();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}