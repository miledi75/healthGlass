package com.example.mileto.healthglass;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordCommentActivity extends AppCompatActivity
{
    private Button          toggleRecording;
    private MediaRecorder   myMediarecorder;
    private CountDownTimer  myCountDownTimer;
    private TextView        recorderTextview;
    private String          recordingPath;
    private String          patientID;
    private String          protocolID;
    static  String          DIRECTORY_NAME="/recordings";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_comment);


        toggleRecording     = (Button)      findViewById(R.id.buttonAddRecording);
        recorderTextview    = (TextView)    findViewById(R.id.textviewRecorderCounter);


        //Get the patientID and the protocolID from te intent
        Intent fromProtocolToDo = getIntent();
        patientID               = fromProtocolToDo.getStringExtra("patientID");
        protocolID              = fromProtocolToDo.getStringExtra("protocolID");
        //build the path to save the recording
        //@Todo change the directory pictures to directory documents later
        recordingPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ DIRECTORY_NAME+"/"+patientID+"/"+this.protocolID;
        Log.d("FULLRECORDERPATH:",getFileForRecording().toString());

        //set the clicklistener for the recorder toggle Button
        toggleRecording.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //When the button is clicked
                //check the button text to get the state
                //and act appropriately
                String buttonState = ((Button)v).getText().toString();

                if(buttonState.equals("Record"))
                {
                    //startRecording();
                    startTimer();
                    ((Button)v).setText("Stop");

                }
                else
                {
                    ((Button)v).setText("Record");
                    stopRecording();
                    recorderTextview.setText("Finished recording");
                }

            }
        });
    }

    private void startRecording()
    {

        myMediarecorder = new MediaRecorder();
        myMediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myMediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myMediarecorder.setOutputFile(getFileForRecording().getPath());
        myMediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try
        {
            myMediarecorder.prepare();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.e("MEDIARECORDER:", "prepare() failed");
        }
        myMediarecorder.start();
        recorderTextview.setText("Recording...");
        Log.d("RECORDERFILEPATH",getFileForRecording().getPath());
    }

    private void stopRecording()
    {
        myMediarecorder.stop();
        myMediarecorder.release();
        myMediarecorder = null;
    }

    /**
     * returns a file to save the recording to
     * @return
     */
    private File getFileForRecording()
    {
        //Create directory if it does not exist
        File imageDirectory = new File(recordingPath);
        if(!imageDirectory.exists())
        {
            imageDirectory.mkdirs();
        }

        DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String imgcurTime = date.format(new Date());
        String _path = imageDirectory.getPath() + File.separator + imgcurTime.replace(" ","_")+ ".3gp";

        File recordFile = new File(_path);
        //this.imagePath = pictureFile.getAbsolutePath();
        return recordFile;
    }

    /**
     * sets up and starts the timer
    */
    private void startTimer()
    {
        //initialize timer popup
        myCountDownTimer = new CountDownTimer(5000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                String counter = Integer.toString((int) (millisUntilFinished/1000)-1);

                recorderTextview.setText(counter);
            }

            @Override
            public void onFinish()
            {
                startRecording();
                recorderTextview.setText("Recording...");
            }
        }.start();
    }

}
