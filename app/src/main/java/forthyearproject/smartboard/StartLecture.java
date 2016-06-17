package forthyearproject.smartboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class StartLecture extends Activity implements View.OnClickListener{

    private EditText textField;

    private String ipAddressServer;
    private int portNumberServer;
    private float mouseSensitivity;

    //String for message to server
    private String message;

    private VelocityTracker myVelTracker = null;

    //Variables for shared preference reception
    private final String DEFAULTIP = "N/A";
    private final int DEFAULTPORT = 0;
    private final float DEFAULTMOUSE = 0.125f;

    //Method to be called on a touch event
    public boolean onTouchEvent(MotionEvent event) {
        //Creating new Display object dependant on current screen properties
        Display display = getWindowManager().getDefaultDisplay();
        Point sizeOfScreen = new Point();
        //setting the sizeOfScreen Point = to the size
        display.getSize(sizeOfScreen);

        // return action value
        int action = event.getActionMasked();

        if( event.getY() < (sizeOfScreen.y*1.05 / 2)){
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (myVelTracker == null) {
                        myVelTracker = VelocityTracker.obtain();
                    } else {
                        myVelTracker.clear();
                    }
                    myVelTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    myVelTracker.addMovement(event);
                    myVelTracker.computeCurrentVelocity(1000);
                    message = ("0" + (int) (myVelTracker.getXVelocity() * mouseSensitivity) + " " + (int) (myVelTracker.getYVelocity() * mouseSensitivity));
                    RequestHandler ServerMessageTask = new RequestHandler();
                    ServerMessageTask.execute();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    myVelTracker.recycle();
                    myVelTracker = null;
                    break;
                case MotionEvent.ACTION_UP:
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_lecture);

        //linking buttons to xml file id's
        textField = (EditText) findViewById(R.id.fileNameField);
        Button button = (Button) findViewById(R.id.submit_file_button);
        Button buttonLeft = (Button) findViewById(R.id.left_mouse_button);
        Button buttonRight = (Button) findViewById(R.id.right_mouse_button);
        Button buttonEnter = (Button) findViewById(R.id.enter_button);
        Button keyRight = (Button) findViewById(R.id.right_button);
        Button keyLeft = (Button) findViewById(R.id.left_button);
        Button keyUp = (Button) findViewById(R.id.up_button);
        Button keyDown = (Button) findViewById(R.id.down_button);

        //Creating a series of OnClickListeners for each button
        //Each button sends a different message buffer to let server know button id
        button.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);

        buttonEnter.setOnClickListener(this);

        keyRight.setOnClickListener(this);
        keyLeft.setOnClickListener(this);
        keyUp.setOnClickListener(this);
        keyDown.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ipAddressServer = "192.168.43.181";//TODO set this ip
        portNumberServer = 4444;
        mouseSensitivity = 0.125f;
        Intent intent = getIntent();
        String user = intent.getStringExtra("User");
        String pass = intent.getStringExtra("Password");
        String module = intent.getStringExtra("Module");
        String lecture = intent.getStringExtra("Lecture");
        message="9startLecture_" +user +"_" +pass +"_" +module +"_" +lecture;
        RequestHandler ServerMessageTask = new RequestHandler();
        ServerMessageTask.execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Quit if back is pressed
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            message = "9stop";
            RequestHandler ServerMessageTask = new RequestHandler();
            ServerMessageTask.execute();
        }
        return super.onKeyDown(keyCode, event);
    }

    //sudo streamer -q -c /dev/video0 -s 640x480 -f jpeg -t 40 -r 12 -j 75 -w 0 -o output.avi
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_file_button:
                message = "2" + textField.getText().toString();
                textField.setText("");
                break;
            case R.id.left_mouse_button:
                message = "11";
                break;
            case R.id.right_mouse_button:
                message = "12";
                break;
            case R.id.enter_button:
                message = "3";
                break;
            case R.id.right_button:
                message = "6";
                break;
            case R.id.left_button:
                message = "7";
                break;
            case R.id.up_button:
                message = "4";
                break;
            case R.id.down_button:
                message = "5";
                break;
        }
        RequestHandler ServerMessageTask = new RequestHandler();
        ServerMessageTask.execute();
    }

    //Class to handle socket connection using AsyncTask to allow for computation while waiting for server
    private class RequestHandler extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket clientSocket = new Socket(ipAddressServer, portNumberServer);
                PrintWriter myPrintWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                myPrintWriter.write(message);
                myPrintWriter.flush();
                myPrintWriter.close();
                clientSocket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
