package forthyearproject.smartboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

public class UploadFiles extends Activity implements View.OnClickListener {

    final String smartboardUrl = "http://192.168.43.243:8000/modules/";//TODO change
    private String selectedFilePath = "";
    String lectureNo = "",moduleName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attachments);
        Button choose = (Button)findViewById(R.id.chooseFile);
        choose.setOnClickListener(this);
        Button send = (Button)findViewById(R.id.sendFile);
        send.setOnClickListener(this);
        Intent intent = getIntent();
        lectureNo = intent.getStringExtra("lecture");
        moduleName = intent.getStringExtra("module");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseFile:
                boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                if (isKitKat) {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);

                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.sendFile:
                EditText id = (EditText) findViewById(R.id.lectureidform);
                EditText pass = (EditText) findViewById(R.id.lecturepassform);
                EditText attachment = (EditText) findViewById(R.id.attachmentnameform);
                RadioButton notes = (RadioButton) findViewById(R.id.selectNotes);
                RadioButton video = (RadioButton) findViewById(R.id.selectVideo);
                RadioButton attach = (RadioButton) findViewById(R.id.selectAttachment);
                String thePath = "";
                if(notes.isChecked()) {
                    thePath = "add_notes%module=" + moduleName + "%lnum=" + lectureNo +
                            "%lecturer=" + id.getText().toString() + "%password="+pass.getText().toString()+"%notes="+attachment.getText().toString();
                }
                else if(video.isChecked()) {
                    thePath = "add_video%module=" + moduleName + "%lnum=" + lectureNo +
                            "%lecturer=" + id.getText().toString() + "%password="+pass.getText().toString();
                }
                else if(attach.isChecked()) {
                    thePath = "add_attachment%module=" + moduleName + "%lnum=" + lectureNo +
                            "%lecturer=" + id.getText().toString() + "%password="+pass.getText().toString()+"%attachment="+attachment.getText().toString();
                }
                String loginUrl;
                try {
                    loginUrl = URLEncoder.encode(thePath, "UTF-8");
                }catch (IOException e){
                    loginUrl = "Failure, could not encode request";
                }
                String response = HttpHelper.UPDATE(smartboardUrl+loginUrl,selectedFilePath);
                if(response.length() < 200)
                    Toast.makeText(this,response,Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this,"an unknown error has occured",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri path = data.getData();
        selectedFilePath = FilePath.getPath(this, path);
        File sourceFile = new File(selectedFilePath);
        if(!sourceFile.isFile())
            Toast.makeText(this,"could not find with path",Toast.LENGTH_LONG).show();
        else
            uploadFile(data);
    }

    private void uploadFile(Intent data){
        Toast.makeText(this,"The file is ready for upload",Toast.LENGTH_LONG).show();
        //the upload process is now done in HttpHelper
    }
}
