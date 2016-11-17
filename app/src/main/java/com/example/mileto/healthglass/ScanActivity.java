package com.example.mileto.healthglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.VoiceControl;
import android.widget.Toast;

public class ScanActivity extends AppCompatActivity
{

    //logcat tag for debugging purposes
    private String tag = "DEBUG_SCANACTIVITY";
    private Button scanButton;

    //declaring a gestureSensor object
    private MyGestureControl mGc;

    //declaring a voicecontrol object
    private MyVoiceControl mVC;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //initialize scanbutton and set selected
        scanButton = (Button) findViewById(R.id.scanCode);
        scanButton.setSelected(true);

        //activate voice control
        try
        {
            mVC = new MyVoiceControl(getApplicationContext());
            if(mVC != null)
            {
                mVC.on();
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
                    //GestureSensor.On();
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
            try
            {
                //GestureSensor.On();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public void displayVoiceCommand(String voiceCommand)
    {
        Toast.makeText(this,voiceCommand,Toast.LENGTH_SHORT);

    }


    @Override
    public void onResume()
    {
        super.onResume();
        //activate voice control
        try
        {
            if (mVC != null)
            {
                mVC.on();
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
                //GestureSensor.On();
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
        //disactivate voicecontrol
        try
        {
            if(mVC != null)
            {
                mVC.off();
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

    public void HandleClick(View arg0)
    {

        Intent patientInfoActivityIntent = new Intent(this,PatientInfoActivity.class);
        //add patientId info variable to intent
        patientInfoActivityIntent.putExtra("patientId","3145891164206");
        //call the patientInfoActivity
        startActivity(patientInfoActivityIntent);
        //UNCOMMENT TO ACTIVQTE SCANNER INTENT, COMMENTED FOR TESTING PURPOSES
        /*Call the Zxing google scanner through intent
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");

        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");

        try
        {
            startActivityForResult(intent, 0); //Barcode Scanner to scan for us
        }
        catch(Exception e)
        {
            Log.i(tag,e.getMessage());
        }*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == 0)
        {
            //TextView tvStatus=(TextView)findViewById(R.id.tvStatus);
            TextView barcodeResult =(TextView)findViewById(R.id.barcodeResult);
            if (resultCode == RESULT_OK)
            {
                //get the patient id fro; the barcode
                String patientId = intent.getStringExtra("SCAN_RESULT");
                //Log.d("SCANNED CODE:",patientId);
                //create intent to call patientInfoActivity
                Intent patientInfoActivityIntent = new Intent(this,PatientInfoActivity.class);
                //add patientId info variable to intent
                patientInfoActivityIntent.putExtra("patientId",patientId);
                //call the patientInfoActivity
                startActivity(patientInfoActivityIntent);
            }
            else if (resultCode == RESULT_CANCELED)
            {
                //tvStatus.setText("Press a button to start a scan.");
                barcodeResult.setText("Scan activation failed...Please try again");
            }
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        //disactivate voice control
        try
        {
            if(mVC != null)
            {
                mVC.off();
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

        }

        @Override
        protected void onForwardSwipe(int i)
        {

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

    private void handleSelection()
    {
        scanButton.callOnClick();
    }
    //end inner class myGestureControl
}
