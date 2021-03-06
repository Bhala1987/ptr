package com.hybris.easyjet.fixture.hybris.invoke.requestbody.customer.registercustomer;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@XmlRootElement
public class RegisterCustomerRequestBody implements IRequestBody {
    private PersonalDetails personalDetails;
    private List<ContactAddress> contactAddress;
    private List<String> optedOutMarketing;
    private List<String> preferredAirports;
}