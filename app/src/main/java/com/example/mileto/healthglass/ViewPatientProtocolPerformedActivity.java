package com.example.mileto.healthglass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ViewPatientProtocolPerformedActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_patient_protocol_performed);

        //get the protocolId from the PatientInfoActivity Intent
        Intent patientInfoActivity = getIntent();
        String protocolId = patientInfoActivity.getStringExtra("protocolId");
        Toast.makeText(this,protocolId, Toast.LENGTH_SHORT).show();
    }
}
