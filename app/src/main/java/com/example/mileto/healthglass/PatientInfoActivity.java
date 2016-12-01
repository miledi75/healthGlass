package com.example.mileto.healthglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.srec.Recognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.Constants;
import com.vuzix.speech.VoiceControl;

import java.util.ArrayList;

public class PatientInfoActivity extends AppCompatActivity
{

        private String patientId;
        private ListView protocolList;
        private MyVoiceControl mVc;
        private MyGestureControl mGc;
        private Button extraInfoButton;


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
                mVc.addGrammar(Constants.GRAMMAR_WAREHOUSE);
                mVc.on();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //initialize gestureControl
        try
        {
            mGc = new MyGestureControl(this);
            if(mGc != null)
            {
                mGc.register();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //get the patientId from the ScanActivity intent
        Intent scanActivityIntent = getIntent();
        this.patientId = scanActivityIntent.getStringExtra("patientId");
        //Toast patientIdMessage = Toast.makeText(this,this.patientId,Toast.LENGTH_SHORT);
        //patientIdMessage.show();

        //initialize button for extraInfoButton
        extraInfoButton = (Button) findViewById(R.id.extraParametersButton);

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

        //set the clicklistener for the extraInfoButton button
        extraInfoButton.setOnClickListener(new View.OnClickListener()
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
        //deactivate voice
        try
        {
            if(mVc != null)
            {
                Toast.makeText(getApplicationContext(),"Pausing voiceControl",Toast.LENGTH_SHORT).show();
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
        super.onPause();
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
                Toast.makeText(getApplicationContext(),"Resuming voiceControl",Toast.LENGTH_SHORT).show();
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
        //deactivate voiceControl
        try
        {
            if(mVc != null)
            {
                //Toast.makeText(getApplicationContext(),"Destroying voiceControl",Toast.LENGTH_SHORT).show();
                mVc.destroy();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //unregister gestureControl
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
        super.onDestroy();
    }

    public void handleSelection()
    {
        //check if parameter button is selected/has focus
        if(extraInfoButton.hasFocus())
        {
            extraInfoButton.performClick();
            //displayVoiceCommand(this.command);
        }
        else //call the onitemClickListener of the selected item
        {
            protocolList.performItemClick(protocolList.getSelectedView(),protocolList.getSelectedItemPosition(),protocolList.getSelectedItemId());
        }
    }
    public void moveDown()
    {
        //variables for item postition in the listview
        int numberOfItems;
        int itemPosition;
        //get the total number of items in the list
        numberOfItems = protocolList.getCount();
        //get the position of the selected item
        itemPosition = protocolList.getSelectedItemPosition();


        //check if the selected item is the last one in the list
        if(itemPosition == numberOfItems-1)
        {
            //move to the first item
            protocolList.setSelection(0);
            protocolList.setItemChecked(0,true);
            //Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
        }
        else
        {
            //move to the next item
            protocolList.setSelection(itemPosition+1);
            protocolList.setItemChecked(itemPosition+1,true);
            //Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
        }
    }

    public void moveUp()
    {
        //variables for item postition in the listview
        int numberOfItems;
        int itemPosition;
        //get the total number of items in the list
        numberOfItems = protocolList.getCount();
        //get the position of the selected item
        itemPosition = protocolList.getSelectedItemPosition();

        //check if the selected item is the first one in the list
        if(itemPosition == 0)
        {
            //move to the last item
            protocolList.setSelection(numberOfItems-1);
            protocolList.setItemChecked(numberOfItems-1,true);
            //Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
        }
        else
        {
            //move to the previous item
            protocolList.setSelection(itemPosition-1);
            protocolList.setItemChecked(itemPosition-1,true);
            //Toast.makeText(getApplicationContext(),Integer.toString(protocolList.getSelectedItemPosition()),Toast.LENGTH_SHORT).show();
        }
    }
    public void moveLeft()
    {
        protocolList.requestFocus();
        protocolList.setItemChecked(0,true);
    }
    public void moveRight()
    {
        extraInfoButton.playSoundEffect(android.view.SoundEffectConstants.CLICK);
        extraInfoButton.setFocusableInTouchMode(true);
        extraInfoButton.requestFocus();
        extraInfoButton.setSelected(true);
        extraInfoButton.setSelected(false);
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
                handleSelection();
                /*check if parameter button is selected/has focus
                if(extraInfoButton.hasFocus())
                {
                    extraInfoButton.callOnClick();
                    //displayVoiceCommand(this.command);
                }
                else //call the onitemClickListener of the selected item
                {
                    protocolList.performItemClick(protocolList.getSelectedView(),protocolList.getSelectedItemPosition(),protocolList.getSelectedItemId());
                    displayVoiceCommand(this.command);
                }*/
            }
            //handle movements up and down in the list
            if(this.command.equals("go down"))
            {
                moveDown();
                /*check if the selected item is the last one in the list
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
                }*/
            }
            if(this.command.equals("go up"))
            {
                moveUp();
                /*check if the selected item is the first one in the list
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
                }*/
            }
            //detect right movement to select button for extra patient parameters
            if(this.command.equals("go right"))
            {
                /*displayVoiceCommand(this.command);
                extraInfoButton.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                extraInfoButton.setFocusableInTouchMode(true);
                extraInfoButton.requestFocus();
                extraInfoButton.setSelected(true);
                //extraInfoButton.callOnClick();
                extraInfoButton.setSelected(false);*/
                moveRight();

            }
            if(this.command.equals("go left"))
            {
                /*protocolList.requestFocus();
                protocolList.setItemChecked(0,true);*/
                moveLeft();
            }
            if(this.command.equals("go Back"))
            {
                finish();
            }
            if(this.command.equals("load"))
            {
                extraInfoButton.performClick();
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
            //@todo check how to implement moveleft and moveright
          //  moveUp();
            Toast.makeText(getApplicationContext(),"OnbackSwipe",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onForwardSwipe(int i)
        {
            moveDown();
            Toast.makeText(getApplicationContext(),"OnForwardSwipe",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onNear()
        {
            handleSelection();
            Toast.makeText(getApplicationContext(),"OnNear",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onFar()
        {
            finish();
            Toast.makeText(getApplicationContext(),"OnFar",Toast.LENGTH_SHORT).show();
        }

        @Override
        public String toString()
        {
            return super.toString();
        }
    }
    //end inner class myGestureControl

}
