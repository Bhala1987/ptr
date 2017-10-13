package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.FlightInterestConstants;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.FlightInterestDao;
import com.hybris.easyjet.database.hybris.models.FlightInterestModel;
import com.hybris.easyjet.database.hybris.models.HybrisFlightDbModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.SaveFlightInterestPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.FlightInterestRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.RemoveFlightInterestRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.RemoveFlightInterestRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.flightInterest.SaveFlightInterestRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ManageFlightInterestRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.ProfileRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.RemoveFlightInterestRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.GET_FLIGHT_INTEREST;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.CustomerPathParams.CustomerPaths.PROFILE;
import static com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.RegisterCustomerFactory.getRandomEmail;


@Component
public class FlightInterestHelper {

    @Autowired
    private FlightFinder flightFinder;
    @Autowired
    private FlightInterestDao flightInterestDao;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private CustomerHelper customerHelper;
    @Autowired
    private StaffMembertoCustomerProfileAssociationHelper staffMembertoCustomerProfileAssociationHelper;
    @Autowired
    private FlightHelper flightHelper;

    private SaveFlightInterestService saveFlightInterestService;
    private GetFlightInterestService getFlightInterestService;
    private RemoveFlightInterestService removeFlightInterestService;
    private HybrisServiceFactory serviceFactory;
    private String INVALID = "invalid";

