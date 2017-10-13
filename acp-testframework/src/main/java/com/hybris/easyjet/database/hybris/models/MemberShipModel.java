package com.hybris.easyjet.database.hybris.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tejal on 13/05/17.
 */
@Builder
@Getter @Setter
public class MemberShipModel {
    private String ejMemberShipNumber;
    private String firstname;
    private String lastname;
    private String expiryDate;
    private String status;
}