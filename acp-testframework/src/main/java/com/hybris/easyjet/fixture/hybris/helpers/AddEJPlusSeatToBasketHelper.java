package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BasketDao;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.dao.SSRDataDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.database.hybris.models.SSRDataModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by Raja on 04/05/2017.
 */
@Component
public class AddEJPlusSeatToBasketHelper {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private
    SSRDataDao ssrDataDao;
    @Autowired
    private TravellerHelper travellerHelper;
    @Autowired
    private MembershipDao membershipDao;
    @Autowired
    private BasketDao basketDao;
    @Autowired
    private TravellerHelper passengerHelper;

    private Passengers passengersFirst;
    private String ejPlus;
    private String surname;
    public static final String COMPLETED = "COMPLETED";

    public void verifySeatIsUpdated(String seat, String price) throws EasyjetCompromisedException {

        String productCode;
        String passengerCode;
        Double actualBasePriceForSeatProduct;
        Double expectedBasePriceForSeatProduct = Double.parseDouble(price);
        switch (seat) {
            case "Extra legroom":
                productCode = "1";
                break;
            case "Up front":
                productCode = "2";
                break;
            case "Standard":
                productCode = "3";
                break;
            default:
                productCode = "3";
        }

        String flightKey = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();

        passengersFirst = travellerHelper.updateInformationForFirstPassenger(basketHelper.getBasketService().getResponse().getBasket(), flightKey);
        passengerCode = passengersFirst.getPassengers().stream().findFirst().orElse(null).getCode();
        actualBasePriceForSeatProduct = basketDao.getSeatProductPriceForPassenger(passengerCode, productCode);
        assertThat(actualBasePriceForSeatProduct.doubleValue()).isEqualTo(expectedBasePriceForSeatProduct);
    }


    public void updateFirstPassengerWithEJPlus(String channel, String type) throws EasyjetCompromisedException, InterruptedException {
        MemberShipModel ejPlusMember = null;
        String flightKey = basketHelper.getBasketService().getResponse().getBasket().getOutbounds().get(0).getFlights().get(0).getFlightKey();
        passengersFirst = travellerHelper.updateInformationForFirstPassenger(basketHelper.getBasketService().getResponse().getBasket(), flightKey);
        if ("customer".equalsIgnoreCase(type)) {
            ejPlusMember = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        } else if ("staff".equalsIgnoreCase(type)) {
            ejPlusMember = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        }
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());
        Passenger passenger = passengers.getPassengers().stream().findFirst().orElse(null);
        passenger.getPassengerDetails().setEjPlusCardNumber(ejPlusMember != null ? ejPlusMember.getEjMemberShipNumber() : null);
        passenger.getPassengerDetails().getName().setLastName(ejPlusMember != null ? ejPlusMember.getLastname() : null);
        basketHelper.invokeUpdatePassengerService(passengers, channel);
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), channel);
        basketHelper.getBasketService().assertThat().theMembershipHasBeenStored(new ArrayList<String>() {
            {
                add(passenger.getCode());
            }
        }, ejPlusMember != null ? ejPlusMember.getEjMemberShipNumber() : null, basketHelper, channel);

        basketHelper.getBasketService().assertThat().thePassengerInformationHasBeenStored(new ArrayList<String>() {
            {
                add(passenger.getCode());
            }
        });
        testData.setData("ejPlusMember", ejPlusMember);
    }

    public void updateSelectedPassengerWithEJPlus(String channel, String type, String passengerIndex) throws EasyjetCompromisedException, InterruptedException {
        MemberShipModel ejPlusMember = null;
        if ("customer".equalsIgnoreCase(type)) {
            ejPlusMember = membershipDao.getEJPlusMemberBasedOnStatus(COMPLETED);
        } else if ("staff".equalsIgnoreCase(type)) {
            ejPlusMember = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        }
        Passengers passengers = passengerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse(), true);
        List<SavedSSRs.Ssr> mySsrList = getSsrs(testData.getOrigin() + testData.getDestination());
        if (ejPlusMember != null) {
            ejPlus = ejPlusMember.getEjMemberShipNumber();
        }
        if (ejPlusMember != null) {
            surname = ejPlusMember.getLastname();
        }
        if (!passengers.getPassengers().isEmpty()) {
            passengers.getPassengers().stream().filter(passenger -> passenger.getCode().equalsIgnoreCase(passengerIndex)).forEach(passenger1 -> {
                passenger1.getPassengerDetails().setEjPlusCardNumber(ejPlus);
                passenger1.getPassengerDetails().getName().setLastName(surname);
                passenger1.getSpecialRequests().setSsrs(mySsrList);
            });
        }

        basketHelper.invokeUpdatePassengerService(passengers, channel);
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), channel);
        final String code = passengers.getPassengers().stream().filter(passenger1 -> passenger1.getCode().equalsIgnoreCase(passengerIndex)).findFirst().get().getCode();
        final List<String> passengerCode = new ArrayList<>();
        passengerCode.add(code);
        basketHelper.getBasketService().assertThat().theMembershipHasBeenStored((ArrayList) passengerCode, ejPlus, basketHelper, channel);
        basketHelper.getBasketService().assertThat().thePassengerInformationHasBeenStored(new ArrayList<String>() {
            {
                add(code);
            }
        });
    }

    private List<SavedSSRs.Ssr> getSsrs(String sector) {

        List<SSRDataModel> ssrDataForValidSector = ssrDataDao.getSSRDataForValidSector(true, testData.getChannel(), sector);
        List<SavedSSRs.Ssr> ssrs = new ArrayList<>();
        for (SSRDataModel ssrDataModel : ssrDataForValidSector) {
            SavedSSRs.Ssr ssr = new SavedSSRs.Ssr();
            ssr.setCode(ssrDataModel.getCode());
            ssr.setDescription("");
            ssrs.add(ssr);
        }
        return ssrs;
    }
}

