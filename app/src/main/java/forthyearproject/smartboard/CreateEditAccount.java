package forthyearproject.smartboard;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;

public class CreateEditAccount extends Activity implements View.OnClickListener {

    final String smartboardUrl = "http://192.168.43.243:8000/users";
    String username,password,modulecode,modulename,actCode;
    EditText userName,userId,userPass,moduleCode,moduleName;
    RadioGroup userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        userType = (RadioGroup) findViewById(R.id.selectUserType);
        userType.check(R.id.selectStudent);
        RadioButton lecturer = (RadioButton)findViewById(R.id.selectLecturer);
        lecturer.setOnClickListener(this);
        RadioButton student = (RadioButton)findViewById(R.id.selectStudent);
        student.setOnClickListener(this);
        RadioButton moduleChange = (RadioButton)findViewById(R.id.add_delete_module);
        moduleChange.setOnClickListener(this);
        Button create = (Button) findViewById(R.id.btnSubmit);
        create.setOnClickListener(this);
        Button clear = (Button) findViewById(R.id.btnCancel);
        clear.setOnClickListener(this);
        Button addModule = (Button) findViewById(R.id.moduleAdd);
        addModule.setOnClickListener(this);
        Button deleteModule = (Button) findViewById(R.id.moduleDelete);
        deleteModule.setOnClickListener(this);
        userName = (EditText)findViewById(R.id.useridField);
        userPass = (EditText)findViewById(R.id.passwordField);
        moduleCode = (EditText)findViewById(R.id.activationCodeField);
        moduleName = (EditText)findViewById(R.id.moduleCodeField);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.selectStudent:
                userType.check(R.id.selectStudent);
                initialiseStudentForm();
                break;
            case R.id.selectLecturer:
                userType.check(R.id.selectLecturer);
                initialiseLecturerForm();
                break;
            case R.id.add_delete_module:
                userType.check(R.id.add_delete_module);
                initialiseModuleChangeForm();
                break;
            case R.id.btnSubmit:
                submitContent();
                break;
            case R.id.btnCancel:
                clearEditText();
                break;
            case R.id.moduleAdd:
                addModule();
                break;
            case R.id.moduleDelete:
                deleteModule();
                break;
            default:
                break;
        }
    }

    private void submitContent() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //This allows us to force a get request on main thread as we need these values returned before we open the application,
        //this means the login will take a moment but once received it will work as normal.
        StrictMode.setThreadPolicy(policy);
        String response = "";
        //if student
        String thePath = "";
        String loginUrl = "";
        username = userName.getText().toString();
        password = userPass.getText().toString();
        RadioButton studentselect = (RadioButton)findViewById(R.id.selectStudent);
        RadioButton lecturerselect = (RadioButton)findViewById(R.id.selectLecturer);
        if(studentselect.isChecked()) {
            thePath = "%type=Student%name=" + username + "%password=" + password + "%code=null";
            try {
                loginUrl = URLEncoder.encode(thePath, "UTF-8");
            } catch (IOException e) {
                loginUrl = "Failure, could not encode request";
            }
            response = HttpHelper.GET(smartboardUrl + "/create_user" + loginUrl);
        }
        //if lecturer
        else if(lecturerselect.isChecked()) {
            modulecode = moduleCode.getText().toString();
            thePath = "%type=Lecturer%name=" + username + "%password=" + password + "%code=" + modulecode;
            try {
                loginUrl = URLEncoder.encode(thePath, "UTF-8");
            } catch (IOException e) {
                loginUrl = "Failure, could not encode request";
            }
            response = HttpHelper.GET(smartboardUrl + "/create_user" + loginUrl);
        }
        if(response.contains("Success"))
            Toast.makeText(this,response,Toast.LENGTH_LONG).show();
        else if(response.contains("Page not found"))
            Toast.makeText(this,"no activation code entered",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,response,Toast.LENGTH_LONG).show();
    }

    private void addModule() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //This allows us to force a get request on main thread as we need these values returned before we open the application,
        //this means the login will take a moment but once received it will work as normal.
        StrictMode.setThreadPolicy(policy);
        String thePath = "";
        String loginUrl = "";
        username = userName.getText().toString();
        password = userPass.getText().toString();
        modulename = moduleName.getText().toString();
        modulecode = moduleCode.getText().toString();
        thePath = "%name="+username+"%password="+password+"%module="+modulename+"%code="+modulecode;
        try {
            loginUrl = URLEncoder.encode(thePath, "UTF-8");
        }catch (IOException e){
            loginUrl = "Failure, could not encode request";
        }
        String response = HttpHelper.GET(smartboardUrl+"/add_module"+loginUrl);
        if(response.contains("Success"))
            Toast.makeText(this,"Module Successfully Added",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"Module Not Added",Toast.LENGTH_LONG).show();
    }

    private void deleteModule() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //This allows us to force a get request on main thread as we need these values returned before we open the application,
        //this means the login will take a moment but once received it will work as normal.
        StrictMode.setThreadPolicy(policy);
        String thePath = "";
        String loginUrl = "";
        username = userName.getText().toString();
        password = userPass.getText().toString();
        modulename = moduleName.getText().toString();
        thePath = "%name="+username+"%password="+password+"%module="+modulename;
        try {
            loginUrl = URLEncoder.encode(thePath, "UTF-8");
        }catch (IOException e){
            loginUrl = "Failure, could not encode request";
        }
        String response = HttpHelper.GET(smartboardUrl+"/delete_module"+loginUrl);
        if(response.contains("Success"))
            Toast.makeText(this,response,Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,response,Toast.LENGTH_LONG).show();
    }

    private void clearEditText() {
        EditText id = (EditText) findViewById(R.id.useridField);
        EditText password = (EditText) findViewById(R.id.passwordField);
        EditText activationCode = (EditText) findViewById(R.id.activationCodeField);
        id.setText("");
        password.setText("");
        activationCode.setText("");
    }

    private void initialiseLecturerForm() {
        TextView id = (TextView)findViewById(R.id.idField);
        LinearLayout module = (LinearLayout)findViewById(R.id.moduleCode);
        LinearLayout activation = (LinearLayout)findViewById(R.id.activationCode);
        LinearLayout moduleButton = (LinearLayout)findViewById(R.id.moduleButton);
        LinearLayout submitClearButton = (LinearLayout)findViewById(R.id.submit_clear_button);
        id.setText(R.string.lecturerid);
        activation.setVisibility(View.VISIBLE);
        submitClearButton.setVisibility(View.VISIBLE);
        module.setVisibility(View.GONE);
        moduleButton.setVisibility(View.GONE);
    }

    private void initialiseStudentForm() {
        TextView id = (TextView)findViewById(R.id.idField);
        LinearLayout module = (LinearLayout)findViewById(R.id.moduleCode);
        LinearLayout activation = (LinearLayout)findViewById(R.id.activationCode);
        LinearLayout moduleButton = (LinearLayout)findViewById(R.id.moduleButton);
        LinearLayout submitClearButton = (LinearLayout)findViewById(R.id.submit_clear_button);
        id.setText(R.string.studentid);
        submitClearButton.setVisibility(View.VISIBLE);
        module.setVisibility(View.GONE);
        activation.setVisibility(View.GONE);
        moduleButton.setVisibility(View.GONE);
    }

    private void initialiseModuleChangeForm() {
        TextView id = (TextView)findViewById(R.id.idField);
        LinearLayout module = (LinearLayout)findViewById(R.id.moduleCode);
        LinearLayout activation = (LinearLayout)findViewById(R.id.activationCode);
        LinearLayout moduleButton = (LinearLayout)findViewById(R.id.moduleButton);
        LinearLayout submitClearButton = (LinearLayout)findViewById(R.id.submit_clear_button);
        id.setText(R.string.userid);
        module.setVisibility(View.VISIBLE);
        activation.setVisibility(View.VISIBLE);
        moduleButton.setVisibility(View.VISIBLE);
        submitClearButton.setVisibility(View.GONE);
    }
}