    @Autowired
    public FlightInterestHelper(HybrisServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public SaveFlightInterestService getFlightInterestService() {
        return saveFlightInterestService;
    }

    public List<FlightInterestModel> getSavedFlightInterestsFor(String customerId) {
        return flightInterestDao.getFlightInterestsForACustomer(customerId);
    }

    public List<HybrisFlightDbModel> getNValidFlights(int interestNumber) throws EasyjetCompromisedException {
        List<HybrisFlightDbModel> validFlights = flightFinder.findNValidFlights();

        if (validFlights.size() < interestNumber - 1) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        return getSortedListForDepartureTime(validFlights).subList((validFlights.size() - 1 - interestNumber), (validFlights.size() - 1));
    }

    private static List<HybrisFlightDbModel> getSortedListForDepartureTime(List<HybrisFlightDbModel> validFlights) {
        Collections.sort(validFlights);
        return validFlights;
    }

    public void addFlightInterests(List<HybrisFlightDbModel> validFlights, String customerId, String channel) {
        Map<String, List<String>> interest = new HashMap<String, List<String>>();
        for (HybrisFlightDbModel flight : validFlights) {
            interest.put(flight.getFlightKey(), Collections.singletonList(FlightInterestConstants.STAFF_FARE));
        }
        addFlightInterestToProfile(customerId, interest, channel);
    }

    public Map<String, List<String>> creatMapOfFlighInterestsToAdd(List<HybrisFlightDbModel> validFlights, List<String> fares) {
        Map<String, List<String>> interests = new HashMap<String, List<String>>();
        for (HybrisFlightDbModel flight : validFlights) {
            interests.put(flight.getFlightKey(), fares);
        }
        return interests;
    }

    private Map<String, List<String>> creatMapOfFlighInterestsToAddAndRemove(List<FindFlightsResponse.Flight> validFlights, List<String> fares) {
        Map<String, List<String>> interests = new HashMap<String, List<String>>();
        for (FindFlightsResponse.Flight flight : validFlights) {
            interests.put(flight.getFlightKey(), fares);
        }
        return interests;
    }

    private Map<String, List<String>> creatMapOfFlighInterestsToAddFromSimpleList(List<String> validFlights, List<String> fares) {
        Map<String, List<String>> interests = new HashMap<String, List<String>>();
        for (String flightKey : validFlights) {
            interests.put(flightKey, fares);
        }
        return interests;
    }

    public void addFlightWithMultipleInterests(Map<String, List<String>> interests, String customerId, String channel) {
        addFlightInterestToProfile(customerId, interests, channel);
    }

    private void addFlightInterestToProfile(String customerId, Map<String, List<String>> interest, String channel) {
        SaveFlightInterestPathParams pathParams = SaveFlightInterestPathParams.builder().customerId(customerId).build();
        FlightInterestRequestBody saveFlightInterestRequestBody = SaveFlightInterestRequestBodyFactory.getSaveFlightInterestRequestBody(interest);
        testData.setData("AddFlightInterestRequestBody", saveFlightInterestRequestBody);
        saveFlightInterestService = serviceFactory.saveFlightInterest(new ManageFlightInterestRequest(getMinimalHeaders(channel), pathParams, saveFlightInterestRequestBody));
        saveFlightInterestService.invoke();
        saveFlightInterestService.wasSuccessful();
    }

    public void addSingleFlightInterestToProfile(String customerId, String flightKey, String fare, String channel) {
        SaveFlightInterestPathParams pathParams = SaveFlightInterestPathParams.builder().customerId(customerId).build();
        Map<String, List<String>> interest = creatMapOfFlighInterestsToAddFromSimpleList(Collections.singletonList(flightKey), Collections.singletonList(fare));
        FlightInterestRequestBody saveFlightInterestRequestBody = SaveFlightInterestRequestBodyFactory.getSaveFlightInterestRequestBody(interest);
        saveFlightInterestService = serviceFactory.saveFlightInterest(new ManageFlightInterestRequest(getMinimalHeaders(channel), pathParams, saveFlightInterestRequestBody));
        saveFlightInterestService.invoke();
    }

    private FlightInterestRequestBody getSingleRequestBody(String flightKey, String fare) {
        Map<String, List<String>> interest = new HashMap<String, List<String>>();
        interest.put(flightKey, Collections.singletonList(fare));
        return SaveFlightInterestRequestBodyFactory.getSaveFlightInterestRequestBody(interest);
    }

    private HybrisHeaders getMinimalHeaders(String channel) {
        HybrisHeaders headers = HybrisHeaders.getValid(channel).build();
        headers.setPrefer(null);
        headers.setConnection(null);
        headers.setAcceptEncoding(null);
        headers.setDate(null);
        return headers;
    }

    public void addFlightInterestToProfile(String customerId, String flightKey, String fare, String channel) {
        SaveFlightInterestPathParams pathParams = SaveFlightInterestPathParams.builder().customerId(customerId).build();
        FlightInterestRequestBody saveFlightInterestRequestBody = getSingleRequestBody(flightKey, fare);
        saveFlightInterestService = serviceFactory.saveFlightInterest(new ManageFlightInterestRequest(HybrisHeaders.getValid(channel).build(), pathParams, saveFlightInterestRequestBody));
        saveFlightInterestService.invoke();
    }

    public String getValidFlightKey() throws Throwable {
        FlightsService flightsService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
        return flightsService.getOutboundFlight().getFlightKey();
    }

    public String getValidFlightKeyForAirport(String aAirport) {
        return flightInterestDao.getAFlightKeyForAnAirport(aAirport);
    }

    public void setupDatabaseData(String aAirport, String checkinTime, String delayInHours, String flightKey) {
        flightInterestDao.updateOnlineClosureCheckinTime(aAirport, checkinTime);
        flightInterestDao.updateFlightDepartureTime(getHoursAddedToNow(Integer.parseInt(delayInHours)), flightKey);
    }

    private Date getHoursAddedToNow(int numHours) {
        Calendar curDate = Calendar.getInstance();
        curDate.setTime(new Date());
        curDate.add(Calendar.HOUR_OF_DAY, numHours);
        return curDate.getTime();
    }

    public List<HybrisFlightDbModel> getFlightListToAddBefore(List<HybrisFlightDbModel> flightsToAdd) {
        if (CollectionUtils.isNotEmpty(flightsToAdd)) {
            flightsToAdd.remove(0);
        }
        return flightsToAdd;
    }

    public String getLastFlightToAddFromFlightsList(List<HybrisFlightDbModel> flightsToAdd) {
        if (CollectionUtils.isNotEmpty(flightsToAdd)) {
            return flightsToAdd.get(0).getFlightKey();
        } else {
            return null;
        }
    }

    public List<HybrisFlightDbModel> setAnInvalidFlightKey(List<HybrisFlightDbModel> flightsToAdd) {
        if (CollectionUtils.isNotEmpty(flightsToAdd)) {
            flightsToAdd.get(0).setFlightKey(FlightInterestConstants.INVALID_FLIGHTKEY);
        }
        return flightsToAdd;
    }

    private List<FindFlightsResponse.Flight> setAnInvalidFlightKeyFromFindFlights(List<FindFlightsResponse.Flight> flightsToAdd) {
        if (CollectionUtils.isNotEmpty(flightsToAdd)) {
            flightsToAdd.get(0).setFlightKey(FlightInterestConstants.INVALID_FLIGHTKEY);
        }
        return flightsToAdd;
    }

    public void removeFlightInterest(String customerID, String flightKey, String bundle, String login, int numFlights) throws Throwable {

        customerHelper.createNewCustomerProfileWithEmail(getRandomEmail(10));
        String customerId = customerHelper.getRegisterCustomerService().getResponse().getRegistrationConfirmation().getCustomerId();
        testData.setData(CUSTOMER_ID, customerId);
        staffMembertoCustomerProfileAssociationHelper.associateCustomerProfileWithStaffMemberFromId(customerId, false);
        if ("valid".equals(login)) {
            customerHelper.loginWithValidCredentials();
            CustomerPathParams params = CustomerPathParams.builder().customerId(customerId).path(GET_FLIGHT_INTEREST).build();
            getFlightInterestService = serviceFactory.getFlightInterest(new ManageFlightInterestRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), params));
            testData.setData(SERVICE, getFlightInterestService);
            getFlightInterestService.invoke();
        }

