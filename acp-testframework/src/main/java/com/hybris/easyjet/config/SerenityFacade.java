package com.hybris.easyjet.config;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.PurchasedSeatHelper;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.AddPurchasedSeatsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BasketContent;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetBookingResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetSeatMapService;
import net.serenitybdd.core.Serenity;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SESSION;
import static com.hybris.easyjet.config.SerenityFacade.testData.*;
import static com.hybris.easyjet.config.constants.CommonConstants.DIGITAL_CHANNEL;
import static com.hybris.easyjet.config.constants.CommonConstants.GBP;

/**
 * This class is intended to manage the access to the serenity session.
 * It provides methods with generic return type to get and set data, and methods to check parameters existence
 */
@Component
public final class SerenityFacade {

    private static final String PAYMENT_CODE = "";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy");
    private static final Map<String, Object> sessionData = new HashMap<>();

    public DataFactory dataFactory = new DataFactory();

    public static SerenityFacade getTestDataFromSpring() {
        return (SerenityFacade) TenantBeanFactoryPostProcessor.getFactory().getBean("serenityFacade");
    }

    @SuppressWarnings("unchecked")
    public void storeTestData() {
        EnumMap<DataKeys, SessionList> oldSession = new EnumMap<>(DataKeys.class);
        for (Map.Entry<Object, Object> entry : Serenity.getCurrentSession().entrySet()) {
            SessionList<Object> value = new SessionList((SessionList) entry.getValue());
            oldSession.put((DataKeys) entry.getKey(), value);
        }
        Serenity.setSessionVariable(SESSION).to(oldSession);
    }

    @SuppressWarnings("unchecked")
    public void restoreTestData() {
        if (!Serenity.hasASessionVariableCalled(SESSION)) {
            throw new IllegalArgumentException("No session stored");
        }
        EnumMap<DataKeys, SessionList> oldSession = Serenity.sessionVariableCalled(SESSION);
        Serenity.initializeTestSession();
        for (Map.Entry<DataKeys, SessionList> entry : oldSession.entrySet()) {
            SessionList<Object> value = entry.getValue();
            Serenity.setSessionVariable(entry.getKey()).to(value);
        }
    }

    public <T> void setData(DataKeys key, T value) {
        SessionList<T> list;
        if (Serenity.hasASessionVariableCalled(key)) {
            list = Serenity.sessionVariableCalled(key);
        } else {
            list = new SessionList<>();
        }
        Serenity.setSessionVariable(key).to(list.push(value));
    }

    public <T> void setData(DataKeys key, T value, String position) {
        SessionList<T> list;
        if (Serenity.hasASessionVariableCalled(key)) {
            list = Serenity.sessionVariableCalled(key);
        } else {
            list = new SessionList<>();
        }
        Serenity.setSessionVariable(key).to(list.push(value, position));
    }

    public <T> T getData(DataKeys key) {
        if (Serenity.hasASessionVariableCalled(key)) {
            SessionList<T> list = Serenity.sessionVariableCalled(key);
            return list.get(0);
        } else {
            return null;
        }
    }

    public <T> T getData(DataKeys key, int index) {
        if (Serenity.hasASessionVariableCalled(key)) {
            SessionList<T> list = Serenity.sessionVariableCalled(key);
            return list.get(index);
        } else {
            return null;
        }
    }

    public <T> T getData(DataKeys key, String position) {
        if (Serenity.hasASessionVariableCalled(key)) {
            SessionList<T> list = Serenity.sessionVariableCalled(key);
            return list.get(position);
        } else {
            return null;
        }
    }

    public SessionList getDataList(DataKeys key) {
        return Serenity.sessionVariableCalled(key);
    }

    public boolean keyExist(DataKeys key) {
        return Serenity.hasASessionVariableCalled(key);
    }

    public boolean keyNotExist(DataKeys key) {
        return !Serenity.hasASessionVariableCalled(key);
    }

    public boolean dataExist(DataKeys key) {
        return !keyNotExist(key) && !Objects.isNull(getData(key));
    }

    public boolean dataNotExist(DataKeys key) {
        return keyNotExist(key) || Objects.isNull(getData(key));
    }

