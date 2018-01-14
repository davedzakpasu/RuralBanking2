package com.cergi.ruralbanking;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Thierry on 05/02/14.
 */
public class Alerts
{
    public static void showAlert(String title, String message, Context ctx)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);

        EmptyListener pl = new EmptyListener();
        builder.setPositiveButton("OK", pl);

        AlertDialog ad = builder.create();

        ad.show();
    }

}