package com.example.mileto.healthglass;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.VoiceControl;

import java.io.File;
import java.util.ArrayList;


public class CommentsActivity extends AppCompatActivity
{

    private Button          addCommentButton;
    private ListView        listviewRecordings;
    private String          recordingPath;
    private String          patientID;
    private String          protocolID;
    private ArrayAdapter    recordingsAdapter;
    private ArrayList<String> recordings;
    static  String          DIRECTORY_NAME="/recordings";

    //voiceControl and gestureControl
    private VoiceControl    mVc;
    private GestureSensor   mGc;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //activate voiceControl
        try
        {
            mVc = new MyVoiceControl(this);
            if(mVc != null)
            {
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


        //prepare the UI elements

        //Get the patientID and the protocolID from te intent
        Intent fromProtocolActivity = getIntent();
        patientID               = fromProtocolActivity.getStringExtra("patientID");
        protocolID              = fromProtocolActivity.getStringExtra("protocolID");

        //check if the request is coming from a performed protocol
        if(fromProtocolActivity.getStringExtra("performed") != null)
        {
            //disable/hide the add comment button
            addCommentButton.setVisibility(View.INVISIBLE);
        }
        //build the path to save the recording

        //initialize buttons
        addCommentButton = (Button) findViewById(R.id.buttonAddComment);


        //initialize listview
        listviewRecordings = (ListView) findViewById(R.id.listviewRecordings);

        //check if the request is coming from a performed protocol
        if(fromProtocolActivity.getStringExtra("performed") != null)
        {
            //disable/hide the add comment button
            addCommentButton.setVisibility(View.INVISIBLE);
        }

        //get the recordingpath for the comments
        recordingPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ DIRECTORY_NAME+"/"+patientID+"/"+this.protocolID;
        loadCommentsInListview();
        //OnclickListenrers for the Buttons
        addCommentButton.setOnClickListener(new View.OnClickListener()
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

    /**
     * handles the events of voiceControl
     * and gestureControl
     */
    private void handleSelection()
    {
        //check if addButton has focus
        if(addCommentButton.hasFocus())
        {
            addCommentButton.performClick();
        }
        else//list with recordings has focus
        {
            //get the selected item
            int selectedPosition = listviewRecordings.getSelectedItemPosition();
            //call the itemClickListener for the selected item
            listviewRecordings.performItemClick(listviewRecordings.getChildAt(selectedPosition),selectedPosition,listviewRecordings.getItemIdAtPosition(selectedPosition));
        }
    }

    /**
     * handle up movements
     */
    public void moveDown()
    {
        //check if button is selected
        if(addCommentButton.hasFocus())
        {
            //give focus to the list
            listviewRecordings.requestFocus();
        }
        else if(listviewRecordings.hasFocus())
        {
            //get the selected item position
            int selectedItemPosition = listviewRecordings.getSelectedItemPosition();
            //get number of items in list
            int totalNumberOfItems = listviewRecordings.getChildCount();
            //if last item is selected, move to the AddButton
            if(selectedItemPosition == totalNumberOfItems -1)
            {
                addCommentButton.requestFocus();
            }
            else
            {
                listviewRecordings.setSelection(selectedItemPosition+1);
            }
        }
    }

    /**
     * handle down movements
     */
    public void moveUp()
    {
        //get number of items in list
        int totalNumberOfItems = listviewRecordings.getChildCount();
        //check if button is selected
        if(addCommentButton.hasFocus())
        {
            //give focus to the list
            listviewRecordings.requestFocus();
            //select last item in list
            listviewRecordings.setSelection(totalNumberOfItems-1);

        }
        else if(listviewRecordings.hasFocus())
        {
            //get the selected item position
            int selectedItemPosition = listviewRecordings.getSelectedItemPosition();

            //if first item is selected, move to the AddButton
            if(selectedItemPosition == 0)
            {
                addCommentButton.requestFocus();
            }
            else
            {
                //move one up the list
                listviewRecordings.setSelection(selectedItemPosition-1);
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadCommentsInListview();
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
                moveDown();
            }

            if(this.command.equals("go up"))
            {
                moveUp();
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
            moveDown();
        }

        @Override
        protected void onForwardSwipe(int i)
        {
            moveUp();
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