    public String getOutboundDate() {
        if (!Serenity.hasASessionVariableCalled(OUTBOUND_DATE)) {
            Serenity.setSessionVariable(OUTBOUND_DATE).to(LocalDate.now().plusDays(1).format(dateTimeFormatter));
        }
        return Serenity.sessionVariableCalled(OUTBOUND_DATE);
    }

    public void setOutboundDate(String outboundDate) {
        Serenity.setSessionVariable(OUTBOUND_DATE).to(outboundDate);
    }

    public String getInboundDate() {
        if (!Serenity.hasASessionVariableCalled(INBOUND_DATE)) {
            Serenity.setSessionVariable(INBOUND_DATE).to(LocalDate.now().plusDays(5).format(dateTimeFormatter));
        }
        return Serenity.sessionVariableCalled(INBOUND_DATE);
    }

    public void setInboundDate(String inboundDate) {
        Serenity.setSessionVariable(INBOUND_DATE).to(inboundDate);
    }

    public String getChannel() {
        if (!Serenity.hasASessionVariableCalled(CHANNEL)) {
            Serenity.setSessionVariable(CHANNEL).to(DIGITAL_CHANNEL);
        }
        return Serenity.sessionVariableCalled(CHANNEL);
    }

    public void setChannel(String channel) {
        Serenity.setSessionVariable(CHANNEL).to(channel);
    }

    public String getCurrency() {
        if (!Serenity.hasASessionVariableCalled(CURRENCY)) {
            Serenity.setSessionVariable(CURRENCY).to(GBP);
        }
        return Serenity.sessionVariableCalled(CURRENCY);
    }

    public void setCurrency(String currency) {
        Serenity.setSessionVariable(CURRENCY).to(currency);
    }

    public String getPassengerMix() {
        if (!Serenity.hasASessionVariableCalled(PASSENGER_MIX)) {
            Serenity.setSessionVariable(PASSENGER_MIX).to("1 Adult");
        }
        return Serenity.sessionVariableCalled(PASSENGER_MIX);
    }

    public void setPassengerMix(String passengerMix) {
        Serenity.setSessionVariable(PASSENGER_MIX).to(passengerMix);
    }

    public String getOrigin() {
        if (!Serenity.hasASessionVariableCalled(ORIGIN)) {
            Serenity.setSessionVariable(ORIGIN).to("LTN");
        }
        return Serenity.sessionVariableCalled(ORIGIN);
    }

    public void setOrigin(String origin) {
        Serenity.setSessionVariable(ORIGIN).to(origin);
    }

    public String getDestination() {
        if (!Serenity.hasASessionVariableCalled(DESTINATION)) {
            Serenity.setSessionVariable(DESTINATION).to("ALC");
        }
        return Serenity.sessionVariableCalled(DESTINATION);
    }

    public void setDestination(String destination) {
        Serenity.setSessionVariable(DESTINATION).to(destination);
    }

    public String getPeriod() {
        if (!Serenity.hasASessionVariableCalled(PERIOD)) {
            Serenity.setSessionVariable(PERIOD).to("future");
        }
        return Serenity.sessionVariableCalled(PERIOD);
    }

    public void setPeriod(String period) {
        Serenity.setSessionVariable(PERIOD).to(period);
    }

    public boolean isVerifySeatAllocation() {
        if (!Serenity.hasASessionVariableCalled(VERIFY_SEAT_ALLOCATION)) {
            Serenity.setSessionVariable(VERIFY_SEAT_ALLOCATION).to(false);
        }
        return Serenity.sessionVariableCalled(VERIFY_SEAT_ALLOCATION);
    }

    public void setVerifySeatAllocation(boolean verifySeatAllocation) {
        Serenity.setSessionVariable(VERIFY_SEAT_ALLOCATION).to(verifySeatAllocation);
    }

    public boolean isDefaultPaymentMethod() {
        if (!Serenity.hasASessionVariableCalled(IS_DEFAULT_PAYMENT_METHOD)) {
            Serenity.setSessionVariable(IS_DEFAULT_PAYMENT_METHOD).to(true);
        }
        return Serenity.sessionVariableCalled(IS_DEFAULT_PAYMENT_METHOD);
    }

    public void setAsDefaultPaymentMethod(boolean isDefault) {
        Serenity.setSessionVariable(IS_DEFAULT_PAYMENT_METHOD).to(isDefault);
    }

