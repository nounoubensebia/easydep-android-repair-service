package com.example.nouno.easydep_repairservice;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nouno on 23/05/2017.
 */

public class SnackBarUtils {
    public static Snackbar makeSnackBar (Context context,View view1)
    {
        Snackbar snackbar = Snackbar.make(view1, R.string.login_failed, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(view1.getResources().getColor(R.color.white));
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(view1.getResources().getColor(R.color.colorPrimary));
        tv.setMaxLines(10);
        snackbar.show();
        return snackbar;
    }
}
