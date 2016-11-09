package com.example.mileto.healthglass;

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

import java.util.ArrayList;

public class ViewPatientProtocolPerformedActivity extends AppCompatActivity
{
    private Button              viewPicturesButton;
    private Button              viewRecordingsButton;
    private ListView            protocolItemsListview;
    private String              patientIdFromBarcode;
    private String              protocolId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_protocol_to_do);

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
        PatientProtocolItem protocolItem1 = new PatientProtocolItem("remove bandage",true);
        PatientProtocolItem protocolItem2 = new PatientProtocolItem("Clean and desinfect",true);
        PatientProtocolItem protocolItem3 = new PatientProtocolItem("renew bandage",true);

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

    //INNER CLASSESS


    /**
     * this class handles the checkbox setting/unsetting in the listview with protocolItems

    public class CheckboxClickListener implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        {
            CheckedTextView checkedTextview = (CheckedTextView)arg1;
            if(checkedTextview.isChecked())
            {
                //Toast.makeText(getApplicationContext(), "now it is unchecked", Toast.LENGTH_SHORT).show();
                checkedTextview.setBackgroundColor(Color.WHITE);
            }
            else
            {
                checkedTextview.setBackgroundColor(Color.GREEN);
                //Toast.makeText(getApplicationContext(), "now it is checked", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}