    public String getBookingType() {
        if (!Serenity.hasASessionVariableCalled(BOOKING_TYPE)) {
            Serenity.setSessionVariable(BOOKING_TYPE).to(CommonConstants.BookingType.STANDARD_CUSTOMER);
        }
        return Serenity.sessionVariableCalled(BOOKING_TYPE);
    }

    public void setBookingType(String bookingType) {
        Serenity.setSessionVariable(BOOKING_TYPE).to(bookingType);
    }

    public String getAccessType() {
        if (!Serenity.hasASessionVariableCalled(ACCESS_TYPE)) {
            Serenity.setSessionVariable(ACCESS_TYPE).to(null);
        }
        return Serenity.sessionVariableCalled(ACCESS_TYPE);
    }

    public void setAccessType(String accessType) {
        Serenity.setSessionVariable(ACCESS_TYPE).to(accessType);
    }

    public boolean getUpdatePassengerWithApis() {
        if (!Serenity.hasASessionVariableCalled(UPDATE_PASSENGER_APIS)) {
            Serenity.setSessionVariable(UPDATE_PASSENGER_APIS).to(false);
        }
        return Serenity.sessionVariableCalled(UPDATE_PASSENGER_APIS);
    }

    public void setUpdatePassengerWithApis(boolean withApis) {
        Serenity.setSessionVariable(UPDATE_PASSENGER_APIS).to(withApis);
    }

    public Double getSeatDiscountForFare() {
        if (!Serenity.hasASessionVariableCalled(SEAT_DISCOUNT_FARE)) {
            Serenity.setSessionVariable(SEAT_DISCOUNT_FARE).to(0.0);
        }
        return Serenity.sessionVariableCalled(SEAT_DISCOUNT_FARE);
    }

    public void setSeatDiscountForFare(Double discount) {
        Serenity.setSessionVariable(SEAT_DISCOUNT_FARE).to(discount);
    }

    public boolean getValidationScenarios() {
        if (!Serenity.hasASessionVariableCalled(VALIDATION)) {
            Serenity.setSessionVariable(VALIDATION).to(false);
        }
        return Serenity.sessionVariableCalled(VALIDATION);
    }

    public void setValidationScenarios(boolean validationScenarios) {
        Serenity.setSessionVariable(VALIDATION).to(validationScenarios);
    }

    public List<AddPurchasedSeatsRequestBody> getPurchsedSeatRequestBody() {
        if (!Serenity.hasASessionVariableCalled(PURCHASED_SEAT_REQUEST_BODY)) {
            Serenity.setSessionVariable(PURCHASED_SEAT_REQUEST_BODY).to(new ArrayList<AddPurchasedSeatsRequestBody>());
        }
        return Serenity.sessionVariableCalled(PURCHASED_SEAT_REQUEST_BODY);
    }

    public void setPurchsedSeatRequestBody(List<AddPurchasedSeatsRequestBody> purchasedSeatRequestBody) {
        Serenity.setSessionVariable(PURCHASED_SEAT_REQUEST_BODY).to(purchasedSeatRequestBody);
    }


    public void setData(String key, Object value) {
        sessionData.put(key, value);
    }

    public Object getData(String key) {
        return sessionData.get(key);
    }

    public String getPassengerId() {
        return Serenity.sessionVariableCalled(PASSENGER_ID);
    }

    public void setPassengerId(String passengerId) {
        Serenity.setSessionVariable(PASSENGER_ID).to(passengerId);
    }

    public String getDocumentId() {
        return Serenity.sessionVariableCalled(DOCUMENT_ID);
    }

    public void setDocumentId(String documentId) {
        Serenity.setSessionVariable(DOCUMENT_ID).to(documentId);
    }

    public PurchasedSeatHelper.SEATPRODUCTS getSeatProductInBasket() {
        return Serenity.sessionVariableCalled(SEAT_PRODUCT_IN_BASKET);
    }

    public void setSeatProductInBasket(PurchasedSeatHelper.SEATPRODUCTS seatProductInBasket) {
        Serenity.setSessionVariable(SEAT_PRODUCT_IN_BASKET).to(seatProductInBasket);
    }

    public String getFlightKey() {
        return Serenity.sessionVariableCalled(FLIGHT_KEY);
    }

    public void setFlightKey(String flightKey) {
        Serenity.setSessionVariable(FLIGHT_KEY).to(flightKey);
    }

