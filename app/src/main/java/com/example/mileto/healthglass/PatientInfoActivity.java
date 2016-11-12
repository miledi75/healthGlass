package com.example.mileto.healthglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class PatientInfoActivity extends AppCompatActivity
{

        private String patientId;
        private ListView protocolList;
        private MyVoiceControl mVc;
        private MyGestureControl mGc;
        private Button extraInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);


        //initialize voice control
        try
        {
            mVc = new MyVoiceControl(getApplicationContext());
            if(mVc != null)
            {
                mVc.on();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //get the patientId from the ScanActivity intent
        Intent scanActivityIntent = getIntent();
        this.patientId = scanActivityIntent.getStringExtra("patientId");
        //Toast patientIdMessage = Toast.makeText(this,this.patientId,Toast.LENGTH_SHORT);
        //patientIdMessage.show();

        //initialize button for extraInfo
        extraInfo = (Button) findViewById(R.id.extraParametersButton);

        //initialize listview for protocols
        protocolList = (ListView) findViewById(R.id.protocolListView);

        //initialize datasource
        ArrayList<PatientProtocol> patientProtocols = new ArrayList<PatientProtocol>();


        //initialize adapter
        ProtocolAdapter protocolAdapter = new ProtocolAdapter(this,patientProtocols);
        //attach the adapter to the listview
        protocolList.setAdapter(protocolAdapter);

        //populate the array with values

        PatientProtocol p1 = new PatientProtocol("Clean wound","COMPLETED",1);
        PatientProtocol p2 = new PatientProtocol("wound checkup","COMPLETED",2);
        PatientProtocol p3 = new PatientProtocol("Bandage checkup","TODO",3);

        protocolAdapter.add(p1);
        protocolAdapter.add(p2);
        protocolAdapter.add(p3);

        //Select the first item in the listview
        protocolList.requestFocus();
        protocolList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        protocolList.setItemChecked(0,true);

        //set the clicklistener for the extraInfo button
        extraInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent extraParameters = new Intent(getApplicationContext(),ExtraParametersActivity.class);
                extraParameters.putExtra("patientId",patientId);
                startActivity(extraParameters);

            }
        });

        //Set the clicklistener for the listview
        protocolList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedFromList = protocolList.getItemAtPosition(position).toString();
                String strPosition = Integer.toString(position);
                if (selectedFromList.matches("(?i).*Completed"))
                {//LOAD COMPLETED PROTOCOL ACITIVITY
                    // Toast.makeText(getApplicationContext(),selectedFromList, Toast.LENGTH_SHORT).show();
                    Intent protocolPerformedActivityIntent = new Intent(getApplicationContext(),ViewPatientProtocolPerformedActivity.class);
                    //add patientId info variable to intent
                    //for testing purposes
                    protocolPerformedActivityIntent.putExtra("protocolId","2");
                    //protocolPerformedActivityIntent.putExtra("protocolId",strPosition);
                    protocolPerformedActivityIntent.putExtra("patientId",patientId);
                    //call the patientInfoActivity
                    startActivity(protocolPerformedActivityIntent);
                }
                else
                {
                 //LOAD TO DO PROTOCOL ACTIVITY
                    Intent protocolToDoActivityIntent = new Intent(getApplicationContext(),ViewPatientProtocolToDoActivity.class);
                    //add patientId info variable to intent
                    protocolToDoActivityIntent.putExtra("protocolId",strPosition);
                    //for testing purposes
                    protocolToDoActivityIntent.putExtra("patientId","2");
                    //protocolToDoActivityIntent.putExtra("patientId",patientId);
                    //call the patientInfoActivity
                    startActivity(protocolToDoActivityIntent);
                }
             }
        });

    }


    /**
     * Uses the patientId to call the REST service and retrieve general patient data
     *
     */
    public void getPatientGeneralInfo()
    {

    }

    /**
     *retrieves the protocols that have been performed and
     * the ones that still have to be performed
     */
    public void getPatientProtocols()
    {

    }





    public void displayVoiceCommand(String voiceCommand)
    {
        Toast.makeText(this,voiceCommand,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPause()
    {
        try
        {
            if(mVc != null)
            {
                mVc.off();
            }
            super.onPause();
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume()
    {
        try
        {
            if (mVc != null)
            {
                mVc.on();
            }
            super.onResume();
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy()
    {
        try
        {
            if(mVc != null)
            {
                mVc.off();
                mVc = null;
            }
            super.onDestroy();
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    //Vuzix voice control class

    public class MyVoiceControl extends VoiceControl
    {
        private String command;
        private Activity callingActivity;
        private Context activityContext;

        public MyVoiceControl(Context context)
        {
            super(context);
            this.activityContext = context;
        }


        protected void onRecognition(String result)
        {
            //variables for item postition in the listview
            int numberOfItems;
            int itemPosition;
            //get the total number of items in the list
            numberOfItems = protocolList.getCount();
            //get the position of the selected item
            itemPosition = protocolList.getSelectedItemPosition();
            this.command = result;
            //displayVoiceCommand(this.command);
            if(this.command.equals("select"))
            {
                //check if parameter button is selected/has focus
                if(extraInfo.hasFocus())
                {
                    extraInfo.callOnClick();
                    //displayVoiceCommand(this.command);
                }
                else //call the onitemClickListener of the selected item
                {
                    protocolList.performItemClick(protocolList.getSelectedView(),protocolList.getSelectedItemPosition(),protocolList.getSelectedItemId());
                    displayVoiceCommand(this.command);
                }
            }
            //handle movements up and down in the list
            if(this.command.equals("go down"))
            {
                //check if the selected item is the last one in the list
                if(itemPosition == numberOfItems-1)
                {
                    //move to the first item
                    protocolList.setSelection(0);
                    protocolList.setItemChecked(0,true);
                    displayVoiceCommand(this.command);
                    Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //move to the next item
                    protocolList.setSelection(itemPosition+1);
                    protocolList.setItemChecked(itemPosition+1,true);
                    displayVoiceCommand(this.command);
                    Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
                }
            }
            if(this.command.equals("go up"))
            {
                //check if the selected item is the first one in the list
                if(itemPosition == 0)
                {
                    //move to the last item
                    protocolList.setSelection(numberOfItems-1);
                    protocolList.setItemChecked(numberOfItems-1,true);
                    displayVoiceCommand(this.command);
                    Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //move to the previous item
                    protocolList.setSelection(itemPosition-1);
                    protocolList.setItemChecked(itemPosition-1,true);
                    displayVoiceCommand(this.command);
                    Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
                }
            }
            //detect right movement to select button for extra patient parameters
            if(this.command.equals("go right"))
            {
                displayVoiceCommand(this.command);
                extraInfo.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                extraInfo.setFocusableInTouchMode(true);
                extraInfo.requestFocus();
                extraInfo.setSelected(true);
                //extraInfo.callOnClick();
                extraInfo.setSelected(false);

            }
            if(this.command.equals("go left"))
            {
                protocolList.requestFocus();
                protocolList.setItemChecked(0,true);
            }



        }

        @Override
        public String toString()
        {
            return command;
        }
    }     //end of Vuzix voice control class

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

        }

        @Override
        protected void onFar()
        {

        }

        @Override
        public String toString()
        {
            return super.toString();
        }
    }
    //end inner class myGestureControl

}