        FlightsService flightsService = flightHelper.getFlights(testData.getChannel(), "1 adult", testData.getOrigin(), testData.getDestination(),
                null, new DateFormat().today().addDay(10), new DateFormat().today().addDay(20));

        List<FindFlightsResponse.Flight> findFlights = flightsService.getOutboundFlights();
        findFlights.addAll(flightsService.getInboundFlights());

        Map<String, List<String>> flightInterestsToAdd = creatMapOfFlighInterestsToAddAndRemove(findFlights.subList(0, numFlights), Collections.singletonList(FlightInterestConstants.STAFF_FARE));
        addFlightWithMultipleInterests(flightInterestsToAdd, customerId, FlightInterestConstants.DIGITAL_CHANNEL);

        getFlightInterestService().wasSuccessful();

        if (INVALID.equalsIgnoreCase(customerID)) {
            customerId = "INVALID_CUSTOMER";
        }

        if (INVALID.equalsIgnoreCase(flightKey)) {
            findFlights = setAnInvalidFlightKeyFromFindFlights(findFlights);
            flightInterestsToAdd = creatMapOfFlighInterestsToAddAndRemove(findFlights, Collections.singletonList(FlightInterestConstants.STAFF_FARE));
        }

        if (INVALID.equalsIgnoreCase(bundle)) {
            flightInterestsToAdd = creatMapOfFlighInterestsToAddAndRemove(findFlights, Collections.singletonList(FlightInterestConstants.INVALID_FARE));
        }

        CustomerPathParams removeParams = CustomerPathParams.builder().customerId(customerId).path(GET_FLIGHT_INTEREST).build();
        RemoveFlightInterestRequestBody removeFlightInterestRequestBody = RemoveFlightInterestRequestBodyFactory.getRemoveFlightInterestRequestBody(flightInterestsToAdd);
        removeFlightInterestService = serviceFactory.removeFlightInterest(new RemoveFlightInterestRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), removeParams, removeFlightInterestRequestBody));
        removeFlightInterestService.invoke();
        testData.setData(SERVICE, removeFlightInterestService);
    }

    public RemoveFlightInterestService removeFlightInterestService() {
        return removeFlightInterestService;
    }

    public GetFlightInterestService getFlightInterestServiceAfterRemoval() {
        CustomerPathParams params = CustomerPathParams.builder().customerId(testData.getData(CUSTOMER_ID)).path(GET_FLIGHT_INTEREST).build();
        getFlightInterestService = serviceFactory.getFlightInterest(new ManageFlightInterestRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), params));
        testData.setData(SERVICE, getFlightInterestService);
        getFlightInterestService.invoke();
        return getFlightInterestService;
    }

    public CustomerProfileService customerProfileService() {
        CustomerPathParams profilePathParams = CustomerPathParams.builder()
                .customerId(testData.getData(CUSTOMER_ID))
                .path(PROFILE)
                .build();

        CustomerProfileService customerProfileService = serviceFactory.getCustomerProfile(
            new ProfileRequest(HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), profilePathParams)
        );

        customerProfileService.invoke();
        return customerProfileService;
    }


}
