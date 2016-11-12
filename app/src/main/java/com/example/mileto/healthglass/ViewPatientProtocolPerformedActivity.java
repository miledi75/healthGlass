package com.example.mileto.healthglass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.VoiceControl;

import java.util.ArrayList;

public class ViewPatientProtocolPerformedActivity extends AppCompatActivity
{
    private Button              viewPicturesButton;
    private Button              viewRecordingsButton;
    private ListView            protocolItemsListview;
    private String              patientIdFromBarcode;
    private String              protocolId;
    private int                 numberOfProtocolItems;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_protocol_performed);

        //get the protocolId from the PatientInfoActivity Intent
        Intent patientInfoActivity  = getIntent();
        this.patientIdFromBarcode   = patientInfoActivity.getStringExtra("patientId");
        this.protocolId             = patientInfoActivity.getStringExtra("protocolId");

        //initialize UI elements
        viewPicturesButton           = (Button) findViewById(R.id.buttonViewPictures);
        viewRecordingsButton      = (Button) findViewById(R.id.buttonViewRecordings);
        protocolItemsListview   = (ListView) findViewById(R.id.listViewProtocolItemsPerformed);

        //add the action listeners for the buttons

        viewPicturesButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                activateCameraIntent();
                //call the intent for the pictures
            }
        });


        viewRecordingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                activateRecorderIntent();
            }
        });

        protocolItemsListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ProtocolItemsAdapter pI = (ProtocolItemsAdapter) parent.getAdapter();
                Toast toast = new Toast(getApplicationContext());
                Toast.makeText(getApplicationContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();
            }
        });
        //populate the listview with the protocolItems

        //Create the data source
        ArrayList<PatientProtocolItem> protocolItems = new ArrayList<PatientProtocolItem>();

        //create the arrayadapter
        ProtocolItemsAdapter protocolItemsAdapter = new ProtocolItemsAdapter(this,protocolItems);

        //Attach the adapter to the listview
        protocolItemsListview.setAdapter(protocolItemsAdapter);
        //Create testData and add to adapter to update view
        PatientProtocolItem protocolItem1 = new PatientProtocolItem("remove bandage",true,1);
        PatientProtocolItem protocolItem2 = new PatientProtocolItem("Clean and desinfect",true,2);
        PatientProtocolItem protocolItem3 = new PatientProtocolItem("renew bandage",true,3);

        //update the adapter
        protocolItemsAdapter.add(protocolItem1);
        protocolItemsAdapter.add(protocolItem2);
        protocolItemsAdapter.add(protocolItem3);
    }

    private void activateCameraIntent()
    {
        //call the camera intent
        Intent picturesIntent = new Intent(this,PicturesActivity.class);
        picturesIntent.putExtra("protocolId",protocolId);
        picturesIntent.putExtra("patientId",patientIdFromBarcode);
        //variable to indicate that the call is coming from protocolPerformed
        picturesIntent.putExtra("performed",true);
        startActivity(picturesIntent);
    }

    private void activateRecorderIntent()
    {
        //create an intent for the audio comment recording
        Intent recordCommentIntent = new Intent(this,CommentsActivity.class);
        //put the patient code and the protocolID in the putextra
        //so the audiorecorder can build the path to save the recordings
        recordCommentIntent.putExtra("patientID",this.patientIdFromBarcode);
        recordCommentIntent.putExtra("protocolID",this.protocolId);
        //variable to indicate that the call is coming from protocolPerformed
        recordCommentIntent.putExtra("performed",true);
        //call the activity
        startActivity(recordCommentIntent);
    }

    /**
     * function that handles UI movement down for VoiceControl and GestureControl
     */
    public void moveDown()
    {
        //check if listview is selected => go to the next item in the listview
        if(protocolItemsListview.hasFocus())
        {
            //check if we are at the end of the list => go to the picturebutton
            if(protocolItemsListview.getSelectedItemPosition() == numberOfProtocolItems-1)
            {
                viewPicturesButton.requestFocus();
            }
            else // go to the next protocolitem
            {
                protocolItemsListview.setSelection(protocolItemsListview.getSelectedItemPosition()+1);
            }
        }
        //check if the imagesbutton is selected => go to the commentbutton
        else if(viewPicturesButton.hasFocus())
        {
            viewRecordingsButton.requestFocus();
        }
        else //comment button has focus: go to the listview
        {
            protocolItemsListview.requestFocus();
        }
    }

    public void moveUp()
    {
        //check if listview is selected => go to the previous item in the listview
        if(protocolItemsListview.hasFocus())
        {
            //check if we are at the beginning of the list => go to the commentButton
            if(protocolItemsListview.getSelectedItemPosition() == 0)
            {
                viewPicturesButton.requestFocus();
            }
            else // go to the previous protocolitem
            {
                protocolItemsListview.setSelection(protocolItemsListview.getSelectedItemPosition()-1);
            }
        }
        //check if the imagesbutton is selected => go to the listview
        else if(viewPicturesButton.hasFocus())
        {
            protocolItemsListview.requestFocus();
        }
        else //comment button has focus: go to the imagesButton
        {
            viewPicturesButton.requestFocus();
        }
    }

    //INNER CLASSESS
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

            if(this.command.equals("show help"))
            {
                getHelpForProtocolItem();
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

    private void handleSelection()
    {
        //check if buttons are selected/have focus
        if(viewRecordingsButton.hasFocus())
        {
            activateRecorderIntent();
        }
        else if(viewPicturesButton.hasFocus())
        {
            activateCameraIntent();
        }
    }

    private void getHelpForProtocolItem()
    {
        if(protocolItemsListview.getSelectedItem() != null)
        {
            //create a patientprotocolItem object to get the selected patientProtocolItem
            PatientProtocolItem pi = (PatientProtocolItem) protocolItemsListview.getSelectedItem();
            //send the protocolItemId to the youtube intent to retrieve and play the video

            Intent youtubePlayerIntent = new Intent(getApplicationContext(),HelpVideoActivity.class);
            youtubePlayerIntent.putExtra("protocolItemId",pi.getProtocolItemId());
            startActivity(youtubePlayerIntent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Select a protocolitem!",Toast.LENGTH_SHORT).show();
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
        protected void onBackSwipe()
        {
            moveUp();
        }

        @Override
        protected void onForwardSwipe()
        {
            moveDown();
        }

        @Override
        protected void onNear()
        {
            handleSelection();
        }

        @Override
        protected void onFar()
        {
            getHelpForProtocolItem();
        }

        @Override
        public String toString()
        {
            return super.toString();
        }
    }


    //end inner class myGestureControl


}
