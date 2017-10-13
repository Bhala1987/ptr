package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.*;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.*;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Name;
import com.hybris.easyjet.fixture.hybris.invoke.response.HoldItemsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AbstractPassenger;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItem;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AugmentedPriceItems;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by tejaldudhale on 03/01/2017.
 */
public class BasketContentFactory {
    @Autowired
    private SerenityFacade testData;

    public static final String EXCESS_WEIGHT_PRODUCT = "ExcessWeightProduct";

    public static final String HOLD_BAG_PRODUCT = "HoldBagProduct";

    private static BasketContent basketContent;

    private BasketContentFactory() {
    }

    public static BasketContent getBasketContent(Basket basket) throws InvocationTargetException, IllegalAccessException {
        DataFactory df = new DataFactory();
        basketContent = BasketContent.builder()
            .basketLanguage(basket.getBasketLanguage())
            .currency(Currency.builder()
                .code(basket.getCurrency().getCode())
                .name(basket.getCurrency().getName())
                .build())
            .defaultCardType(basket.getDefaultCardType())
            .customerContext(CustomerContext.builder()
                .name(Name.builder()
                    .firstName(df.getFirstName())
                    .lastName(df.getLastName())
                    .fullName(df.getName())
                    .title("MR")
                    .build()
                )
                .address(Address.builder()
                    .addressLine1(df.getAddress())
                    .addressLine2(df.getAddress())
                    .addressLine3(df.getAddressLine2())
                    .postalCode("sl11rg")
                    .city(df.getCity())
                    .country("USA")
                    .countyState("state")

                    .build()
                )
                .internationalDiallingCode("+44")
                .email(df.getFirstName() + "." + df.getLastName() + "@" + df.getRandomText(5) + ".com")
                .phoneNumber(df.getNumberText(12)

                )
                .build())
            .uniquePassengerList(populateUniquePassengerList(getTravellersAddedToBasket(basket)))
            .outbounds(populateOutBound(basket))
            .inbounds(populateInboundBound(basket))
            .fees(populateFee(basket.getFees()))
            .carHires(populateCareHire())
            .hotels(populateHotels())
            .travelInsurances(populateTravelInsurance())
            .comments(populateComments())
            .discounts(populateDiscount(basket.getDiscounts()))
            .taxes(populateTax(basket.getTaxes()))
            .subtotalAmountWithCreditCard(basket.getSubtotalAmountWithCreditCard())
            .subtotalAmountWithDebitCard(basket.getSubtotalAmountWithDebitCard())
            .totalAmountWithCreditCard(basket.getTotalAmountWithCreditCard())
            .totalAmountWithDebitCard(basket.getTotalAmountWithDebitCard())

            .build();

        return basketContent;
    }

    public static BasketContent getBasketContentForExistingCustomer(Basket basket, CustomerProfileResponse.BasicProfile profile) throws InvocationTargetException, IllegalAccessException {
        DataFactory df = new DataFactory();
        basketContent = BasketContent.builder()
            .basketLanguage(basket.getBasketLanguage())
            .currency(Currency.builder()
                .code(basket.getCurrency().getCode())
                .name(basket.getCurrency().getName())
                .build())
            .defaultCardType(basket.getDefaultCardType())
            .customerContext(CustomerContext.builder()
                .address(Address.builder()
                    .addressLine1(df.getAddressLine2())
                    .addressLine2(df.getAddressLine2())
                    .addressLine3(df.getAddressLine2())
                    .postalCode(profile.getContactAddress().get(0).getPostalCode())
                    .city(df.getCity())
                    .country(profile.getContactAddress().get(0).getCountry())
                    .countyState(profile.getContactAddress().get(0).getCounty_state())
                    .build())
                .name(Name.builder()
                    .firstName(profile.getPersonalDetails().getFirstName())
                    .lastName(profile.getPersonalDetails().getLastName())
                    .fullName(profile.getPersonalDetails().getFirstName() + " " + profile.getPersonalDetails().getLastName())
                    .title(profile.getPersonalDetails().getTitle())
                    .build())
                .phoneNumber(df.getNumberText(12))
                .email(profile.getPersonalDetails().getEmail())
                .internationalDiallingCode("+44")
                .build())
            .uniquePassengerList(populateUniquePassengerList(getTravellersAddedToBasket(basket)))
            .outbounds(populateOutBound(basket))
            .inbounds(populateInboundBound(basket))
            .fees(populateFee(basket.getFees()))
            .carHires(populateCareHire())
            .hotels(populateHotels())
            .travelInsurances(populateTravelInsurance())
            .comments(populateComments())
            .discounts(populateDiscount(basket.getDiscounts()))
            .taxes(populateTax(basket.getTaxes()))
            .subtotalAmountWithCreditCard(basket.getSubtotalAmountWithCreditCard())
            .subtotalAmountWithDebitCard(basket.getSubtotalAmountWithDebitCard())
            .totalAmountWithCreditCard(basket.getTotalAmountWithCreditCard())
            .totalAmountWithDebitCard(basket.getTotalAmountWithDebitCard())
            .build();
        return basketContent;
    }

