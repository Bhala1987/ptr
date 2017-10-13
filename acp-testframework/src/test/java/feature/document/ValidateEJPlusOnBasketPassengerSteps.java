package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.MembershipDao;
import com.hybris.easyjet.database.hybris.models.MemberShipModel;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import cucumber.api.java.en.*;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Created by giuseppecioce on 24/04/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class ValidateEJPlusOnBasketPassengerSteps {
    public static final String COMPLETED = "COMPLETED";
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private MembershipDao membershipDao;
    @Autowired
    private SerenityFacade testData;
    private Passengers passengersFirst;
    private Passengers passengersSecond;

    private String ejPlus;
    private String surname;
    private MemberShipModel memberShipModel;


    @Given("^I have received a valid updatePassengerDetails request for channel \"([^\"]*)\" and passenger \"([^\"]*)\"$")
    public void iHaveReceivedAValidUpdatePassengerDetailsRequestForChannelAndPassenger(String channel, String passenger) throws Throwable {
        testData.setChannel(channel);
        testData.setChannel(channel);
        passengersFirst = basketHelper.createRequestToUpdateFirstPassenger(passenger, channel);
    }

    @And("^the request contains a EJ plus membership number$")
    public void theRequestContainsAEJPlusMembershipNumber() throws Throwable {
        memberShipModel = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        surname = memberShipModel.getLastname();
        ejPlus = memberShipModel.getEjMemberShipNumber();
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setLastName(memberShipModel.getLastname());
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(memberShipModel.getEjMemberShipNumber());
    }

    @But("^the number of characters passed is less than (\\d+) numeric characters$")
    public void theNumberOfCharactersPassedIsLessThanNumericCharacters(int length) throws Throwable {
        int ejPlusCardNumber = new DataFactory().getNumberBetween(length, Integer.MAX_VALUE);
        String ejPlus = ("" + ejPlusCardNumber).substring(0, (length - 1));
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(ejPlus);
    }

    @But("^the expiry date is in the past$")
    public void theExpiryDateIsInThePast() throws Throwable {
        MemberShipModel memberShipModel = membershipDao.getExpiredEJPlusMembership(COMPLETED);
        String ejPlus = memberShipModel.getEjMemberShipNumber();
        String surname = memberShipModel.getLastname();

        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setLastName(surname);
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(ejPlus);
    }

    @But("^the surname of the passenger passed in the request does not match the surname on the eJ plus membership$")
    public void theSurnameOfThePassengerPassedInTheRequestDoesNotMatchTheSurnameOnTheEJPlusMembership() throws Throwable {
        DataFactory df = new DataFactory();
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setLastName(df.getLastName() + df.getRandomChars(5));
    }

    @But("^the number starts with a S but is less than (\\d+) numeric characters$")
    public void theNumberStartsWithASButIsLessThanNumericCharacters(int length) throws Throwable {
        String ejPlus = passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getEjPlusCardNumber();
        ejPlus = ejPlus.substring(0, length);
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(ejPlus);
    }

    @When("^I validate the eJ Plus membership received$")
    public void iValidateTheEJPlusMembershipReceived() throws Throwable {
        basketHelper.invokeUpdatePassengerService(passengersFirst, testData.getChannel());
    }

    @Then("^I will generate a warning message \"([^\"]*)\"$")
    public void iWillGenerateAWarningMessage(String warning) throws Throwable {
        basketHelper.getBasketPassengerService().assertThat().additionalInformationReturned(warning);
    }

    @But("^the number is more than (\\d+) numeric characters but not starts with a \"([^\"]*)\"$")
    public void theNumberIsMoreThanNumericCharactersButNotStartsWithA(int arg0, String first) throws Throwable {
        char c;
        String ejPlus = passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getEjPlusCardNumber();
        do {
            c = (char) (new Random().nextInt(26) + 'A');
        } while (first.indexOf(c) >= 0);
        ejPlus = ejPlus.replaceFirst("(?i)s", c + "");
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(ejPlus);
    }

    @But("^the same number appears against a different passenger on the booking$")
    public void theSameNumberAppearsAgainstADifferentPassengerOnTheBooking() throws Throwable {
        basketHelper.invokeUpdatePassengerService(passengersFirst, testData.getChannel());
        passengersFirst = basketHelper.createRequestToUpdatePassengerExcludeCode(passengersFirst.getPassengers().stream().findFirst().orElse(null).getCode());
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setLastName(surname);
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(ejPlus);
    }

    @And("^the passenger details will not be updated$")
    public void thePassengerDetailsWillNotBeUpdated() throws Throwable {
        basketHelper.getBasket(basketHelper.getBasketService().getResponse().getBasket().getCode(), testData.getChannel());
        basketHelper.getBasketService().assertThat().thePassengerInformationHasNotBeenUpdated(new ArrayList<String>() {{
            add(passengersFirst.getPassengers().stream().findFirst().orElse(null).getCode());
            if (Objects.nonNull(passengersSecond))
                add(passengersSecond.getPassengers().stream().findFirst().orElse(null).getCode());
        }}, ejPlus);
    }

    @Then("^I will store the membership number against the each instance of the passenger in the basket$")
    public void iWillStoreTheMembershipNumberAgainstTheEachInstanceOfThePassengerInTheBasket() throws Throwable {
        ArrayList<String> list = new ArrayList<>();
        list.add(passengersFirst.getPassengers().stream().findFirst().orElse(null).getCode());
        if (Objects.nonNull(passengersSecond))
            list.add(passengersSecond.getPassengers().stream().findFirst().orElse(null).getCode());
        basketHelper.getBasketService().assertThat().theMembershipHasBeenStored(list, ejPlus, basketHelper, testData.getChannel());
    }

    @And("^I will return the updated basket$")
    public void iWillReturnTheUpdatedBasket() throws Throwable {
        ArrayList<String> codes = new ArrayList<>();
        codes.add(passengersFirst.getPassengers().stream().findFirst().orElse(null).getCode());
        if (Objects.nonNull(passengersSecond))
            codes.add(passengersSecond.getPassengers().stream().findFirst().orElse(null).getCode());
        basketHelper.getBasketService().assertThat().thePassengerInformationHasBeenStored(codes);
    }


    @And("^I received same request for different passenger \"([^\"]*)\" on different flight on the same basket$")
    public void iReceivedSameRequestForDifferentPassengerOnDifferentFlightOnTheSameBasket(String passenger) throws Throwable {
        testData.setOrigin("LTN");
        testData.setDestination("CDG");
        passengersSecond = basketHelper.createRequestToUpdateFirstPassenger(passenger, testData.getChannel());
        passengersSecond.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setLastName(surname);
        passengersSecond.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(ejPlus);

        basketHelper.invokeUpdatePassengerService(passengersSecond, testData.getChannel());
    }

    @Then("^I will return a message \"([^\"]*)\" of error$")
    public void iWillReturnAMessageOfError(String error) throws Throwable {
        basketHelper.getBasketPassengerService().assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @But("^the passengers have the same value for EJ plus membership$")
    public void thePassengersHaveTheSameValueForEJPlusMembership() throws Throwable {
        passengersFirst = basketHelper.createRequestToUpdateAllPassengerOnSameFlight();
        passengersFirst.getPassengers().forEach(item -> {
            item.getPassengerDetails().getName().setLastName(surname);
            item.getPassengerDetails().setEjPlusCardNumber(ejPlus);
        });
    }

    @And("^the passenger details for the first passenger has been updated$")
    public void thePassengerDetailsForTheFirstPassengerHasBeenUpdated() throws Throwable {
        this.iWillStoreTheMembershipNumberAgainstTheEachInstanceOfThePassengerInTheBasket();
    }

    @And("^the passenger details for the second passenger has not been updated$")
    public void thePassengerDetailsForTheSecondPassengerHasNotBeenUpdated() throws Throwable {
        basketHelper.getBasketService().assertThat().thePassengerInformationHasNotBeenUpdated(new ArrayList<String>() {{
            add(passengersFirst.getPassengers().stream().filter(f -> !f.getCode().equalsIgnoreCase(passengersFirst.getPassengers().stream().findFirst().orElse(null).getCode())).findFirst().orElse(null).getCode());
        }}, ejPlus);
    }

    @Given("^my basket contains (\\d+) flight for passenger mix \"([^\"]*)\" using channel \"([^\"]*)\"$")
    public void myBasketContainsFlightForPassengerMixUsingChannel(int numFlight, String passenger, String channel) throws Throwable {
        testData.setChannel(channel);
        basketHelper.addMultipleFlight(numFlight, passenger, channel);
    }

    @And("^I receive valid updatePassengerDetails request for all passenger on the basket$")
    public void iReceiveValidUpdatePassengerDetailsRequestForAllPassengerOnTheBasket() throws Throwable {
        passengersFirst = basketHelper.createRequestToUpdateAllPassenger(true);
    }

    @And("^the request contains same EJ plus membership number for the passenger on different flight$")
    public void theRequestContainsSameEJPlusMembershipNumberForThePassengerOnDifferentFlight() throws Throwable {
        MemberShipModel memberShipModel = membershipDao.getValidEJPlusMembershipForStaffWithStatus(COMPLETED);
        ejPlus = memberShipModel.getEjMemberShipNumber();
        surname = memberShipModel.getLastname();
        passengersFirst.getPassengers().forEach(item -> {
            item.getPassengerDetails().getName().setLastName(surname);
            item.getPassengerDetails().setEjPlusCardNumber(ejPlus);
        });
    }

    @Then("^I will store the membership number against the passengers on different flight$")
    public void iWillStoreTheMembershipNumberAgainstThePassengersOnDifferentFlight() throws Throwable {
        basketHelper.getBasketService().assertThat().theMembershipHasBeenStored(new ArrayList<String>() {{
            passengersFirst.getPassengers().forEach(item -> {
                add(item.getCode());
            });
        }}, ejPlus, basketHelper, testData.getChannel());
    }

    @And("^I will return the updated basket for the passenger on different flight$")
    public void iWillReturnTheUpdatedBasketForThePassengerOnDifferentFlight() throws Throwable {
        basketHelper.getBasketService().assertThat().thePassengerInformationHasBeenStored(new ArrayList<String>() {{
            passengersFirst.getPassengers().forEach(item -> {
                add(item.getCode());
            });
        }});
    }

    @And("^the request contains a EJ plus membership number with status other than (.*)$")
    public void theRequestContainsAEJPlusMembershipNumberWithStatus(String status) throws Throwable {
        MemberShipModel memberShipModel = membershipDao.getEJPlusMemberOtherThanStatus(status);
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().getName().setLastName(memberShipModel.getLastname());
        passengersFirst.getPassengers().stream().findFirst().orElse(null).getPassengerDetails().setEjPlusCardNumber(memberShipModel.getEjMemberShipNumber());
    }
}