    public BasketContent getBasketContent() {
        return Serenity.sessionVariableCalled(BASKET_CONTENT);
    }

    public void setBasketContent(BasketContent basketContent) {
        Serenity.setSessionVariable(BASKET_CONTENT).to(basketContent);
    }

    public String getActualFlightKey() {
        return Serenity.sessionVariableCalled(ACTUAL_FLIGHT_KEY);
    }

    public void setActualFlightKey(String actualFlightKey) {
        Serenity.setSessionVariable(ACTUAL_FLIGHT_KEY).to(actualFlightKey);
    }

    public String getActualFareType() {
        return Serenity.sessionVariableCalled(ACTUAL_FARE_TYPE);
    }

    public void setActualFareType(String actualFareType) {
        Serenity.setSessionVariable(ACTUAL_FARE_TYPE).to(actualFareType);
    }

    public String getActualCurrency() {
        return Serenity.sessionVariableCalled(ACTUAL_CURRENCY);
    }

    public void setActualCurrency(String actualCurrency) {
        Serenity.setSessionVariable(ACTUAL_CURRENCY).to(actualCurrency);
    }

    public String getJourneyType() {
        return Serenity.sessionVariableCalled(JOURNEY_TYPE);
    }

    public void setJourneyType(String journeyType) {
        Serenity.setSessionVariable(JOURNEY_TYPE).to(journeyType);
    }

    public String getAccessToken() {
        return Serenity.sessionVariableCalled(ACCESS_TOKEN);
    }

    public void setAccessToken(String accessToken) {
        Serenity.setSessionVariable(ACCESS_TOKEN).to(accessToken);
    }

    public String getBasketId() {
        return Serenity.sessionVariableCalled(BASKET_ID);
    }

    public void setBasketId(String basketId) {
        Serenity.setSessionVariable(BASKET_ID).to(basketId);
    }

    public PurchasedSeatHelper.SEATPRODUCTS getTypeOfSeat() {
        if (!Serenity.hasASessionVariableCalled(TYPE_OF_SEAT)) {
            Serenity.setSessionVariable(TYPE_OF_SEAT).to(PurchasedSeatHelper.SEATPRODUCTS.GENERIC);
        }
        return Serenity.sessionVariableCalled(TYPE_OF_SEAT);
    }

    public void setTypeOfSeat(PurchasedSeatHelper.SEATPRODUCTS typeOfSeat) {
        Serenity.setSessionVariable(TYPE_OF_SEAT).to(typeOfSeat);
    }

    public String getEmail() {
        return Serenity.sessionVariableCalled(EMAIL);
    }

    public void setEmail(String email) {
        Serenity.setSessionVariable(EMAIL).to(email);
    }

    public void setFirstCustomerEmail(String email) {
        Serenity.setSessionVariable(FIRSTCUSTOMEREMAIL).to(email);
    }

    public void setFirstCustomerPassword(String pwd) {
        Serenity.setSessionVariable(FIRSTCUSTOMERPASSWORD).to(pwd);
    }

    public String getFirstCustomerEmail() {
        return Serenity.sessionVariableCalled(FIRSTCUSTOMEREMAIL);
    }

    public String getFirstCustomerPassword() {
        return Serenity.sessionVariableCalled(FIRSTCUSTOMERPASSWORD);
    }

    public String getPassword() {
        return Serenity.sessionVariableCalled(PASSWORD);
    }

    public void setPassword(String password) {
        Serenity.setSessionVariable(PASSWORD).to(password);
    }

    public String getPassengerIdFromChange() {
        return Serenity.sessionVariableCalled(PASSENGER_ID_FROM_CHANGE);
    }

    public void setPassengerIdFromChange(String passengerIdFromChangechange) {
        Serenity.setSessionVariable(PASSENGER_ID_FROM_CHANGE).to(passengerIdFromChangechange);
    }

    public GetSeatMapService getSeatingServiceHelper() {
        return Serenity.sessionVariableCalled(SEATING_SERVICE_HELPER);
    }

    public void setSeatingServiceHelper(GetSeatMapService seatingServiceHelper) {
        Serenity.setSessionVariable(SEATING_SERVICE_HELPER).to(seatingServiceHelper);
    }

    public String getFareType() {
        return Serenity.sessionVariableCalled(getFlights.FARE_TYPE);
    }

