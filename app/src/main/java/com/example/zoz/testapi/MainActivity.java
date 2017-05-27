package com.example.zoz.testapi;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.zoz.testapi.ServiceHandler.ip;
import static com.example.zoz.testapi.ServiceHandler.response;
import static com.example.zoz.testapi.ServiceHandler.token;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String nameText, passwordText, emailText;
    EditText name, password, email;
    Button reg, login, exit, buGetUsers;
    TableLayout tableLayout;
    private static String regUrl = ""+ip+"/authorization/registration";
    private static String loginUrl = ""+ip+"/authorization/login/";
    private static String logoutUrl = ""+ip+"/authorization/logout/";
    private static String getUsersUrl = ""+ip+"/authorization/allUsers";
    private static final String TAG_TOKEN = "key";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_LAST_LOGIN = "last_login";
    JSONArray jsonrespons = null;
    private String messageToken;
    ContentValues cv;
    SQLiteDatabase db;
    BDSupport bdSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText);
        email = (EditText) findViewById(R.id.editText3);
        reg = (Button) findViewById(R.id.button);
        reg.setOnClickListener(this);
        exit = (Button) findViewById(R.id.btExit);
        exit.setOnClickListener(this);
        buGetUsers = (Button) findViewById(R.id.buGetUsers);
        buGetUsers.setOnClickListener(this);
        login = (Button) findViewById(R.id.button2);
        login.setOnClickListener(this);
        bdSupport = new BDSupport(this,"usersdb",null,1);
        tableLayout = (TableLayout) findViewById(R.id.tableUesrs);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button: //registration
                nameText =  name.getText().toString();
                passwordText = password.getText().toString();
                emailText =  email.getText().toString();
                new DoServerColl(2).execute();
                break;

            case R.id.button2: //login
                nameText =  name.getText().toString();
                passwordText = password.getText().toString();
                emailText = null;
                new DoServerColl(1).execute();
                break;

            case R.id.btExit: //exit
                new DoServerColl(3).execute();
                break;

            case R.id.buGetUsers: //getAllUser
                new GetServerColl(getUsersUrl).execute();
                printUsers();
                break;
        }
    }

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }
    public void printUsers(){
        db = bdSupport.getReadableDatabase();
        Cursor cc = db.query("usersdb", null, null, null, null, null, null);
        if (cc.moveToFirst()) {
            int idColIndex = cc.getColumnIndex("id");
            int nameColIndex = cc.getColumnIndex("uresrName");
            int loginColIndex = cc.getColumnIndex("lastLogin");
            tableLayout.removeAllViews();
            do {
                TextView nameUserTv = new TextView(getApplicationContext());
                nameUserTv.setText(cc.getString(nameColIndex));
                TextView lastLoginTv = new TextView(getApplicationContext());
                if(cc.getString(loginColIndex).length()>10){
                    lastLoginTv.setText(cc.getString(loginColIndex).substring(0,16));
                }else{
                    lastLoginTv.setText(cc.getString(loginColIndex));
                }
                TableRow tableRow = new TableRow(this);
                tableRow.addView(nameUserTv);
                tableRow.addView(lastLoginTv);
                tableLayout.addView(tableRow);
                Log.d("TAGN","ID = " + cc.getInt(idColIndex) + ", name = " + cc.getString(nameColIndex) + ", last_login = " + cc.getString(loginColIndex));
            }while (cc.moveToNext());
            bdSupport.close();
        } else
            Log.d("TAGN", "0 rows");
    }

    private class DoServerColl extends AsyncTask<String, String, Void> {
        int index = 0;
        public DoServerColl(int index) {
            this.index = index;
        }

        @Override
        protected Void doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr;
            switch(index){
            case 1:

                jsonStr = sh.serverColl(loginUrl, ServiceHandler.POST, nameText,passwordText, emailText);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        token = jsonObj.getString(TAG_TOKEN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            break;

            case 2:
                jsonStr = sh.serverColl(regUrl, ServiceHandler.POST, nameText, passwordText, emailText);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        //token = jsonObj.getString(TAG_TOKEN);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            break;
            case 3:
                jsonStr = sh.serverColl(logoutUrl, ServiceHandler.POST);
                Log.d("Response: ", "> " + jsonStr);
            break;
            }
            return null;
        }
    }
    private class GetServerColl extends AsyncTask<String, String, Void> {
        ServiceHandler sh = new ServiceHandler();
        String urlForGet;
        public GetServerColl(String Url) {
            this.urlForGet = Url;
        }

        @Override
        protected Void doInBackground(String... params) {
            String jsonStr = sh.serverColl(urlForGet, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    jsonrespons = new JSONArray(jsonStr);
                    JSONObject jsonObj;
                    db = bdSupport.getReadableDatabase();
                    //db.execSQL("DROP TABLE IF EXISTS usersdb");
                    //db.execSQL("CREATE TABLE usersdb (id INTEGER PRIMARY KEY autoincrement, uresrName TEXT, lastLogin TEXT);");
                    for(int i=0;i<jsonrespons.length();i++){
                        JSONObject c = jsonrespons.getJSONObject(i);
                        String userName = c.getString(TAG_USERNAME);
                        String lastLogin = c.getString(TAG_LAST_LOGIN);
                        cv = new ContentValues();
                        cv.put("uresrName",userName);
                        cv.put("lastLogin",lastLogin);
                        Log.d("Response: ", "> " + userName + " "+ lastLogin);
                        int updCount = db.update("usersdb", cv, "uresrName = ?", new String[]{userName});
                        if(updCount==0){
                        long rowID = db.insert("usersdb", null, cv);
                        Log.d("TAGN", "row inserted, ID = " + rowID);}

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            bdSupport.close();
            return null;
        }
    }
}
