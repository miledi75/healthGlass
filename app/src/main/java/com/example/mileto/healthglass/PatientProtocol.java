package com.example.mileto.healthglass;



import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mileto on 27/09/2016.
 * Model for the protocol of a patient
 */

public class PatientProtocol
{
    private String  protocolName;
    private String  protocolDate;
    private String  protocolStatus;
    private int     protocolId;

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }



    //constructor
    public PatientProtocol(String name,String status, int id)
    {
        setProtocolName(name);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        setProtocolDate(dateFormat.format(date));
        setProtocolStatus(status);
        setProtocolId(id);
    }
    public String getProtocolStatus() {
        return protocolStatus;
    }

    public void setProtocolStatus(String protocolStatus)
    {
        this.protocolStatus = protocolStatus;
    }
    public String getProtocolName()
    {
        return protocolName;
    }

    public String getProtocolDate()
    {
        return protocolDate;
    }

    public void setProtocolName(String protocolName)
    {
        this.protocolName = protocolName;
    }

    public void setProtocolDate(String protocolDate)
    {
        this.protocolDate = protocolDate;
    }

    @Override
    public String toString()
    {
        return this.protocolName + ": " + this.protocolDate + ": "+ this.getProtocolStatus();
    }
}


