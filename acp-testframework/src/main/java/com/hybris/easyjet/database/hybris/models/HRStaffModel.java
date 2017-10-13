package com.hybris.easyjet.database.hybris.models;

import lombok.Data;

/**
 * Created by Siva on 26/1/2016.
 */
@Data
public class HRStaffModel {

    private String hjmpTS;
    private String createdTS;
    private String modifiedTS;
    private String TypePkString;
    private String OwnerPkString;
    private String PK;
    private String p_employeeid;
    private String p_email;
    private String p_active;
    private String p_worktype;
    private String aCLTS;
    private String propTS;

    public HRStaffModel(String hjmpTS, String createdTS, String modifiedTS, String TypePkString, String OwnerPkString, String PK, String p_employeeid, String p_email, String p_active, String p_worktype, String aCLTS, String propTS) {

        this.hjmpTS = hjmpTS;
        this.createdTS = createdTS;
        this.modifiedTS = modifiedTS;
        this.TypePkString = TypePkString;
        this.OwnerPkString = OwnerPkString;
        this.PK = PK;
        this.p_employeeid = p_employeeid;
        this.p_email = p_email;
        this.p_active = p_active;
        this.p_worktype = p_worktype;
        this.aCLTS = aCLTS;
        this.propTS = propTS;
    }

    public HRStaffModel(String p_employeeid, String p_email) {

        this.p_employeeid = p_employeeid;
        this.p_email = p_email;
    }
}



