package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.FlightInterestModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by antonellospiggia on 22/02/2017.
 */
public class FlightInterestErrorAssertion extends ErrorsAssertion {
    public FlightInterestErrorAssertion(Errors errors) {
        super(errors);
    }

    public FlightInterestErrorAssertion assertNoNewFlightInterestIsSaved(List<FlightInterestModel> beforeSaving, List<FlightInterestModel> afterSaving) {
        assertEquals(beforeSaving, afterSaving);
        return this;
    }


}
