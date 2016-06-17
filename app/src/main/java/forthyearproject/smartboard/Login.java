package forthyearproject.smartboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;

public class Login extends Activity implements View.OnClickListener {

    final String smartboardUrl = "http://192.168.43.243:8000/users/login";//TODO change
    String username, password,userType = "student";
    EditText user,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button)findViewById(R.id.LoginButton);
        login.setOnClickListener(this);
        CheckBox staySignedIn = (CheckBox)findViewById(R.id.staySignedIn);
        staySignedIn.setOnClickListener(this);
        TextView createAccount = (TextView) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.userpass);
        checkSavedLogin();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.LoginButton:
                sendData();
                break;
            case R.id.staySignedIn:
                changeButtonStatus(v);
                break;
            case R.id.createAccount:
                createAccount();
                break;
            default:
                break;
        }
    }

    private void createAccount() {
        Intent intent = new Intent(getBaseContext(), CreateEditAccount.class);
        startActivity(intent);
    }

    private void changeButtonStatus(View v){
        //isChecked() returns opposite result than expected as the onClick preforms it action before
        // the result is returned.
        if(((CheckBox) v).isChecked())
            ((CheckBox) v).setChecked(true);
        else
            ((CheckBox) v).setChecked(false);
    }

    private void sendData(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //This allows us to force a get request on main thread as we need these values returned before we open the application,
        //this means the login will take a moment but once received it will work as normal.
        StrictMode.setThreadPolicy(policy);
        username = user.getText().toString();
        password = pass.getText().toString();
        String userType = "Student";
        String modules = "compilers,graphics,ect";
        String thePath = "%name=" + username + "%password="+password;
        String loginUrl;
        try {
            loginUrl = URLEncoder.encode(thePath, "UTF-8");
        }catch (IOException e){
            loginUrl = "Failure, could not encode request";
        }
        String loginInfo = HttpHelper.GET(smartboardUrl+loginUrl);
        //All login failure messages contain Failure, no success messages contain Failure.
        if(loginInfo == null || loginInfo.equals("") || loginInfo.contains("Failure") || !loginInfo.contains("%%%%")){
            onFailure(loginInfo);
        }
        else
            onSuccess(userType, loginInfo);//modules);

    }

    private void onSuccess(String userType,String modules) {
        SharedPreferences preferences = getSharedPreferences("SMARTBOARD_STORAGE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String temp ="";
        if(modules.contains("Name:") && modules.contains("%%%%")) {
            temp =  modules.substring(modules.indexOf("Name:"), modules.indexOf("%%%%"));
            editor.putString("User",temp);
            editor.apply();
        }
        else {
            editor.putString("User","unavailable");
            editor.apply();
        }
        if(modules.contains("Password:") && modules.contains("%%%%")) {
            temp = modules.substring(modules.indexOf("Password:"), modules.indexOf("%%%%", modules.indexOf("Password:")));
            editor.putString("Password", temp);
            editor.apply();
        }
        else {
            editor.putString("Password",null);
            editor.apply();
        }
        if(modules.contains("UserType:") && modules.contains("%%%%")) {
            temp = modules.substring(modules.indexOf("UserType"), modules.indexOf("%%%%", modules.indexOf("UserType")));
            userType = temp;
            editor.putString("UserType", temp);
            editor.apply();
        }
        else {
            editor.putString("UserType","Student");
            editor.apply();
        }
        if(modules.contains("Module:") && modules.contains("%%%%")) {
            temp =  modules.substring(modules.indexOf("Module:"), modules.indexOf("%%%%", modules.indexOf("Module:")));
            editor.putString("Init",temp);
            editor.apply();
        }
        else {
            editor.putString("Init",null);
            editor.apply();
        }
        editor.commit();
        Intent intent = new Intent(getBaseContext(), HomeScreen.class);
        intent.putExtra("userType",userType);
        startActivity(intent);
    }

    private void onFailure(String test) {
        if(test == null || test.equals(""))
            Toast.makeText(this, "no response", Toast.LENGTH_SHORT).show();
        else if(test.length() > 100)
            Toast.makeText(this, "unknown response, you may have entered a null username/password", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
    }

    private void checkSavedLogin() {
        SharedPreferences prefs = this.getSharedPreferences("forthyearproject.smartboard", Context.MODE_PRIVATE);
    }
}
