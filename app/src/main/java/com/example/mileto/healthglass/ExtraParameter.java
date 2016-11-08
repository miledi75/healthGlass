package com.example.mileto.healthglass;

/**
 * Created by mileto.di.marco on 8/11/2016.
 * for my Msc project at UH
 */

 class ExtraParameter
{
    private String parameter_name;
    private String parameter_value;

    public ExtraParameter(String parameter_name, String parameter_value)
    {
        this.parameter_name = parameter_name;
        this.parameter_value = parameter_value;
    }

    public String getParameter_name()
    {
        return parameter_name;
    }

    public void setParameter_name(String parameter_name)
    {
        this.parameter_name = parameter_name;
    }

    public String getParameter_value()
    {
        return parameter_value;
    }

    public void setParameter_value(String parameter_value)
    {
        this.parameter_value = parameter_value;
    }
}
