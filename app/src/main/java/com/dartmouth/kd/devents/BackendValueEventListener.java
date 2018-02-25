package com.dartmouth.kd.devents;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

/**
 * Created by Xing-Dong Yang on 2/12/2018.
 */

public class BackendValueEventListener implements ValueEventListener {

    public void onDataChange(DataSnapshot dataSnapshot){
        if(dataSnapshot.hasChildren()) {
            Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
            Iterator<DataSnapshot>  iterator = iterable.iterator();
            DataSnapshot snapshot = iterator.next();
            snapshot.getRef().removeValue();
        }
    }

    public void onCancelled (DatabaseError databaseError){}
}
