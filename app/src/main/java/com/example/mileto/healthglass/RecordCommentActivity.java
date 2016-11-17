package com.example.mileto.healthglass;

import android.content.Context;
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
import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.Constants;
import com.vuzix.speech.VoiceControl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordCommentActivity extends AppCompatActivity
{
    private Button          buttonToggleRecording;
    private MediaRecorder   myMediarecorder;
    private CountDownTimer  myCountDownTimer;
    private TextView        recorderTextview;
    private String          recordingPath;
    private String          patientID;
    private String          protocolID;
    static  String          DIRECTORY_NAME="/recordings";

    private VoiceControl    mVc;
    private GestureSensor   mGc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_comment);

        //initialize voice and gestureSensor
        //activate voiceControl
        try
        {
            mVc = new MyVoiceControl(this);
            if(mVc != null)
            {
                //add media grammar
                mVc.addGrammar(Constants.GRAMMAR_MEDIA);
                mVc.on();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //activate gestureSensor
        try
        {
            mGc = new MyGestureControl(this);
            if(mGc != null)
            {
                mGc.register();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //initialize UI elements
        buttonToggleRecording = (Button)      findViewById(R.id.buttonAddRecording);
        recorderTextview    = (TextView)    findViewById(R.id.textviewRecorderCounter);


        //Get the patientID and the protocolID from te intent
        Intent fromProtocolToDo = getIntent();
        patientID               = fromProtocolToDo.getStringExtra("patientID");
        protocolID              = fromProtocolToDo.getStringExtra("protocolID");
        //build the path to save the recording
        recordingPath = getExternalFilesDir(Environment.DIRECTORY_MUSIC)+ DIRECTORY_NAME+"/"+patientID+"/"+this.protocolID;
        Log.d("FULLRECORDERPATH:",getFileForRecording().toString());

        //set the clicklistener for the recorder toggle Button
        buttonToggleRecording.setOnClickListener(new View.OnClickListener()
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

    private void handleSelection()
    {
        buttonToggleRecording.performClick();
    }

    //inner class myvoicecontrol
    public class MyVoiceControl extends VoiceControl
    {
        private String command;
        private Context activityContext;

        public MyVoiceControl(Context context)
        {
            super(context);
            this.activityContext = context;
        }


        protected void onRecognition(String result)
        {
            this.command = result;
            if(this.command.equals("select"))
            {
                handleSelection();
            }

            if(this.command.equals("record"))
            {
                startRecording();
            }

            if(this.command.equals("end"))
            {
                stopRecording();
            }



            if(this.command.equals("stop"))
            {
                finish();
            }

        }

        @Override
        public String toString()
        {
            return command;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //register voice
        try
        {
            if (mVc != null)
            {
                mVc.on();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //register gesture
        try
        {
            if(mGc != null)
            {
                mGc.register();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //deactivate voice
        try
        {
            if(mVc != null)
            {
                mVc.off();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //unregister gesture
        try
        {
            if(mGc != null)
            {
                mGc.unregister();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            if(mVc != null)
            {
                mVc.off();
                mVc = null;
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //unregister gesture
        try
        {
            if(mGc != null)
            {
                mGc.unregister();
                mGc = null;
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    //end of Vuzix voice control class

    //inner class myGestureControl
    public class MyGestureControl extends GestureSensor
    {

        public MyGestureControl(Context context)
        {
            super(context);
        }

        @Override
        protected void onBackSwipe(int i)
        {

        }

        @Override
        protected void onForwardSwipe(int i)
        {

        }

        @Override
        protected void onNear()
        {
            handleSelection();
        }

        @Override
        protected void onFar()
        {
            finish();
        }

    }



    //end inner class myGestureControl

}