    public void setFareType(String fareType) {
        Serenity.setSessionVariable(getFlights.FARE_TYPE).to(fareType);
    }

    public String getPaymentCode() {
        return Serenity.sessionVariableCalled(PAYMENT_CODE);
    }

    public void setPaymentCode(String paymentCode) {
        Serenity.setSessionVariable(PAYMENT_CODE).to(paymentCode);
    }

    public GetBookingResponse getBookingResponse() {
        return Serenity.sessionVariableCalled(GET_BOOKING_RESPONSE);
    }

    public void setBookingResponse(GetBookingResponse bookingResponse) {
        Serenity.setSessionVariable(GET_BOOKING_RESPONSE).to(bookingResponse);
    }

    public int getSportEquipCount() {
        return Serenity.sessionVariableCalled(SPORT_EQUIP_COUNT);
    }

    public void setSportEquipCount(int count) {
        Serenity.setSessionVariable(SPORT_EQUIP_COUNT).to(count);
    }

    public int getHoldBagCount() {
        return Serenity.sessionVariableCalled(HOLD_BAG_COUNT);
    }

    public void setHoldBagCount(int count) {
        Serenity.setSessionVariable(HOLD_BAG_COUNT).to(count);
    }

    public FindFlightsResponse.Flight getOutboundFlight() {
        return Serenity.sessionVariableCalled(getFlights.OUTBOUND_FLIGHT);
    }

    public void setOutboundFlight(FindFlightsResponse.Flight flight) {
        Serenity.setSessionVariable(getFlights.OUTBOUND_FLIGHT).to(flight);
    }

    public List<FindFlightsResponse.Flight> getOutboundFlights() {
        return Serenity.sessionVariableCalled(getFlights.OUTBOUND_FLIGHTS);
    }

    public void setOutboundFlights(List<FindFlightsResponse.Flight> flight) {
        Serenity.setSessionVariable(getFlights.OUTBOUND_FLIGHTS).to(flight);
    }

    public void cleanStoredData() {
        sessionData.clear();
    }

    public String getAmendableBasket() {
        return Serenity.sessionVariableCalled(AMENDABLE_BASKET);
    }

    public void setAmendableBasket(String basketCode) {
        Serenity.setSessionVariable(AMENDABLE_BASKET).to(basketCode);
    }

    public enum DataKeys {
        // common data
        CHANNEL,
        HEADERS,
        SERVICE,
        XTEST,

        // deal data
        DEALS,
        APPLICATION_ID,
        OFFICE_ID,
        CORPORATE_ID,
        //TODO quantity of what??
        QUANTITY,
        FLIGHT_QUERY_PARAMS,

        // getFlights data
        GET_FLIGHT_SERVICE,
        PASSENGER_MIX,
        PASSENGERS,
        //TODO this is useless; passenger total is stored in passengerMix object
        PASSENGERS_TOTAL,
        FARE_TYPE,
        IS_STAFF,
        ORIGIN,
        DESTINATION,
        OUTBOUND_DATE,
        INBOUND_DATE,
        FLEXIBLE_DAYS,
        CURRENCY,
        USABLE_SECTORS,
        OUTBOUND_JOURNEY,
        OUTBOUND_FLIGHT,
        INBOUND_JOURNEY,
        INBOUND_FLIGHT,

        // addFlight data
        BASKET_SERVICE,
        BASKET_ID,
        PASSENGER_ID,
        FLIGHT_KEY,
        NEW_FLIGHT_KEY,
        BUNDLE,
        PASSENGER_CODES,
        BASKET_RESPONSE,
        IS_ALREADY_ALLOCATED_SEAT,
        SAVED_PASSENGER_CODE,
        SAVED_PASSENGER_SIZE,
        STORE_SAVED_PASSENGER_CODE,
        INFANT_ON_LAP_ID,

        // Seat map data
        EXTRA_LEGROOM_BASEPRICE,
        UPFRONT_BASEPRICE,
        STANDARD_BASEPRICE,

        // hold items service
        ADD_HOLD_BAG_SERVICE,
        ADD_SPORTS_ITEM_SERVICE,
        SEAT_PRICE,

        // paymentMethods data
        PAYMENT_METHODS_SERVICE,
        PAYMENT_METHODS,

