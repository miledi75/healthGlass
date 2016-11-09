package com.example.mileto.healthglass;

/**
 * Created by mileto.di.marco on 3/11/2016.
 * Model for the items to be performed within a patients protocol
 */

public class PatientProtocolItem
{
    private String      protocolItemName;
    //true = performed, false = not performed
    private Boolean     status;
    private int         protocolItemId;

    public PatientProtocolItem(String protocolItemName, Boolean status,int Id)
    {
        this.protocolItemName   = protocolItemName;
        this.status             = status;
        this.protocolItemId     = Id;
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

    public int getProtocolItemId()
    {
        return protocolItemId;
    }

    public void setProtocolItemId(int protocolItemId)
    {
        this.protocolItemId = protocolItemId;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }
}