    public static BasketContent getBasketContentForExistingCustomer(Basket basket, com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger profile) throws InvocationTargetException, IllegalAccessException {
        DataFactory df = new DataFactory();
        basketContent = BasketContent.builder()
            .basketLanguage(basket.getBasketLanguage())
            .currency(Currency.builder()
                .code(basket.getCurrency().getCode())
                .name(basket.getCurrency().getName())
                .build())
            .defaultCardType(basket.getDefaultCardType())
            .customerContext(CustomerContext.builder()
                .address(Address.builder()
                    .addressLine1(df.getAddressLine2())
                    .addressLine2(df.getAddressLine2())
                    .addressLine3(df.getAddressLine2())
                    .postalCode(df.getAddress())
                    .city(df.getCity())
                    .country(df.getAddress())
                    .countyState("")
                    .build())
                .name(Name.builder()
                    .firstName(profile.getPassengerDetails().getName().getFirstName())
                    .lastName(profile.getPassengerDetails().getName().getLastName())
                    .fullName(profile.getPassengerDetails().getName().getFirstName() + " " + profile.getPassengerDetails().getName().getLastName())
                    .title(profile.getPassengerDetails().getName().getTitle())
                    .build())
                .phoneNumber(df.getNumberText(12))
                .email(profile.getPassengerDetails().getEmail())
                .internationalDiallingCode("+44")
                .build())
            .uniquePassengerList(populateUniquePassengerList(getTravellersAddedToBasket(basket)))
            .outbounds(populateOutBound(basket))
            .inbounds(populateInboundBound(basket))
            .fees(populateFee(basket.getFees()))
            .carHires(populateCareHire())
            .hotels(populateHotels())
            .travelInsurances(populateTravelInsurance())
            .comments(populateComments())
            .discounts(populateDiscount(basket.getDiscounts()))
            .taxes(populateTax(basket.getTaxes()))
            .subtotalAmountWithCreditCard(basket.getSubtotalAmountWithCreditCard())
            .subtotalAmountWithDebitCard(basket.getSubtotalAmountWithDebitCard())
            .totalAmountWithCreditCard(basket.getTotalAmountWithCreditCard())
            .totalAmountWithDebitCard(basket.getTotalAmountWithDebitCard())
            .build();
        return basketContent;
    }

    private static List<TravelInsurance> populateTravelInsurance() {
        return new ArrayList<>();
    }

    private static List<String> populateComments() {
        return new ArrayList<>();
    }

    private static List<Hotel> populateHotels() {
        return new ArrayList<>();
    }

