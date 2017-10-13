package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.CarHireHelper;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.services.CarHireService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = TestApplication.class)
public class CarHireRatesSteps {
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private CarHireHelper carHireHelper;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private FlightHelper flightHelper;

    @And("^I request for a car hire with out mandatory fields$")
    public void iRequestForACarHireWithOutMandatoryFields(List<String> mandatoryFields) throws Throwable {
        Map<String, CarHireService> errorServiceResponsesMap = new HashMap<>();
        mandatoryFields.stream().forEach(field -> {
            carHireHelper.addCarHireToBasketWithMissingMandatoryFields(field);
            CarHireService carHireService = testData.getData(SERVICE);
            errorServiceResponsesMap.put(field, carHireService);
        });
        testData.setData(SerenityFacade.DataKeys.SERVICE_CALLS, errorServiceResponsesMap);
    }

    @And("^I request for a car hire with out non mandatory fields$")
    public void iRequestForACarHireWithOutNonMandatoryFields(List<String> mandatoryFields) throws Throwable {
        Map<String, CarHireService> positiveServiceResponsesMap = new HashMap<>();
        mandatoryFields.stream().forEach(field -> {
            carHireHelper.addCarHireToBasketWithMissingNonMandatoryFields(field);
            CarHireService carHireService = testData.getData(SERVICE);
            positiveServiceResponsesMap.put(field, carHireService);
        });
        testData.setData(SerenityFacade.DataKeys.SERVICE_CALLS, positiveServiceResponsesMap);
    }


    @And("^I request for a car hire with invalid (.*) and value (.*)$")
    public void iRequestForACarHireWithInvalidInvalidFieldAndValueValue(String invalidField, String fieldValue) throws Throwable {
        carHireHelper.addCarHireToBasketWithErrorFields(invalidField, fieldValue);
    }

    @And("^I request for a car hire search$")
    public void iRequestForACarHireSearch() throws Throwable {
            carHireHelper.getCarHireQuotes();
            basketHelper.clearBasket(testData.getBasketId(), testData.getChannel());
    }

    @When("^I add the flight to the basket as staff with passenger (.*)$")
    public void iAddTheFlightToTheBasketAsStaffWithPassengerPassengerMix(String passengerMix) throws Throwable {
        try {
            basketHelper.addInboundOutboundFlightsForStaff(passengerMix, "Standard");
        } catch (AssertionError error){
            handelError(error);

        }

    }
    @Then("^I see an error message for car hire mandatory fields (.*)$")
    public void iSeeAnErrorMessage(String errorCode) throws Throwable {
        Map<String, CarHireService> serviceResponsesMap = testData.getData(SerenityFacade.DataKeys.SERVICE_CALLS);
        serviceResponsesMap.keySet().stream().forEach(key -> {
            CarHireService carHireService = serviceResponsesMap.get(key);
            carHireService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);

        });
    }

    @Then("^I see (.*) response code for car hire missing non mandatory fields$")
    public void iSeeASuccessStatusCode(int statusCode) throws Throwable {
        Map<String, CarHireService> errorServiceResponsesMap = testData.getData(SerenityFacade.DataKeys.SERVICE_CALLS);
        errorServiceResponsesMap.keySet().stream().forEach(key -> {
            CarHireService carHireService = errorServiceResponsesMap.get(key);
            assertThat(carHireService.getStatusCode()).withFailMessage("Failed for " + key).isEqualTo(statusCode);
        });
    }

    @Then("^I see an error message for car hire add basket (.*)$")
    public void iSeeAnErrorMessageForCarHireAddBasket(String errorCode) throws Throwable {
        CarHireService carHireService = testData.getData(SERVICE);
        carHireService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @And("^I see car hire search results$")
    public void iSeeCarHireSearchResults() throws Throwable {
        CarHireService carHireService = testData.getData(SERVICE);
        assertThat(carHireService.getStatusCode()).withFailMessage("Failed for ").isEqualTo(200);
    }

    @Then("^I see car hire search results with currency (.*)$")
    public void iRequestForACarHireSearchInExpectedCurrency(String expectedCurrency) throws Throwable {
        CarHireService carHireService = testData.getData(SERVICE);
        assertThat(carHireService.getStatusCode()).withFailMessage("Failed for ").isEqualTo(200);
        carHireService.assertThat().assertCurrenciesDisplayedCorrectly(expectedCurrency);
    }

    @And("^I see car hire products including both credit and debit card prices$")
    public void iSeeProductsIncludingBothCreditAndDebitCardPrices() throws Throwable {
        CarHireService carHireService = testData.getData(SERVICE);
        carHireService.assertThat().assertProductIncludesDebitAndCreditCardPrices();
    }
    private void handelError(AssertionError error) throws EasyjetCompromisedException {
        if(error.getMessage().contains("SVC_100012_20017")||error.getMessage().contains("SVC_100012_3036")){
            throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
        }else{
            Assert.fail(error.getMessage());
        }
    }

}
