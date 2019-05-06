package com.example.selfchat3_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyAdapter.recItemOnLongClick {
    public static final String SP_DATA_SIZE_KEY = "data_size";
    public static final String SP_DATA_LIST_KEY = "sent_messages";

    Button button;
    EditText editText;
    RecyclerView recyclerView;

    MyAdapter myAdapter;

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button3);
        editText = (EditText) findViewById(R.id.editText2);
        recyclerView = (RecyclerView) findViewById(R.id.rec1);

        FirebaseApp.initializeApp(MainActivity.this);

        db = FirebaseFirestore.getInstance();

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        int data_size = sp.getInt(SP_DATA_SIZE_KEY, 0);

        myAdapter = new MyAdapter(data_size, sp, editor);
        myAdapter.setClickListener(this);

        recyclerView.setAdapter(myAdapter);
        if(data_size != 0 ) { myAdapter.loadData(); }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                editText.setText("");
                if(message.equals(""))
                {
                    Toast.makeText(getApplicationContext(),
                            "you can't send an empty message, oh silly!", Toast.LENGTH_LONG).show();
                    return;
                }
                myAdapter.add_message(message);
                Map<String, Object> user = new HashMap<>();
                user.put("content", message);
                user.put("timestamp", "5:30");
                user.put("id", "12");

                // Add a new document with a generated ID
                db.collection("messages")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.e("a", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("b", "Error adding document", e);
                            }
                        });
            }
        });
    }

    @Override
    public void itemLongClick(View view, final int position) {

        new AlertDialog.Builder(this)
                .setTitle("Delete the selected message?")
                .setMessage("Note that you can't restore deleted messages")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    myAdapter.delete_message(position);
                        Toast.makeText(getApplicationContext(),
                                "message was deleted successfully", Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),
                                "deleting process was aborted", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("wrote_message", editText.getText().toString());
        savedInstanceState.putStringArrayList("messages", myAdapter.data);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String wrote_message = savedInstanceState.getString("wrote_message");
        myAdapter.data = savedInstanceState.getStringArrayList("messages");
        editText.setText(wrote_message);
        myAdapter.supportConfigurationChange();
    }
}