package com.example.mileto.healthglass;

/**
 * Created by mileto.di.marco on 3/11/2016.
 * Model for the items to be performed within a patients protocol
 */

public class PatientProtocolItem
{
    private String  protocolItemName;
    //true = performed, false = not performed
    private Boolean     status;

    public PatientProtocolItem(String protocolItemName, Boolean status)
    {
        this.protocolItemName = protocolItemName;
        this.status = status;
    }

    public String getProtocolItemName()
    {
        return protocolItemName;
    }

    public void setProtocolItemName(String protocolItemName)
    {
        this.protocolItemName = protocolItemName;
    }

    public Boolean getStatus()
    {
        return status;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }
}
