package com.hybris.easyjet.fixture.hybris.invoke.requestbody.registercustomer;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StaffMemberRequest implements IRequestBody {

    private String email;
    private String title;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String employeeEmail;

}