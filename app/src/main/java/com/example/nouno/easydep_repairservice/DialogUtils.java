package com.example.nouno.easydep_repairservice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by nouno on 10/04/2017.
 */

public class DialogUtils {

    public static Dialog createDialog (Context context,String title, final OnButtonClickListener<String> onButtonClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);


        final EditText input = new EditText(context);
        //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)input.getLayoutParams();
        //params.setMargins(16,0,0,0);
        //input.setLayoutParams(params);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               onButtonClickListener.OnClick(input.getText().toString());
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();

    }
}