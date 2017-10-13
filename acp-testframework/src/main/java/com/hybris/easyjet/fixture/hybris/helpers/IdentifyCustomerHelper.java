package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.IdentifyCustomerQueryParams;
import org.springframework.stereotype.Component;

/**
 * Created by dwebb on 12/5/2016.
 */
@Component
public class IdentifyCustomerHelper {

    public IdentifyCustomerHelper() {
//        empty
    }

    public IdentifyCustomerQueryParams getParamsFor(String field, CustomerModel customer) {

        switch (field.toLowerCase()) {
            case "firstname":
                return IdentifyCustomerQueryParams.builder().firstname(customer.getFirstname()).build();
            case "lastname":
                return IdentifyCustomerQueryParams.builder().lastname(customer.getLastname()).build();
            case "title":
                return IdentifyCustomerQueryParams.builder().title(customer.getTitle()).build();
            default:
                break;
        }

        return null;

    }

    public IdentifyCustomerQueryParams queryParamsFromCustomer(CustomerModel customer, String casing) {

        CaseConverter converter = new CaseConverter();

        return IdentifyCustomerQueryParams.builder()
                //TODO: Fix when customer returns valid data via DAO - currently email. title and postcode not working
//                .title(converter.convert(customer.getTitle(), casing))
                .firstname(converter.convert(customer.getFirstname(), casing))
                .lastname(converter.convert(customer.getLastname(), casing))
//                .email(converter.convert(customer.getEmail(), casing))
//                .postcode(converter.convert(customer.getPostalcode(), casing))
                .build();
    }

}
