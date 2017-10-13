package feature.document.steps.services.managebookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.database.hybris.dao.BookingPermissionDao;
import com.hybris.easyjet.database.hybris.dao.CurrenciesDao;
import com.hybris.easyjet.database.hybris.models.BookingPermissionModel;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.fixture.hybris.asserters.BookingAssertion;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.GetBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.GetBookingService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetBookingSteps handle the communication with the getBookings service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetBookingSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BookingPermissionDao bookingPermissionDao;
    @Steps
    private BookingAssertion bookingAssertion;

    @Autowired
    private CurrenciesDao currenciesDao;
    @Autowired
    private BookingDao bookingDao;

    private GetBookingService getBookingService;
    private BookingPathParams.BookingPathParamsBuilder bookingPathParams;

    private void setPathParameter() {
        bookingPathParams = BookingPathParams.builder().bookingId(testData.getData(BOOKING_ID));
    }

    private void invokeGetBookingService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getBookingService = serviceFactory.getBookings(new GetBookingRequest(headers.build(), bookingPathParams.build()));
        testData.setData(SERVICE, getBookingService);
        testData.setData(GET_BOOKING_SERVICE, getBookingService);
        getBookingService.invoke();
    }

    @Step
    public void sendGetBookingRequest() {
        setPathParameter();
        invokeGetBookingService();
        testData.setData(GET_BOOKING_RESPONSE, getBookingService.getResponse());
    }

    @When("^I send the request to getBooking service$")
    public void getBookingRequest() {
        setPathParameter();
        invokeGetBookingService();
    }

    @Then("^I expect to see only appropriate allowed functions returned with the booking for:$")
    public void iExpectToSeeOnlyAppropriateAllowedFunctionsReturnedWithTheBookingFor(List<BookingPermissionModel> bookingPermissionModels) {
        assert bookingPermissionModels.size() == 1;

        BookingPermissionModel bookingPermissionModel = bookingPermissionModels.get(0);

        getBookingService.assertThat().theBookingHasTheCorrectAllowedFunctions(
            bookingPermissionDao.getBookingPermissions(
                bookingPermissionModel.getChannel(),
                bookingPermissionModel.getBookingType(),
                bookingPermissionModel.getAccessType()
            )
        );
    }

    @Then("^the booking booking type is (STANDARD_CUSTOMER|based on deal)$")
    public void checkBookingType(String bookingType) {
        sendGetBookingRequest();
        if (testData.keyExist(DEALS)) {
            List<DealModel> deals = testData.getData(DEALS);
            bookingType = deals.get(0).getBookingType();
        }
        getBookingService.assertThat()
                .bookingTypeIsRight(bookingType);
    }


    @And("^I store the margin percentage on the OrderEntry$")
    public void iStoreTheMarginPercentageOnTheOrderEntry() {
        bookingAssertion.setResponse(testData.getData(GET_BOOKING_RESPONSE));
        bookingAssertion.verifyMarginValueIntheOrder(currenciesDao.getCurrencyConversionPropertyValue(), bookingDao);
    }

}