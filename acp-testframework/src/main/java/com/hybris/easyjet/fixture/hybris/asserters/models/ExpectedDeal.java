package com.hybris.easyjet.fixture.hybris.asserters.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpectedDeal {
    private String pk;
    private String SystemName;
    private String CorporateId;
    private String OfficeId;
}
