package com.example.test;

/**
 * Created by haxorware on 19/5/15.
 */
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class lap_gcm extends Activity{
    String  history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stc
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        Toast.makeText(getApplicationContext(), "History",
                Toast.LENGTH_LONG).show();
        DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
        Cursor AllFriends = databaseHelper.getFriends();

        AllFriends.moveToFirst();

        while (!AllFriends.isAfterLast()) {
            String id= AllFriends.getString(0);
            String Name = AllFriends.getString(1);
            AllFriends.moveToNext();
            history=history+id+"."+Name+"\n";
            Log.w("test",Name+id);

        }
        TextView body = (TextView)findViewById(R.id.body);
        body.setText(history);
       // setContentView(R.layout.history);

    }
}