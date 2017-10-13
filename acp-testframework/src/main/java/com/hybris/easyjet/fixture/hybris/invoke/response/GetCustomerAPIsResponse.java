package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.IdentityDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijayapalkayyam on 29/03/2017.
 */
@Getter
@Setter
public class GetCustomerAPIsResponse extends Response {
    private List<IdentityDocument> identityDocuments = new ArrayList<>();
}