        // commitBooking data
        COMMIT_BOOKING_SERVICE,
        BASKET_CONTENT,
        BOOKING_ID,
        BOOKING_STATUS,
        BOOKING_TYPE,
        PAYMENT_METHOD,
        SAVED_TRAVELLER,

        // getBooking data
        GET_BOOKING_SERVICE,
        GET_BOOKING_RESPONSE,
        CREDIT_CARD_FEE,

        // agent data
        AGENT_ID,
        EMPLOYEE_ID,
        AGENT_ACCESS_TOKEN,
        EMPLOYEE_NAME,

        // customer data
        CUSTOMER_ID,
        CUSTOMER_ACCESS_TOKEN,
        SIGNIFICANT_OTHER_ID,
        DEPENDENT_ID,
        GET_CUSTOMER_PROFILE_SERVICE,

        // registerCustomer data
        REGISTER_CUSTOMER_SERVICE,
        REGISTER_CUSTOMER_REQUEST,

        // searchFor Customer
        IDENTIFY_CUSTOMER_SERVICE,

        // changeFlight data
        PASSENGER_LIST,

        // cleaning data
        BASKET_FLIGHTS,
        COMMIT_FLIGHTS,

        // store service calls
        SERVICE_CALLS,

        // AL data
        ALLOCATED_FLIGHTS,
        FLIGHT_FULLY_ALLOCATED,

        // other data
        SCENARIO,
        SESSION,
        TRANSACTION_ID,INVALID,
        FLIGHT_INVENTORY,
        FLIGHT_HOLD_ITEMS,
        REQUIRED_EJPLUS,
        FIELD,
        OLD_VALUE_FIELD,
        MEMBERSHIP_MODEL,
        UPDATE_PASSENGER_BASED_SAVED_PASSENGER,
        STOCK_AFTER_CHANGE_FLIGHT,
        STOCK_BEFORE_CHANGE_FLIGHT,
        REFUND,
        WANT_DEAL,
        ORIGINAL_BASKET,
        PRICE_DIFFERENCE,
        AMENDED_BASKET,
        APIS_DETAILS,
        COMMENT_CODE,
        CAR_HIRE_SERVICE
    }

    public enum getFlights {
        FARE_TYPE,
        APPLICATION_ID,
        OFFICE_ID,
        CORPORATE_ID,
        IS_STAFF,
        OUTBOUND_FLIGHT,
        OUTBOUND_FLIGHTS
    }

    public enum testData {
        OUTBOUND_DATE,
        INBOUND_DATE,
        CHANNEL,
        CURRENCY,
        PASSENGER_MIX,
        PASSENGER_ID,
        DOCUMENT_ID,
        SEAT_PRODUCT_IN_BASKET,
        FLIGHT_KEY,
        PURCHASED_SEAT_REQUEST_BODY,
        BASKET_CONTENT,
        ACTUAL_FLIGHT_KEY,
        ACTUAL_FARE_TYPE,
        ACTUAL_CURRENCY,
        JOURNEY_TYPE,
        ACCESS_TOKEN,
        BASKET_ID,
        ORIGIN,
        DESTINATION,
        TYPE_OF_SEAT,
        VERIFY_SEAT_ALLOCATION,
        EMAIL,
        PASSWORD,
        FIRSTCUSTOMEREMAIL,
        FIRSTCUSTOMERPASSWORD,
        PERIOD,
        SEATING_SERVICE_HELPER,
        PASSENGER_ID_FROM_CHANGE,
        PAYMENT_TYPE,
        GET_BOOKING_RESPONSE,
        BOOKING_TYPE,
        SPORT_EQUIP_COUNT,
        HOLD_BAG_COUNT,
        UPDATE_PASSENGER_APIS,
        REGISTERED_CUSTOMER_REQUEST,
        IS_DEFAULT_PAYMENT_METHOD,
        ALLOCATED_SEATS,
        SEAT_DISCOUNT_FARE,
        AMENDABLE_BASKET,
        CUSTOMER_PROFILE_SERVICE,
        EMPLOYEE_ID,
        CUSTOMER_LAST_NAME,
        PASSENGER_LAST_NAME,
        EJPLUS_NUMBER,
        VALIDATION,
        STOCK_AFTER_CHANGE_FLIGHT,
        STOCK_BEFORE_CHANGE_FLIGHT,
        ACCESS_TYPE
    }
}
