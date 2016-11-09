package com.example.mileto.healthglass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;

/**
 * Created by mileto.di.marco on 3/11/2016.
 */

public class ProtocolItemsAdapter extends ArrayAdapter<PatientProtocolItem>
{
    //private TextView protocolItem;
    //private CheckBox protocolItemCheckbox;
    private CheckedTextView protocolItem;

    public ProtocolItemsAdapter(Context context, ArrayList<PatientProtocolItem> protocolItems)
    {
        super(context,0, protocolItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Get the data for the item at this position
        PatientProtocolItem patientProtocolItem = getItem(position);
        //check if the view is being reused, otherwise inflate it
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.protocolitems_listview_layout,parent,false);
        }

        //get references to the UI elements to populate them
        protocolItem    = (CheckedTextView) convertView.findViewById(R.id.checkedTextViewProtocolItem);
       // protocolItemCheckbox   = (CheckBox) convertView.findViewById(R.id.checkBoxProtocolItem);
        //populate the data
        protocolItem.setText(patientProtocolItem.getProtocolItemName());
        protocolItem.setChecked(false);
        //protocolItemCheckbox.setChecked(patientProtocolItem.getStatus());
        //return the view to render on the screen

        return convertView;
    }

}
