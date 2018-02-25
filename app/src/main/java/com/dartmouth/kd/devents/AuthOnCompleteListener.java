package com.dartmouth.kd.devents;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AuthOnCompleteListener implements OnCompleteListener {
    Context context;
    public AuthOnCompleteListener(Context context){
        this.context = context;
    }

    public void onComplete(Task task){
        if(task.isSuccessful())
            Utils.showActivity(context, MainActivity.class);
        else
            Utils.showDialog(context, "Error!", task.getException().getMessage());
    }
}