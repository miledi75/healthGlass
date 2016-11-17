package com.example.mileto.healthglass;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.VoiceControl;

import java.io.File;
import java.util.ArrayList;

public class ViewPatientProtocolToDoActivity extends AppCompatActivity {

    private Button              pictureButton;
    private Button              audioRecordButton;
    private ListView            protocolItemsListview;
    private String              patientIdFromBarcode;
    private String              protocolId;
    private MyVoiceControl      mVc;
    private MyGestureControl    mGc;
    private int                 numberOfProtocolItems;
    private int                 numberOfProtocolItemsPerformed;
    private AlertDialog.Builder goHomeDilaogBuilder;
    private AlertDialog         goHomeDialog;

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
        pictureButton           = (Button) findViewById(R.id.buttonAddPicture);
        audioRecordButton       = (Button) findViewById(R.id.buttonAddRecording);
        protocolItemsListview   = (ListView) findViewById(R.id.listViewProtocolItems);

        //add the action listeners for the buttons

        pictureButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                activateCameraIntent();
                //call the intent for the pictures
            }
        });


        audioRecordButton.setOnClickListener(new View.OnClickListener() {
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
        PatientProtocolItem protocolItem1 = new PatientProtocolItem("tBasisprocedure (DAV)",false,1);
        PatientProtocolItem protocolItem2 = new PatientProtocolItem("Opnemen staal wondvocht voor cultuur",false,2);
        PatientProtocolItem protocolItem3 = new PatientProtocolItem("tPeilen van wonde",false,3);

        //update the adapter
        protocolItemsAdapter.add(protocolItem1);
        protocolItemsAdapter.add(protocolItem2);
        protocolItemsAdapter.add(protocolItem3);

        //get the number of protocolitems to be performed
        numberOfProtocolItems = protocolItemsAdapter.getCount();
        //allow multiple selections of checkboxes
        protocolItemsListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //set the onitemclicklistener to the custom clicklistener for the checkboxes
        protocolItemsListview.setOnItemClickListener(new CheckboxClickListener());

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
        //call the activity
        startActivity(recordCommentIntent);
    }

    //INNER CLASSESS

    /**
     * this class handles the checkbox setting/unsetting in the listview with protocolItems
     */
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
    }

    /**
     * this function handles the selection through voicecontrol or gesturecontrol
     */
    public void handleSelection()
    {
        //check if buttons are selected/have focus
        if(audioRecordButton.hasFocus())
        {
            activateRecorderIntent();
        }
        else if(pictureButton.hasFocus())
        {
            activateCameraIntent();
        }
        else if(protocolItemsListview.hasFocus())
        {
            //get the number of items already performed in the protocol list
            numberOfProtocolItemsPerformed = protocolItemsListview.getCheckedItemCount();
            Toast.makeText(getApplicationContext(),Integer.toString(numberOfProtocolItemsPerformed),Toast.LENGTH_SHORT);
            //compare the nuberperformed to the number to perform
            if(numberOfProtocolItemsPerformed == numberOfProtocolItems)
            {
                //Protocol is finished, display message
                Toast.makeText(getApplicationContext(),"Protocol completed",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //get the first item that has focus or call the itemclicklistener
                protocolItemsListview.performItemClick(protocolItemsListview.getChildAt(protocolItemsListview.getSelectedItemPosition()),protocolItemsListview.getSelectedItemPosition(),protocolItemsListview.getItemIdAtPosition(protocolItemsListview.getSelectedItemPosition()));
                //go to the next item
                protocolItemsListview.setSelection(protocolItemsListview.getSelectedItemPosition()+1);
            }
        }
    }

    /**
     * function that reacts to the helprequest for a specific protocolItem
     * called by voiceControl or gestureControl
     * uses the proocolItemId to get a youtube identifier from a REST server call
     */
    public void getHelpForProtocolItem()
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
                startActivity(in);
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
                pictureButton.requestFocus();
            }
            else // go to the next protocolitem
            {
                protocolItemsListview.setSelection(protocolItemsListview.getSelectedItemPosition()+1);
            }
        }
        //check if the imagesbutton is selected => go to the commentbutton
        else if(pictureButton.hasFocus())
        {
            audioRecordButton.requestFocus();
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
                pictureButton.requestFocus();
            }
            else // go to the previous protocolitem
            {
                protocolItemsListview.setSelection(protocolItemsListview.getSelectedItemPosition()-1);
            }
        }
        //check if the imagesbutton is selected => go to the listview
        else if(pictureButton.hasFocus())
        {
            protocolItemsListview.requestFocus();
        }
        else //comment button has focus: go to the imagesButton
        {
            pictureButton.requestFocus();
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

            if(this.command.equals("stop"))
            {
                //call the dialog to query user to go home
                goHome();
            }

            if(this.command.equals("go back"))
            {
                finish();
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