    public static BasketContent getBasketContentWithInvalidOrMissingParameter(Basket basket, String parameter) throws InvocationTargetException, IllegalAccessException {
        BasketContent content = getBasketContent(basket);
        switch (parameter) {
            case "BasketContent_ContentMissingOutboundDetails":
                content.setOutbounds(null);
                break;
            case "BasketContent_MissingCustomerContext":
                content.setCustomerContext(null);
                break;
            case "BasketContent_MissingCustomerAddress":
                content.getCustomerContext().getAddress().setAddressLine1(null);
                break;
            case "BasketContent_MissingCustomerEmail":
                content.getCustomerContext().setEmail(null);
                break;
            case "BasketContent_MissingPassengerList":
                content.setUniquePassengerList(null);
                break;
            case "BasketContent_InvalidFlightKey":
                content.getOutbounds().get(0).getFlights().get(0).setFlightKey("20170614MADIBZ92371111");
                break;
            case "BasketContent_InvalidPassengerType":
                content.getUniquePassengerList().get(0).getPassengerDetails().setPassengerType("invalidPassengerType");
                break;
            case "BasketContent_InvalidFareType":
                content.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).setFareType("fareType");
                break;
            case "BasketContent_InvalidPassengerId":
                content.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).setExternalPassengerId("invalid");
                break;
            case "BasketContent_MissingPassengerId":
                content.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).setExternalPassengerId(null);
                break;
            case "BasketContent_MissingDefaultCardType":
                content.setDefaultCardType("");
                break;
            case "BasketContent_InvalidCurrency":
                content.getCurrency().setCode(null);
                content.getCurrency().setName(null);
                break;
            case "BasketContent_MissingFlightKey":
                content.getOutbounds().get(0).getFlights().get(0).setFlightKey(null);
                break;
            case "BasketContent_MissingFlightDetails":
                content.getOutbounds().get(0).setFlights(null);
                break;
            case "BasketContent_MissingFlightNumber":
                content.getOutbounds().get(0).getFlights().get(0).setFlightNumber(null);
                break;
            case "BasketContent_InvalidEmail_Missing@":
                content.getCustomerContext().setEmail("test");
                break;
            case "BasketContent_InvalidEmail_MissingDomain":
                content.getCustomerContext().setEmail("test@t");
                break;
            default:
                return content;
        }
        return content;
    }

    public static BasketContent getBasketContentWithInvalidOrMissingParameter(BasketContent basketContent, String parameter, String productType) {
        List<HoldItem> holdItems = null;
        switch (parameter) {
            case "Missing_Code":
                holdItems = basketContent.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getHoldItems().stream()
                    .filter(holdItem -> holdItem.getDescription().equalsIgnoreCase(productType))
                    .collect(Collectors.toList());
                holdItems.get(0).setCode("");
                return basketContent;
            case "Missing_Quantity":
                holdItems = basketContent.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getHoldItems().stream()
                    .filter(holdItem -> holdItem.getDescription().equalsIgnoreCase(productType))
                    .collect(Collectors.toList());
                holdItems.get(0).setQuantity(null);
                return basketContent;
            case "Missing_BasePrice":
                holdItems = basketContent.getOutbounds().get(0).getFlights().get(0).getPassengers().get(0).getHoldItems().stream()
                    .filter(holdItem -> holdItem.getDescription().equalsIgnoreCase(productType))
                    .collect(Collectors.toList());
                holdItems.get(0).setPricing(null);
                return basketContent;
            default:
                return basketContent;
        }
    }

    public static BasketContent getBasketConetentWithUpdatedPrice(Basket basket, String criteria) throws InvocationTargetException, IllegalAccessException {
        BasketContent content = getBasketContent(basket);
        List<Flight> flights;
        switch (criteria) {
            case "flight price change":
                flights = content.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream()).collect(Collectors.toList());
                flights.forEach(flight -> flight.getPassengers().forEach(
                    passenger -> passenger.getFareProduct().getPricing().setBasePrice(passenger.getFareProduct().getPricing().getBasePrice() + 10)));
                break;
            case "basket debit total change":
                content.setTotalAmountWithDebitCard(content.getTotalAmountWithDebitCard() + 10.0);
                break;
            case "passenger fees change":
                flights = content.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream()).collect(Collectors.toList());
                flights.forEach(flight -> flight.getPassengers().forEach(
                    passenger -> passenger.getFareProduct().getPricing().getFees().forEach(fees -> fees.setAmount(10.0))));
                break;
            case "passenger tax change":
                flights = content.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream()).collect(Collectors.toList());
                flights.forEach(flight -> flight.getPassengers().forEach(
                    passenger -> passenger.getFareProduct().getPricing().getTaxes().forEach(tax -> tax.setAmount(10.0))));
                break;
            case "seat price change":
                flights = content.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream()).collect(Collectors.toList());
                flights.forEach(flight -> flight.getPassengers().forEach(
                    passenger -> passenger.getFareProduct().getPricing().getTaxes().forEach(tax -> tax.setAmount(10.0))));
                break;
            default:
                return content;
        }
        return content;
    }

    public static BasketContent getBasketContentWithUpdatedPrice(BasketContent basketContent, List<String> criteria) {

        List<Passenger> passenger = basketContent
            .getOutbounds()
            .stream()
            .filter(Objects::nonNull)
            .flatMap(f -> f.getFlights().stream())
            .flatMap(g -> g.getPassengers().stream())
            .collect(Collectors.toList());

        List<HoldItem> holdItems = passenger.stream()
            .filter(basketPassenger -> basketPassenger.getHoldItems() != null)
            .flatMap(basketPassenger -> basketPassenger.getHoldItems().stream())
            .collect(Collectors.toList());

        holdItems.forEach(
            holdItem -> {
                if (("SmallSportsProduct".equalsIgnoreCase(holdItem.getDescription()) || "LargeSportsProduct".equalsIgnoreCase(holdItem.getDescription()))
                    && (criteria.contains("SmallSportsProduct") || criteria.contains("LargeSportsProduct"))) {
                    holdItem.getPricing().setBasePrice(holdItem.getPricing().getBasePrice() + 10);
                    //update base price
                }
                if (HOLD_BAG_PRODUCT.equalsIgnoreCase(holdItem.getDescription())) {
                    if (criteria.contains(HOLD_BAG_PRODUCT)) {
                        //update base price
                        holdItem.getPricing().setBasePrice(holdItem.getPricing().getBasePrice() + 10);
                    }
                    if (holdItem.getExtraWeight() != null && criteria.contains("ExcessWeightProduct")) {
                        //update excess weight price
                        holdItem.getExtraWeight().get(0).getPricing().setBasePrice(holdItem.getExtraWeight().get(0).getPricing().getBasePrice() + 10);
                    }
                }
            }
        );
        return basketContent;
    }

    public static BasketContent getBasketContentWithUpdatedSeatPrice(BasketContent basketContent, int passengerIndex) {

        List<Passenger> passenger = basketContent
            .getOutbounds()
            .stream()
            .filter(Objects::nonNull)
            .flatMap(f -> f.getFlights().stream())
            .flatMap(g -> g.getPassengers().stream())
            .collect(Collectors.toList());
        Passenger passenger1 = passenger.get(passengerIndex);
        passenger1.getSeat().getPricing().setBasePrice(passenger1.getSeat().getPricing().getBasePrice() + 10);
        passenger1.getSeat().getPricing().setTotalAmountWithDebitCard(passenger1.getSeat().getPricing().getTotalAmountWithDebitCard() + 10);
        passenger1.getSeat().getPricing().setTotalAmountWithCreditCard(passenger1.getSeat().getPricing().getTotalAmountWithCreditCard() + 10);
        return basketContent;
    }

    private static Fees populateFee(AugmentedPriceItems basketFees) throws InvocationTargetException, IllegalAccessException {
        Fees feesCommitBooking = Fees.builder()
            .build();
        BeanUtils.copyProperties(feesCommitBooking, basketFees);
        feesCommitBooking.setItems(populateItems(basketFees.getItems()
        ));
        return feesCommitBooking;
    }

    private static Taxes populateTax(AugmentedPriceItems basketTaxes) throws InvocationTargetException, IllegalAccessException {
        Taxes taxesCommitBooking = Taxes.builder()
            .build();
        BeanUtils.copyProperties(taxesCommitBooking, basketTaxes);
        taxesCommitBooking.setItems(populateItems(basketTaxes.getItems()
        ));
        return taxesCommitBooking;
    }

    private static List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> populateItems(List<? extends AugmentedPriceItem> augmentedPriceItems) throws InvocationTargetException, IllegalAccessException {
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> bookingItems = new ArrayList<>();
        for (AugmentedPriceItem augmentedPriceItem : augmentedPriceItems
            ) {
            com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item bookingItem = com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item.builder().build();
            BeanUtils.copyProperties(bookingItem, augmentedPriceItem);
            bookingItems.add(bookingItem);
        }
        return bookingItems;
    }

    private static Discounts populateDiscount(AugmentedPriceItems.Discounts basketDiscounts) throws InvocationTargetException, IllegalAccessException {
        Discounts discountsCommitBooking = Discounts.builder()
            .build();
        BeanUtils.copyProperties(discountsCommitBooking, basketDiscounts);
        discountsCommitBooking.setItems(populateItems(basketDiscounts.getItems()));
        return discountsCommitBooking;
    }

    private static List<UniquePassenger> populateUniquePassengerList(List<Basket.Passenger> travellerList) {
        List<UniquePassenger> uniquePassengerLists = new ArrayList<UniquePassenger>();
        for (Basket.Passenger traveller : travellerList
            ) {
            uniquePassengerLists.add(UniquePassenger.builder()
                .externalPassengerId(traveller.getCode())
                .passengerDetails(PassengerDetails.builder()
                    .name(Name.builder()
                        .firstName(traveller.getPassengerDetails().getName().getFirstName())
                        .lastName(traveller.getPassengerDetails().getName().getLastName())
                        .fullName(traveller.getPassengerDetails().getName().getFullName())
                        .title(traveller.getPassengerDetails().getName().getTitle())
                        .middleName(traveller.getPassengerDetails().getName().getMiddleName())
                        .build()
                    )
                    .email(traveller.getPassengerDetails().getEmail())
                    .phoneNumber(traveller.getPassengerDetails().getPhoneNumber())
                    .passengerType(traveller.getPassengerDetails().getPassengerType())
                    .nifNumber(traveller.getPassengerDetails().getNifNumber())
                    .ejPlusCardNumber(traveller.getPassengerDetails().getEjPlusCardNumber())
                    .build()

                )
                .build());
        }
        return uniquePassengerLists;
    }

    private static List<Journey> populateOutBound(Basket basket) {
        List<Journey> outbounds = new ArrayList<>();
        for (Basket.Flights outbound : basket.getOutbounds()
            ) {
            outbounds.add(Journey.builder()
                .isDirect(outbound.getIsDirect())
                .totalDuration(outbound.getTotalDuration())
                .stops(outbound.getStops())
                .journeyTotalWithCreditCard(outbound.getJourneyTotalWithCreditCard())
                .journeyTotalWithDebitCard(outbound.getJourneyTotalWithDebitCard())
                .flights(populateOutBoundFlights(outbound))
                .build()
            );

        }
        return outbounds;
    }

    private static List<Flight> populateOutBoundFlights(Basket.Flights outbound) {
        List<Flight> flights = new ArrayList<>();
        for (Basket.Flight flight : outbound.getFlights()
            ) {
            flights.add(Flight.builder()
                .flightKey(flight.getFlightKey())
                .flightNumber(flight.getFlightNumber())
                .carrier(flight.getCarrier())
                .departureDateTime(flight.getDepartureDateTime())
                .arrivalDateTime(flight.getArrivalDateTime())
                .sector(Sector.builder().code(flight.getSector().getCode())
                        .departure(Airport.builder()
                        .code(flight.getSector().getDeparture().getCode())
                        .name(flight.getSector().getDeparture().getName())
                        .terminal(flight.getSector().getDeparture().getTerminal())
                        .terminal("1")
                        .build())
                    .apisRequired(true)
                    .nifNumberRequired(false)
                    .build())
                .passengers(getPassengerList(getOutBoundTravellersToBasket(outbound)))
                .build()

            );
        }
        return flights;
    }

    private static List<Journey> populateInboundBound(Basket basket) {
        List<Journey> inbounds = new ArrayList<>();
        for (Basket.Flights inbound : basket.getInbounds()
            ) {
            inbounds.add(Journey.builder()
                .isDirect(inbound.getIsDirect())
                .totalDuration(inbound.getTotalDuration())
                .stops(inbound.getStops())
                .journeyTotalWithCreditCard(inbound.getJourneyTotalWithCreditCard())
                .journeyTotalWithDebitCard(inbound.getJourneyTotalWithDebitCard())
                .flights(populateInBoundFlights(inbound))
                .build()
            );

        }
        return inbounds;
    }

    private static List<CarHire> populateCareHire() {
        return new ArrayList<>();
    }

    private static List<Flight> populateInBoundFlights(Basket.Flights inbound) {
        List<Flight> flights = new ArrayList<>();
        for (Basket.Flight flight : inbound.getFlights()
            ) {
            flights.add(Flight.builder()
                    .flightKey(flight.getFlightKey())
                    .flightNumber(flight.getFlightNumber())
                    .carrier(flight.getCarrier())
                    .departureDateTime(flight.getDepartureDateTime())
                    .arrivalDateTime(flight.getArrivalDateTime())
                    .sector(Sector.builder().code(flight.getSector().getCode())
                            .departure(Airport.builder()
                            .code(flight.getSector().getDeparture().getCode())
                            .name(flight.getSector().getDeparture().getName())
//																			.marketGroup(seatmap.getSector().getDeparture().getMarketGroup())
//																								 .marketGroup("ALC")
                            .terminal(flight.getSector().getDeparture().getTerminal())
                            .terminal("1")
                            .build())
                        .build())
                    .passengers(getPassengerList(getInBoundTravellersAddedToBasket(inbound)))
                    .build()

            );
        }
        return flights;
    }

    private static List<Basket.Passenger> getTravellersAddedToBasket(Basket basket) {
        List<Basket.Passenger> travellers = new ArrayList<Basket.Passenger>();
        List<Basket.Flight> outboundFlights = basket.getOutbounds().stream()
            .flatMap(f -> f.getFlights().stream())
            .collect(Collectors.toList());

        List<Basket.Flight> inboundFlights = basket.getInbounds().stream()
            .flatMap(f -> f.getFlights().stream())
            .collect(Collectors.toList());
        for (Basket.Flight inboundFlight : inboundFlights
            ) {
            travellers.addAll(inboundFlight.getPassengers());
        }
        for (Basket.Flight outboundFlight : outboundFlights
            ) {
            travellers.addAll(outboundFlight.getPassengers());
        }
        return travellers;
    }

    private static List<Basket.Passenger> getOutBoundTravellersToBasket(Basket.Flights outbound) {
        return outbound.getFlights().stream()
            .flatMap(p -> p.getPassengers().stream())
            .collect(Collectors.toList());
    }

    private static List<Basket.Passenger> getInBoundTravellersAddedToBasket(Basket.Flights inbound) {
        return inbound.getFlights().stream()
            .flatMap(p -> p.getPassengers().stream())
            .collect(Collectors.toList());
    }

    private static List<Passenger> getPassengerList(List<Basket.Passenger> travellers) {
        List<Passenger> passengers = new ArrayList<Passenger>();
        for (Basket.Passenger traveller : travellers) {
            passengers.add(com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Passenger.builder()
                .fareProduct(FareProduct.builder()
                    .bundleCode(traveller.getFareProduct().getBundleCode())
                    .code(traveller.getFareProduct().getCode())
                    .description(traveller.getFareProduct().getDescription())
                    .name(traveller.getFareProduct().getName())
                    .quantity(traveller.getFareProduct().getQuantity())
                    .pricing(Pricing.builder()
                        .basePrice(traveller.getFareProduct().getPricing().getBasePrice())
                        .discounts(getDiscount(traveller.getFareProduct().getPricing().getDiscounts()))
                        .taxes(getTaxes(traveller.getFareProduct().getPricing().getTaxes()))
                        .fees(getFee(traveller.getFareProduct().getPricing().getFees()))
                        .totalAmountWithCreditCard(traveller.getPassengerTotalWithCreditCard())
                        .totalAmountWithDebitCard(traveller.getPassengerTotalWithDebitCard())
                        .build()
                    )
                    .build()
                )
                .externalPassengerId(traveller.getCode())
                .age(traveller.getAge())
                .seat(populateSeats(traveller))
                .isLead(traveller.getIsLead())
                .passengerTotalWithCreditCard(String.valueOf(traveller.getPassengerTotalWithCreditCard()))
                .passengerTotalWithDebitCard(String.valueOf(traveller.getPassengerTotalWithDebitCard()))
                .fareType(traveller.getFareType())
                    .infantsOnLap(traveller.getInfantsOnLap())
                .holdItems(getHoldItemForTraveller(traveller))
                .passengerAPIS(Objects.nonNull(traveller.getPassengerAPIS()) ? getPassengerAPIS(traveller.getPassengerAPIS()) : null)
                .build()
            );
        }

        return passengers;
    }

    private static PassengerAPIS getPassengerAPIS(AbstractPassenger.PassengerAPIS apis) {
        return PassengerAPIS.builder()
                .name(Name.builder()
                        .firstName(apis.getName().getFirstName())
                        .fullName(apis.getName().getFullName())
                        .lastName(apis.getName().getLastName())
                        .middleName(apis.getName().getMiddleName())
                        .title(apis.getName().getTitle())
                        .build()
                )
                .countryOfIssue(apis.getCountryOfIssue())
                .dateOfBirth(apis.getDateOfBirth())
                .documentExpiryDate(apis.getDocumentExpiryDate())
                .documentNumber(apis.getDocumentNumber())
                .documentType(apis.getDocumentType())
                .gender(apis.getGender())
                .nationality(apis.getNationality())
                .build();
    }

    private static Seat populateSeats(Basket.Passenger passenger) {
        Seat basketContentSeat = null;
        if (passenger.getSeat() != null) {
            basketContentSeat = Seat.builder().build();
            basketContentSeat.setCode(passenger.getSeat().getCode());
            basketContentSeat.setBundleCode(passenger.getSeat().getBundleCode());
            basketContentSeat.setName(passenger.getSeat().getName());
            basketContentSeat.setPricing(Pricing.builder()
                .basePrice(passenger.getSeat().getPricing().getBasePrice())
                .discounts(getDiscount(passenger.getSeat().getPricing().getDiscounts()))
                .taxes(getTaxes(passenger.getSeat().getPricing().getTaxes()))
                .fees(getFee(passenger.getSeat().getPricing().getTaxes()))
                .totalAmountWithDebitCard(passenger.getSeat().getPricing().getTotalAmountWithDebitCard())
                .totalAmountWithCreditCard(passenger.getSeat().getPricing().getTotalAmountWithCreditCard())
                .build());
            basketContentSeat.setQuantity(passenger.getSeat().getQuantity());
            basketContentSeat.setSeatNumber(passenger.getSeat().getSeatNumber());
            basketContentSeat.setSeatBand(passenger.getSeat().getSeatBand());
        }
        return basketContentSeat;
    }

    public static BasketContent getBasketContentWithHoldItem(List<HoldItemsResponse.HoldItems> holdItemses) {
        List<Passenger> passengers = basketContent.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream())).collect(Collectors.toList());
        for (Passenger passenger : passengers
            ) {
            passenger.setHoldItems(populateHoldItems(holdItemses));
        }
        return basketContent;
    }

    public static BasketContent getBasketContentWithHoldItem(List<HoldItemsResponse.HoldItems> holdItemses, int passengerIndex) {
        List<Passenger> passengers = basketContent.getOutbounds().stream().flatMap(outbound -> outbound.getFlights().stream().flatMap(flight -> flight.getPassengers().stream())).collect(Collectors.toList());
        passengers.get(passengerIndex - 1).setHoldItems(populateHoldItems(holdItemses));
        return basketContent;
    }

    private static List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.HoldItem> populateHoldItems(List<HoldItemsResponse.HoldItems> holdItemses) {
        List<HoldItem> holdItems = holdItemses.stream().map(item ->
            HoldItem.builder()
                .name(item.getLocalizedDescriptions().get(0).getValue())
                .code(item.getProductCode())
                .description(item.getProductType())
                .quantity(1)
                .pricing(Pricing.builder()
                    .basePrice(item.getPrices().get(0).getBasePrice()
                    )
                    .build())
                .build()
        ).collect(Collectors.toList());

        Optional<HoldItem> possibleExcessWeightProduct = holdItems.stream().filter(holdItem -> holdItem.getDescription().equalsIgnoreCase(EXCESS_WEIGHT_PRODUCT)).findAny();

        if (possibleExcessWeightProduct.isPresent()) {
            HoldItem excessWeightProduct = possibleExcessWeightProduct.get();
            Optional<HoldItem> holdBagProduct = holdItems.stream().filter(holdItem -> "HoldBagProduct".equalsIgnoreCase(holdItem.getDescription())).findAny();
            if (!holdBagProduct.isPresent()) {
                throw new IllegalStateException("Excess Weight Item found without Hold Bag Item");
            }
            holdBagProduct.get().setExtraWeight(
                populateExtraWeight(excessWeightProduct));
            holdItems.remove(excessWeightProduct);
        }
        return holdItems;
    }

    private static List<ExtraWeight> populateExtraWeight(HoldItem excessWeightProduct) {
        List<ExtraWeight> extraWeights = new ArrayList<>();
        extraWeights.add(ExtraWeight.builder()
            .name(excessWeightProduct.getName())
            .code(excessWeightProduct.getCode())
            .description(excessWeightProduct.getDescription())
            .quantity(1)
            .pricing(Pricing.builder()
                .basePrice(excessWeightProduct.getPricing().getBasePrice())
                .build())
            .build());
        return extraWeights;
    }

    private static List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> getDiscount(List<AugmentedPriceItem.Discount> discounts) {
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> discounts1 = new ArrayList<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item>();
        for (AugmentedPriceItem discount : discounts
            ) {
            discounts1.add(com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item.builder()
                .name(discount.getName())
                .amount(discount.getAmount())
                .percentage(discount.getPercentage())
                .code(discount.getCode())
                .build());
        }
        return discounts1;
    }

    private static List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> getFee(List<AugmentedPriceItem> fees) {
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> fees1 = new ArrayList<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item>();
        for (AugmentedPriceItem fee : fees
            ) {
            fees1.add(com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item.builder()
                .name(fee.getName())
                .amount(fee.getAmount())
                .percentage(fee.getPercentage())
                .code(fee.getCode())
                .build());
        }
        return fees1;
    }

    private static List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> getTaxes(List<AugmentedPriceItem> taxisListes) {
        List<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item> taxes1 = new ArrayList<com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item>();
        for (AugmentedPriceItem tax : taxisListes
            ) {
            taxes1.add(com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Item.builder()
                .code(tax.getCode())
                .name(tax.getName())
                .percentage(tax.getPercentage())
                .amount(tax.getAmount())
                .build());
        }
        return taxes1;
    }

    public static AddCommentToBookingRequestBody aBasicAddCommentToBooking() {
        return AddCommentToBookingRequestBody.builder()
            .commentType("")
            .comment("")
            .build();
    }

    public static UpdateCommentsOnBookingRequestBody aBasicUpdateCommentOnBooking() {
        return UpdateCommentsOnBookingRequestBody.builder()
                .commentType("")
                .comment("")
                .build();
    }

    private static List<HoldItem> getHoldItemForTraveller(Basket.Passenger traveller) {
        List<AbstractPassenger.HoldItem> holdItems = traveller.getHoldItems();
        List<HoldItem> commitBookingRequestHoldItems = new ArrayList<>();
        if (!"infant".equalsIgnoreCase(traveller.getPassengerDetails().getPassengerType())) {
            for (AbstractPassenger.HoldItem holdItem : holdItems) {
                commitBookingRequestHoldItems.add(
                    HoldItem.builder()
                        .extraWeight(populateExtraWeightBody(holdItem))
                        .code(holdItem.getCode())
                        .name(holdItem.getName())
                        .quantity(holdItem.getQuantity())
                        .pricing(Pricing.builder()
                            .basePrice(holdItem.getPricing().getBasePrice())
                            .discounts(getDiscount(holdItem.getPricing().getDiscounts()))
                            .taxes(getTaxes(holdItem.getPricing().getTaxes()))
                            .fees(getFee(holdItem.getPricing().getFees()))
                            .totalAmountWithCreditCard(holdItem.getPricing().getTotalAmountWithCreditCard())
                            .totalAmountWithDebitCard(holdItem.getPricing().getTotalAmountWithDebitCard())
                            .build())
                        .maxHeight(holdItem.getMaxHeight())
                        .maxlength(holdItem.getMaxLength())
                        .maxWeight(holdItem.getMaxWeight())
                        .maxWidth(holdItem.getMaxWidth())
                        .build()
                );
            }
        }
        return commitBookingRequestHoldItems;
    }

    private static List<ExtraWeight> populateExtraWeightBody(AbstractPassenger.HoldItem holdItem) {
        List<AbstractPassenger.ExtraWeight> extraWeight = holdItem.getExtraWeight();
        List<ExtraWeight> extraWeights = new ArrayList<>();

        for (AbstractPassenger.ExtraWeight ew : extraWeight) {
            extraWeights.add(ExtraWeight.builder()
                .name(ew.getName())
                .code(ew.getCode())
                .description(ew.getDescription())
                .quantity(1)
                .pricing(Pricing.builder()
                    .basePrice(ew.getPricing().getBasePrice())
                    .build())
                .build());
        }

        return extraWeights;
    }
}
