package com.dartmouth.kd.devents;


import android.app.Activity;
import android.app.AlertDialog;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;



public class DialogFragment extends android.app.DialogFragment {

 public static final int DIALOG_PHOTO= 1;



 private static final String DIALOG_KEY = "dialog_id";
 public static DialogFragment newInstance(int dialog_id) {
  DialogFragment f1 = new DialogFragment();
  Bundle args = new Bundle();
  args.putInt(DIALOG_KEY, dialog_id);
  f1.setArguments(args);
  return f1;
 }

 @Override
 public Dialog onCreateDialog(Bundle savedInstanceState) {
  int dialog_id = getArguments().getInt(DIALOG_KEY);
  final Activity parent = getActivity();
  switch (dialog_id) {
    case DIALOG_PHOTO:

            AlertDialog.Builder builder = new AlertDialog.Builder(parent);
            builder.setTitle(R.string.ui_profile_pic_Gallerychoose);

            builder.setItems(R.array.ui_profile_photo_selection,
                    new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int item) {

                      ((UserProfile) parent).onPhotoPickerItemSelected(item);
                     }
                    });
            return builder.create();


   default:
    return null;
  }
 }
}


