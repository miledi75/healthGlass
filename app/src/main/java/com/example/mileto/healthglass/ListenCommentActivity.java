package com.example.mileto.healthglass;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class ListenCommentActivity extends AppCompatActivity
{

    private String          fileNameComment;
    private Button          listenComment;
    private Button          deleteComment;
    private TextView        textviewComment;
    private MediaPlayer     myMediaPlayer;
    private String          commentPath;
    private Uri             commentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_comment);

        //initialize buttons
        listenComment       = (Button)      findViewById(R.id.buttonListenComment);
        deleteComment       = (Button)      findViewById(R.id.buttonDeleteComment);
        //Disable the listenButton until the mediaPlayer is ready
        //listenComment.setEnabled(false);
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
        listenComment.setOnClickListener(new View.OnClickListener()
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

        deleteComment.setOnClickListener(new View.OnClickListener()
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
                    listenComment.setText("Listen");
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
}
