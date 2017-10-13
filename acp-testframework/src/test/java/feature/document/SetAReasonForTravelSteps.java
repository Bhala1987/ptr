package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.SetAReasonHelper;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.fixture.WaitHelper.pollingLoop;

/**
 * Created by Niyi Falade on 02/08/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class SetAReasonForTravelSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private SetAReasonHelper setAReasonHelper;
    @Autowired
    private BasketHelper basketHelper;


    @When("^set (.*) as travel reason using amendable basket ID$")
    public void setAReasonForTravelUsingAmendableBasketID(String bookingReason) throws Throwable {
        setAReasonHelper.setReasonForTravel("12234456565",bookingReason);
    }


    @When("^the requested basket ID can not be identified$")
    public void theRequestedBasketIDCanNotBeIdentified() throws Throwable {
        setAReasonHelper.setReasonForTravel("12234456565","LEISURE");
    }

    @Then("^booking reason service returns errorcode \"([^\"]*)\"$")
    public void bookingReasonServiceReturnsErrorcode(String code) throws Throwable {
        setAReasonHelper.getSetReasonForTravelService().assertThatErrors().containedTheCorrectErrorMessage(code);
    }

    @When("^set (.*) as travel reason using the booking ID$")
    public void setAsTravelReasonUsingTheBookingID(String bookingReason) throws Throwable {
        setAReasonHelper.setReasonForTravel(testData.getBasketId(),bookingReason);
    }

    @When("^no reason is provide to set as travel reason using the booking ID$")
    public void noReasonIsProvideToSetAsTravelReasonUsingTheBookingID() throws Throwable {
        setAReasonHelper.setReasonForTravel(testData.getBasketId(),"");
    }

    @When("^basket contain valid booking type and reason$")
    public void basketContainValidBookingTypeAndReason() throws Throwable {
        pollingLoop().untilAsserted(() -> {

            basketHelper.getBasket(testData.getBasketId());
            basketHelper.getBasketService().assertThat().checkBasketContainsBookingTypeAndReason(basketHelper.getBasketService().getResponse().getBasket());

        });
    }
}
