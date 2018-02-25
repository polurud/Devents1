package com.dartmouth.kd.devents;

import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


public class ListViewChildEventListener implements ChildEventListener {
    ArrayAdapter<String> arrayAdapter;
    public ListViewChildEventListener(ArrayAdapter<String> adapter){
        arrayAdapter = adapter;
    }

    public void onChildAdded(DataSnapshot dataSnapshot, String s){
        String line = (String)dataSnapshot.child("title").getValue();
        arrayAdapter.add(line);
    }



    public void onChildRemoved(DataSnapshot dataSnapshot){
        String line = (String)dataSnapshot.child("title").getValue();
        arrayAdapter.remove(line);

    }
    public void onChildMoved(DataSnapshot dataSnapshot, String s){}
    public void onChildChanged(DataSnapshot dataSnapshot, String s){}
    public void onCancelled(DatabaseError error){}
}
