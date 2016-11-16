package com.example.mileto.healthglass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
import java.io.FileInputStream;
import java.io.IOException;


public class ListenCommentActivity extends AppCompatActivity
{

    private String          fileNameComment;
    private Button          buttonListenComment;
    private Button          buttonDeleteComment;
    private TextView        textviewComment;
    private MediaPlayer     myMediaPlayer;
    private String          commentPath;
    private Uri             commentUri;
    //voice and gesture
    private VoiceControl    mVc;
    private GestureSensor   mGc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_comment);

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


        //initialize buttons
        buttonListenComment = (Button)      findViewById(R.id.buttonListenComment);
        buttonDeleteComment = (Button)      findViewById(R.id.buttonDeleteComment);
        //Disable the listenButton until the mediaPlayer is ready
        //buttonListenComment.setEnabled(false);
        //initialize textview
        textviewComment     = (TextView)    findViewById(R.id.textviewPlayComment);



        //get the fileName and recording path coming from the recordings listview in commentsActivity
        Intent commentsActivity = getIntent();
        fileNameComment     = commentsActivity.getStringExtra("fileName");
        commentPath         = commentsActivity.getStringExtra("commentPath")+"/"+fileNameComment;
        //display the filename in the textview
        textviewComment.setText(fileNameComment);
        //get the Uri from the commentPath to feed the mediaplayer
        File commentFile    = new File(commentPath);

        //set the onclickListener for the buttons
        buttonListenComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String buttonState = ((Button)v).getText().toString();

                if(buttonState.equals("Listen"))
                {
                    ((Button)v).setText("Pause");
                    startPlayer();
                    textviewComment.setText("Playing...");
                }
                else if(buttonState.equals("Pause"))
                {
                    ((Button)v).setText("Continue");
                    pausePlayer();
                }
                else
                {
                    ((Button)v).setText("Pause");
                    myMediaPlayer.start();
                }

            }
        });

        buttonDeleteComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //delete a recording
                deleteComment(commentPath);
            }
        });
    }
    private void startPlayer()
    {
        //get the Uri from the commentPath to feed the mediaplayer
        File commentFile    = new File(commentPath);

        commentUri          = Uri.fromFile(commentFile);
        FileInputStream fs  = null;


        //start preparing the mediaPlayer
        //only when the mediaFile is fully loaded enable the listen button
        myMediaPlayer = new MediaPlayer();
        myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            fs = new FileInputStream(commentFile);
            myMediaPlayer.setDataSource(commentUri.getPath());

            myMediaPlayer.prepare();
            myMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    //this is called when the mediaplayer is ready

                    //play the comment
                    myMediaPlayer.setVolume(1.0f,1.0f);
                    myMediaPlayer.start();
                    textviewComment.setText("Ready to play");
                }
            });
            myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    textviewComment.setText(fileNameComment);
                    buttonListenComment.setText("Listen");
                    myMediaPlayer.release();
                    myMediaPlayer = null;
                }
            });
        }
        catch (IOException e )
        {
            Log.d("MEDIAFILE:",commentUri.getPath());
            e.printStackTrace();
        }
    }

    private void continuePlayer()
    {
        myMediaPlayer.start();
    }
    private void pausePlayer()
    {
        myMediaPlayer.pause();
    }
    private void deleteComment(String pathToFile)
    {
        boolean deleted;
        final String _commentToDelete = pathToFile;
        //build a user dialog
        AlertDialog.Builder deleteCommentDialogBuilder = new AlertDialog.Builder(this);
        deleteCommentDialogBuilder.setMessage("Delete comment?");
        //positive clicklistener
        //Log.d("DeletPic:",pathToPicture);
        deleteCommentDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                File commentFile = new File(_commentToDelete);
                if(commentFile.exists())
                {
                    if(commentFile.delete())
                    {
                        Toast.makeText(getApplicationContext(),"Comment deleted",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Deletion failed!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Deletion failed!",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        //negative clickListener
        deleteCommentDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        //create the dialog and show it
        AlertDialog deletePictureDialog = deleteCommentDialogBuilder.create();
        deletePictureDialog.show();
    }

    /**
     * handle voice and gesture selection
     */
    private void handleSelection()
    {
        //check which button has the focus
        if(buttonListenComment.hasFocus())
        {
            buttonListenComment.performClick();
        }
        else
        {
            buttonDeleteComment.performClick();
        }
    }


    private void toggle()
    {
        if(buttonListenComment.hasFocus())
        {
            buttonDeleteComment.requestFocus();
        }
        else
        {
            buttonListenComment.requestFocus();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //activate voicecontrol
        try
        {
            if(mVc != null)
            {
                mVc.on();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //register gestureControl
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

            if(this.command.equals("stop"))
            {
                //go back to vuzix home screen
                finish();
            }

            if(this.command.equals("go down"))
            {
                toggle();
            }

            if(this.command.equals("go up"))
            {
                toggle();
            }

            if(this.command.equals("play"))
            {
                if(myMediaPlayer == null)
                {
                    startPlayer();
                }
                else
                {
                    continuePlayer();
                }
            }
            if(this.command.equals("pause"))
            {
                pausePlayer();
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
            toggle();
        }

        @Override
        protected void onForwardSwipe(int i)
        {
            toggle();
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
