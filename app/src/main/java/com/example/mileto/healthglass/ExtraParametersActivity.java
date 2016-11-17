package com.example.mileto.healthglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//import com.vuzix.hardware.GestureSensor;
//import com.vuzix.speech.VoiceControl;

import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.VoiceControl;

import java.util.ArrayList;


public class ExtraParametersActivity extends Activity
{

    private String patientId;
    private ListView extraParameterList;
    private MyVoiceControl mVc;
    private GestureSensor mGc;
    private Button closeButton;
    ArrayList<ExtraParameter> parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_parameters);

        //get the patientID from the PatientInfoActivity Intent
        Intent intent = getIntent();
        patientId = intent.getStringExtra("patientId");

        //@todo call the REST service for the extra parameters using the patientId

        //initialize UI elements
        extraParameterList  = (ListView)    findViewById(R.id.extraParametersListview);
        closeButton         = (Button)      findViewById(R.id.extraParametersBackButton);

        //fill the parameter arrayList

        /**
         *
         ID
         Geboortedatum (eerste drie worden meestel gebruikt om een patiënt te kunnen identificeren)
         Reden van opname
         Kamernummer
         NTR-code (niet te reanimeren)
         bloedruk (laatste gemeten), hartslag (laatste gemeten), temperatuur (laatst gemeten), ademhalingsfrequentie (laatst gemeten), gewicht (laatst gemeten) en lengte

         */

        //set the datasource
        parameters = new ArrayList<ExtraParameter>();

        //declare the arrayadapter
        ExtraParameterAdapter adapter = new ExtraParameterAdapter(getApplicationContext(),parameters);

        //bind the adapter tot the listView
        extraParameterList.setAdapter(adapter);


        //fill the array with parameters
        parameters.add(new ExtraParameter("ID:",patientId));
        parameters.add(new ExtraParameter("Birth date:","19/02/1975"));
        parameters.add(new ExtraParameter("Admitted for:","Ischemic"));
        parameters.add(new ExtraParameter("Room:","102"));
        parameters.add(new ExtraParameter("NTR","y"));
        parameters.add(new ExtraParameter("Blood pressure","13/9"));
        parameters.add(new ExtraParameter("Heart rate","65"));
        parameters.add(new ExtraParameter("Temp","37.2"));
        parameters.add(new ExtraParameter("Weight","75kg"));
        parameters.add(new ExtraParameter("Length","175cm"));
        parameters.add(new ExtraParameter("Resp.rate","15bpm"));



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

        //activate gestureControl
        //check if gesturesensor is on
        if(GestureSensor.isOn())
        {
            try
            {
                mGc = new MyGestureControl(getApplicationContext());
                if (mGc == null)
                {
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
        }


        //Set the buttonCLicklistener to return to previous activity
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

    }


    //take care off voice and gesture deactivation when activity loses focus
    @Override
    public void onPause()
    {
        super.onPause();
        //disactivate voice control
        try
        {
            if(mVc != null)
            {
                mVc.off();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
        }

        //disactivate gestureControl
        try
        {
            if(mGc != null)
            {
                mGc.unregister();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
        }
    }

    //reactivate voiceControl and gestureControl when acitivity regains focus
    @Override
    public void onResume()
    {
        super.onResume();
        //activate voice control
        try
        {
            if(mVc != null)
            {
                mVc.on();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
        }

        //activate gestureControl
        try
        {
            if(mGc != null)
            {
                mGc.register();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //disactivate voice control
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
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
        }

        //disactivate gestureControl
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
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
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
            if(this.command.equals("go back"))
            {
                //Go back to PatientInfoActivity
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
        protected void onBackSwipe(int i) {

        }

        @Override
        protected void onForwardSwipe(int i) {

        }

        @Override
        protected void onNear() {

        }

        @Override
        protected void onFar()
        {

        }
    }
    //end inner class myGestureControl




}
