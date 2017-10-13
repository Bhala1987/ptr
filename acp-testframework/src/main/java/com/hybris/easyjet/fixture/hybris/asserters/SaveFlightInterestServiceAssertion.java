package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.config.constants.FlightInterestConstants;
import com.hybris.easyjet.database.hybris.models.FlightInterestModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.CustomerFlightInterestResponse;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;

/**
 * Created by ptr-kvijayapal on 1/31/2017.
 */

public class SaveFlightInterestServiceAssertion extends Assertion<SaveFlightInterestServiceAssertion, CustomerFlightInterestResponse> {

    private int numberOfHoursBefore;
    private List<CustomerFlightInterestResponse.RegistrationStatus> registrationStatuses;
    private int numberOfRegistrationStatuses;

    public SaveFlightInterestServiceAssertion(CustomerFlightInterestResponse customerRecentSearchesResponse) {

        this.response = customerRecentSearchesResponse;
        this.registrationStatuses = response.getFlightInterestConfirmation().getRegistrationStatus();
        this.numberOfRegistrationStatuses = registrationStatuses.size();
    }

    public SaveFlightInterestServiceAssertion iHaveCorrectValidationStatusesForFlightKeys(List<String> flightKeys) {

        assertNotNull(registrationStatuses);
        assertEquals(registrationStatuses.size(), flightKeys.size());
        registrationStatuses.stream().forEach(flightConfirmation -> {
            assertEquals(flightConfirmation.getIsRegistered(), "true");
            assertTrue(flightKeys.contains(flightConfirmation.getFlightKey()));
        });
        return this;
    }

    public SaveFlightInterestServiceAssertion customerIdIsEqualTo(String expectedCustomerId) {

        assertNotNull(response.getFlightInterestConfirmation().getCustomerId());
        assertEquals(expectedCustomerId, response.getFlightInterestConfirmation().getCustomerId());
        return this;
    }

    public SaveFlightInterestServiceAssertion hrefIsEqualTo(String expectedHref) {

        assertNotNull(response.getFlightInterestConfirmation().getHref());
        assertEquals(expectedHref, response.getFlightInterestConfirmation().getHref());
        return this;
    }

    public SaveFlightInterestServiceAssertion assertANewFlightInterestIsSaved(List<FlightInterestModel> beforeSaving, List<FlightInterestModel> afterSaving, String flightKey, String fare) {

        assertNotNull(afterSaving);
        assertNotNull(flightKey);
        assertNotNull(fare);
        assertTrue(afterSaving.size() == (beforeSaving.size() + 1));
        FlightInterestModel flightInterest = afterSaving.stream().filter(f -> flightKey.equals(f.getFlightKey())
                && fare.equals(f.getFareType())).findFirst().orElse(null);
        assertNotNull(flightInterest);
        afterSaving.remove(flightInterest);
        assertEquals(beforeSaving, afterSaving);
        return this;
    }

    public SaveFlightInterestServiceAssertion iHaveConsistentValidationStatusesForFlightKeys(List<String> flightKeys) {

        assertNotNull(registrationStatuses);
        assertEquals(registrationStatuses.size(), flightKeys.size());
        CustomerFlightInterestResponse.RegistrationStatus invalidRegistration = registrationStatuses.stream()
                .filter(f -> FlightInterestConstants.INVALID_FLIGHTKEY.equals(f.getFlightKey()))
                .findFirst()
                .orElse(null);
        assertNotNull(invalidRegistration);
        assertEquals(invalidRegistration.getIsRegistered(), "false");
        registrationStatuses.remove(invalidRegistration);
        registrationStatuses.stream().forEach(flightConfirmation -> {
            assertEquals(flightConfirmation.getIsRegistered(), "true");
            assertTrue(flightKeys.contains(flightConfirmation.getFlightKey()));
        });
        return this;
    }

    public SaveFlightInterestServiceAssertion assertANewFlightInterestIsSaved(List<FlightInterestModel> beforeSaving, List<FlightInterestModel> afterSaving, Map<String, List<String>> flightInterests) {

        assertNotNull(beforeSaving);
        assertNotNull(afterSaving);
        assertNotNull(flightInterests);
        for (String flightKey : flightInterests.keySet()) {
            for (String fare : flightInterests.get(flightKey)) {
                FlightInterestModel registeredInterest = afterSaving.stream()
                        .filter(fi -> flightKey.equals(fi.getFlightKey()) && fare.equals(fi.getFareType()))
                        .findFirst()
                        .orElse(null);
                assertNotNull(registeredInterest);
                afterSaving.remove(registeredInterest);
            }
        }
        assertEquals(beforeSaving, afterSaving);
        return this;
    }

    public SaveFlightInterestServiceAssertion assertOnlyValidNewFlightInterestAreSaved(List<FlightInterestModel> beforeSaving, List<FlightInterestModel> afterSaving, Map<String, List<String>> flightInterests) {

        flightInterests.remove(FlightInterestConstants.INVALID_FLIGHTKEY);
        return assertANewFlightInterestIsSaved(beforeSaving, afterSaving, flightInterests);
    }

}
