package com.example.mileto.healthglass;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CommentsActivity extends AppCompatActivity
{

    private Button          addComment;
    private ListView        listviewRecordings;
    private String          recordingPath;
    private String          patientID;
    private String          protocolID;
    private ArrayAdapter    recordingsAdapter;
    private ArrayList<String> recordings;
    static  String          DIRECTORY_NAME="/recordings";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        //prepare the UI elements

        //Get the patientID and the protocolID from te intent
        Intent fromProtocolToDo = getIntent();
        patientID               = fromProtocolToDo.getStringExtra("patientID");
        protocolID              = fromProtocolToDo.getStringExtra("protocolID");
        //build the path to save the recording

        //initialize buttons
        addComment      = (Button) findViewById(R.id.buttonAddComment);


        //initialize listview
        listviewRecordings = (ListView) findViewById(R.id.listviewRecordings);


        //get the recordingpath for the comments
        recordingPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ DIRECTORY_NAME+"/"+patientID+"/"+this.protocolID;
        loadCommentsInListview();
        //OnclickListenrers for the Buttons
        addComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Start intent RecordCommentActivity
                //and pass the patientID and the protocolID
                Intent recordComment = new Intent(getApplicationContext(),RecordCommentActivity.class);
                recordComment.putExtra("patientID",patientID);
                recordComment.putExtra("protocolID",protocolID);
                startActivity(recordComment);
            }
        });

        //onclicklistener for the listview
        listviewRecordings.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int itemPosition        = position;

                // the filename of the recording
                String  fileName        = (String) listviewRecordings.getItemAtPosition(position);
                //Call the listenComment activity
                Intent listenComment    = new Intent(getApplicationContext(),ListenCommentActivity.class);
                //send the filename and the recordingPath
                listenComment.putExtra("fileName",fileName);
                listenComment.putExtra("commentPath",recordingPath);
                startActivity(listenComment);
            }
        });
    }

    private void loadCommentsInListview()
    {
        File[] files = listRecordingsInFolder(recordingPath);

        //fill an arraylist with the data to populate the listview
        recordings = new ArrayList<String>();
        if(files != null)
        {
            for (int i = 0; i < files.length; ++i)
            {
                recordings.add(files[i].getName());
            }
        }


        //create an arrayAdapter to provide the recordings to the listview
        recordingsAdapter = new ArrayAdapter<String>(this, R.layout.recordings_listview_layout,recordings);

        //bind the adapter to the listview
        listviewRecordings.setAdapter(recordingsAdapter);
    }

    private File[] listRecordingsInFolder(String pathToFolder)
    {
        File f = new File(pathToFolder);
        File[] files = f.listFiles();
        return files;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        loadCommentsInListview();
    }
}
