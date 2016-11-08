package com.example.mileto.healthglass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mileto.di.marco on 3/11/2016.
 * for my Msc project at UH
 */

public class ProtocolAdapter extends ArrayAdapter<PatientProtocol>
{
    private TextView protocol;

    public ProtocolAdapter(Context context, ArrayList<PatientProtocol> protocols)
    {
        super(context, 0,protocols);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Get the data for the item at this position
        PatientProtocol patientProtocol = getItem(position);
        //check if the view is being reused, otherwise inflate it
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.protocol_listview_layout,parent,false);
        }

        //get references to the UI elements to populate them
        protocol = (TextView) convertView.findViewById(R.id.textviewProtocol);

        //populate the data
        protocol.setText(patientProtocol.toString());

        //protocolItemCheckbox.setChecked(patientProtocolItem.getStatus());
        //return the view to render on the screen

        return convertView;
    }
}
