package com.example.mileto.healthglass;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.Constants;
import com.vuzix.speech.VoiceControl;


public class HelpVideoActivity extends AppCompatActivity
{

    private static final int RECOVERY_REQUEST = 1;
    private String srcPath;
    private String protocolItemId;

    private VideoView myVideoView;

    private VoiceControl    mVc;
    private GestureSensor   mGc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_video);


        //Voicecontrol and gesturecontrol
        //activate voice control
        try
        {
            mVc = new MyVoiceControl(getApplicationContext());
            if(mVc != null)
            {
                mVc.addGrammar(Constants.GRAMMAR_MEDIA);
                mVc.on();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }

        //activate gestureControl
        //check if gesturesensor is on
        if(GestureSensor.isOn())
        {
            try
            {
                mGc = new MyGestureControl(getApplicationContext());
                if (mGc == null) {
                    Toast.makeText(this, "Cannot create gestureSensor", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //activate gesturesensor
                    mGc.register();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,"Please turn on the gestureSensor",Toast.LENGTH_SHORT).show();
            //GestureSensor.On();
        }

        //get the protocolItemId from the sending intent
        Intent intent   = getIntent();
        protocolItemId  = intent.getStringExtra("protocolItemId").toString();

        //@todo implement rest call to get the corresponding video link with the educational clip

        switch (protocolItemId)
        {
            case "1":
                srcPath = "http://chamilo.arteveldehs.be/index.php?go=document_downloader&object=1420734&security_code=ed1364991af6f461e0ba835508c8b66f0827515a&application=repository&display=1";
                break;
            case "2":
                srcPath = "http://chamilo.arteveldehs.be/index.php?go=document_downloader&object=1420736&security_code=3263897e2d0a2207b25ff455a98e63fd8eb2a241&application=repository&display=1";
                break;
            case "3":
                srcPath = "http://chamilo.arteveldehs.be/index.php?go=document_downloader&object=1420737&security_code=970be7c8cca1fd0a3908444fd9758b24aaa1782a&application=repository&display=1";
                break;
        }
        srcPath = "http://chamilo.arteveldehs.be/index.php?go=document_downloader&object=1411819&security_code=c95f82d406888a297500036c22168c6419d28300&application=repository&display=1";
        myVideoView = (VideoView)findViewById(R.id.myvideoview);
        myVideoView.setVideoURI(Uri.parse(srcPath));

        myVideoView.setMediaController(new MediaController(this));
        myVideoView.requestFocus();
        myVideoView.start();
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
            if(this.command.equals("go back"))
            {
                //Go back to PatientInfoActivity
                finish();
            }

            if(this.command.equals("pause"))
            {
                if(myVideoView.isPlaying())
                {
                    myVideoView.pause();
                }
            }
            if(this.command.equals("play"))
            {
                if(!myVideoView.isPlaying())
                {
                    myVideoView.start();
                }
            }

        }

        @Override
        public String toString()
        {
            return command;
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
        protected void onBackSwipe(int i) {

        }

        @Override
        protected void onForwardSwipe(int i) {

        }

        @Override
        protected void onNear()
        {
            if(myVideoView.isPlaying())
            {
                myVideoView.pause();
            }
            else
            {
                myVideoView.start();
            }
        }

        @Override
        protected void onFar()
        {
            finish();
        }
    }
    //end inner class myGestureControl
}
