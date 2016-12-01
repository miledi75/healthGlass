package com.example.mileto.healthglass;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mileto.di.marco on 8/11/2016.
 */

public class ExtraParameterAdapter extends ArrayAdapter<ExtraParameter>
{
    private TextView extraParameterName;
    private TextView extraParameterValue;

    public ExtraParameterAdapter(Context context, ArrayList<ExtraParameter> extraParameters)
    {
        super(context, 0,extraParameters);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Get the data for the item at this position
        ExtraParameter extraParameter = getItem(position);
        //check if the view is being reused, otherwise inflate it
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.extra_parameters_listview_layout,parent,false);
        }

        //get references to the UI elements to populate them
        extraParameterName  = (TextView) convertView.findViewById(R.id.textview_parameter_label);
        extraParameterValue = (TextView) convertView.findViewById(R.id.textview_parameter_text);

        //populate the data
        extraParameterName.setText(extraParameter.getParameter_name());
        extraParameterValue.setText(extraParameter.getParameter_value());
        extraParameterName.setTextColor(Color.BLACK);
        extraParameterValue.setTextColor(Color.BLACK);

        //return the view to render on the screen

        return convertView;
    }
}
