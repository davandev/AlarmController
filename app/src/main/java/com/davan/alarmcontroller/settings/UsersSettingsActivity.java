package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 **/
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UsersSettingsActivity extends AppCompatActivity
{
    private static final String TAG = UsersSettingsActivity.class.getSimpleName();

    private ListView listView ;
    private ArrayAdapter<String> adapter;
    private final HashMap<String,String> users = new HashMap<>();

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
        ArrayList<String> entries = new ArrayList<>();

        Iterator it = users.keySet().iterator();
        while (it.hasNext())
        {
            entries.add((String) it.next());
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, entries);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(view);
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

        String itemValue = (String) listView.getItemAtPosition(position);
        String password = users.get(itemValue);
        try {
            String[] passwords = password.split(":");
            viewUser(view, itemValue, passwords[0], passwords[1], passwords[2],Boolean.parseBoolean(passwords[3]));
        } catch (ArrayIndexOutOfBoundsException e) {
            viewUser(view, itemValue, password, "","", false);
        }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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

    /**
     * View the settings of an existing user
     * @param view
     * @param user name of the user
     * @param password user password
     * @param pin pincode
     * @param chatId telegram chatid
     * @param defaultUser determine if the user is default user
     */
    private void viewUser(View view, String user, String password, String pin, String chatId, boolean defaultUser)
    {
        Log.d(getLocalClassName(), "viewUser");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater=this.getLayoutInflater();
        View layout=inflater.inflate(R.layout.user, null);

        final EditText userInput=(EditText)layout.findViewById(R.id.newUserField);
        final EditText passwordInput=(EditText)layout.findViewById(R.id.newPasswordField);
        final EditText pinInput=(EditText)layout.findViewById(R.id.newPinField);
        final EditText telegramChatId=(EditText)layout.findViewById(R.id.telegramChatId);

        final CheckBox defaultUserInput= (CheckBox)layout.findViewById(R.id.defaultUserCheckBox);
        userInput.setText(user);
        passwordInput.setText(password);
        pinInput.setText(pin);
        telegramChatId.setText(chatId);
        defaultUserInput.setChecked(defaultUser);

        alert.setView(layout);

        if (user.compareTo("") == 0)
        {
            alert.setTitle(R.string.pref_title_user_add);
        }
        else
        {
            alert.setTitle(R.string.pref_title_user_edit);
        }
        alert.setNeutralButton(R.string.pref_button_delete_user, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String user = userInput.getText().toString();
                if( users.containsKey(user))
                {
                    Log.d(TAG, "Deleting user:" + user);
                    users.remove(user);
                    adapter.remove(user);
                    adapter.notifyDataSetChanged();
                    storeUsers();
                }
            }
        });

        alert.setNegativeButton(R.string.pref_title_close_user, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {

            }
        });
        alert.setPositiveButton(R.string.pref_button_save_user, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String password = passwordInput.getText().toString();
                String user = userInput.getText().toString();
                String pin = pinInput.getText().toString();
                String chatId = telegramChatId.getText().toString();
                boolean defaultUser = defaultUserInput.isChecked();

                Log.d(TAG, "User:" + user);
                String passwords = password + ":" + pin + ":" + chatId + ":" + String.valueOf(defaultUser);
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

    /**
     * Add a new user
     * @param view
     */
    private void addUser(View view)
    {
        Log.d(getLocalClassName(), "addUser");
        viewUser(view,"","", "", "", false);
    }

    /**
     * Read all configured users from shared preferences.
     */
    private void readUsers()
    {
        SharedPreferences prefs = getSharedPreferences("com.davan.alarmcontroller.users", 0);
        for( Map.Entry entry : prefs.getAll().entrySet() ) {
            users.put(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    /**
     * Store configured users in shared preferencees
     */
    private void storeUsers()
    {
        SharedPreferences.Editor editor = getSharedPreferences("com.davan.alarmcontroller.users", 0).edit();
        editor.clear();
        for( Map.Entry entry : users.entrySet() )
        {
            editor.putString(entry.getKey().toString(), entry.getValue().toString());
        }
        editor.commit();
    }
}