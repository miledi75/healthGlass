package com.example.mileto.healthglass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

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
    private AlertDialog.Builder goHomeDilaogBuilder;
    private AlertDialog         goHomeDialog;

    private VoiceControl        mVc;
    private GestureSensor       mGc;

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

        //Voicecontrol and gesturecontrol
        //activate voice control
        try
        {
            mVc = new MyVoiceControl(getApplicationContext());
            if(mVc != null)
            {
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

    /**
     * takes user back to homepage
     */
    private void goHome()
    {
        //present a dialog to query user
        //build a user dialog
        goHomeDilaogBuilder = new AlertDialog.Builder(this);
        goHomeDilaogBuilder.setMessage("Go to scan page?");
        //positive clicklistener
        goHomeDilaogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //go back to the home page
                Intent in = new Intent(getApplicationContext(),ScanActivity.class);
            }
        });

        //negative clickListener
        goHomeDilaogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        //create the dialog and show it
        goHomeDialog = goHomeDilaogBuilder.create();
        goHomeDialog.show();
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

            if(this.command.equals("go back"))
            {
                finish();
            }
            if(this.command.equals("go up"))
            {
                moveUp();
            }
            if(this.command.equals("stop"))
            {
                //call the dialog to query user to go home
                goHome();
            }

            if(this.command.equals("cancel"))
            {
                //check if dialog is activated
                if (goHomeDialog.isShowing())
                {
                    goHomeDialog.dismiss();
                }

            }
            if (this.command.equals("go"))
            {
                //check if dialog is activated
                if (goHomeDialog.isShowing())
                {
                    //activate the click event of the yes button
                    goHomeDialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
                }
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
            super.onPause();
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
            moveUp();
        }

        @Override
        protected void onForwardSwipe(int i)
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
            finish();
        }

        @Override
        public String toString()
        {
            return super.toString();
        }
    }


    //end inner class myGestureControl


}
