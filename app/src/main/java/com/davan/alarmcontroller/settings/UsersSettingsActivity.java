package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.davan.alarmcontroller.R;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UsersSettingsActivity extends AppCompatActivity {
    private static final String TAG = UsersSettingsActivity.class.getSimpleName();

    ListView listView ;
    ArrayAdapter<String> adapter;
    HashMap<String,String> users = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        readUsers();
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        ArrayList<String> entries = new ArrayList<String>();

        Iterator it = users.keySet().iterator();
        while (it.hasNext())
        {
            entries.add((String) it.next());
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, entries);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(view);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
                String password = users.get(itemValue);
                try {
                    String[] passwords = password.split(":");
                    viewUser(view, itemValue, passwords[0], passwords[1], Boolean.parseBoolean(passwords[2]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    viewUser(view, itemValue, password, "", false);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_help:
                // User chose the "help" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void viewUser(View view, String user, String password, String pin, boolean defaultUser )
    {
        Log.d(getLocalClassName(), "viewUser");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater=this.getLayoutInflater();
        //this is what I did to added the layout to the alert dialog
        View layout=inflater.inflate(R.layout.user,null);

        final EditText userInput=(EditText)layout.findViewById(R.id.newUserField);
        final EditText passwordInput=(EditText)layout.findViewById(R.id.newPasswordField);
        final EditText pinInput=(EditText)layout.findViewById(R.id.newPinField);
        final CheckBox defaultUserInput= (CheckBox)layout.findViewById(R.id.defaultUserCheckBox);
        userInput.setText(user);
        passwordInput.setText(password);
        pinInput.setText(pin);
        defaultUserInput.setChecked(defaultUser);

        alert.setView(layout);

        alert.setTitle("Handler user");
        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String user = userInput.getText().toString();
                if( users.containsKey(user))
                {
                    Log.d(TAG, "Deleting user" + user);
                    users.remove(user);
                    adapter.remove(user);
                    adapter.notifyDataSetChanged();
                    storeUsers();

                }
            }
        });

        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton)
            {

            }
        });
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String password = passwordInput.getText().toString();
                String user = userInput.getText().toString();
                String pin = pinInput.getText().toString();
                boolean defaultUser = defaultUserInput.isChecked();

                Log.d(TAG, "User:" + user);
                String passwords = password + ":" + pin + ":" + String.valueOf(defaultUser);
                if (!users.containsKey(user)) {
                    users.put(user, passwords);
                    adapter.add(user);
                    adapter.notifyDataSetChanged();
                } else {
                    users.put(user, passwords);
                }
                storeUsers();
            }
        });
        alert.show();

    }

    public void addUser(View view)
    {
        Log.d(getLocalClassName(), "addUser");
        viewUser(view,"","", "", false);
    }

    public void readUsers()
    {
        SharedPreferences prefs = getSharedPreferences("com.davan.alarmcontroller.users", 0);
        for( Map.Entry entry : prefs.getAll().entrySet() ) {
            users.put(entry.getKey().toString(), entry.getValue().toString());
        }
    }
    public void storeUsers()
    {
        SharedPreferences.Editor editor = getSharedPreferences("com.davan.alarmcontroller.users", 0).edit();
        for( Map.Entry entry : users.entrySet() ) {
            Log.d(TAG, "Key" + entry.getKey().toString() + " Value: " + entry.getValue().toString());
            editor.putString(entry.getKey().toString(), entry.getValue().toString());
        }
        editor.commit();

    }
}