package forthyearproject.smartboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

public class Login extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button)findViewById(R.id.LoginButton);
        login.setOnClickListener(this);
        CheckBox staySignedIn = (CheckBox)findViewById(R.id.staySignedIn);
        staySignedIn.setOnClickListener(this);
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
            default:
                break;
        }

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
        //TODO send http request to server
        if(true) //TODO if(success)
            onSuccess();
        else
            onFailure();
    }

    private void onSuccess() {
        Intent intent = new Intent(getBaseContext(), HomeScreen.class);
        startActivity(intent);
    }

    private void onFailure() {
        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
    }

    private void checkSavedLogin() {
        SharedPreferences prefs = this.getSharedPreferences("forthyearproject.smartboard", Context.MODE_PRIVATE);
    }
}
