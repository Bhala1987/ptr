package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.BookingDocumentsRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.BookingDocumentsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.BookingDocumentsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BOOKING_ID;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BookingPathParams.BookingPaths.BOOKING_DOCUMENTS;

/**
 * Created by Alberto
 */
@ContextConfiguration(classes = TestApplication.class)
public class BookingDocumentsSteps {
    protected static Logger LOG = LogManager.getLogger(BookingDocumentsSteps.class);

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    private BookingDocumentsService bookingDocumentsService;

    private static final String PASSENGER_MIX = "1 Adult";

    @And("^the system has a valid booking$")
    public void theSystemHasAValidBooking() throws Throwable {
        testData.setData(BOOKING_ID, bookingHelper.createNewBooking(testData.getChannel(), PASSENGER_MIX).getBookingConfirmation().getBookingReference());
        testData.setBookingResponse(bookingHelper.getBookingDetails(testData.getData(BOOKING_ID), testData.getChannel()));
    }

    @When("^the system has received a valid requestBookingDocuments for a ([^\"]*) with ([^\"]*)$")
    public void theSystemHasReceivedAValidRequestBookingDocumentsForAOutputModeDocumentType(String documentType, String outputMode) {
        BookingDocumentsRequestBody requestBody = createBookingDocumentsRequest(documentType, outputMode);
        BookingPathParams pathParams = BookingPathParams.builder()
                .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                .path(BOOKING_DOCUMENTS)
                .build();

        bookingDocumentsService = serviceFactory.getBookingDocumentsService(
                new BookingDocumentsRequest(
                        HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), pathParams, requestBody));

        bookingDocumentsService.invoke();
    }

    @Then("^it will generate the ([^\"]*) requested$")
    public void itWillGenerateTheDocumentTypeRequested(String documentType) {
        bookingDocumentsService.assertThat().responseHasSelectedType(documentType);
    }

    /**
     * Create Request Body for Booking Documents
     *
     * @param documentType
     * @param outputMode
     * @return
     */
    private BookingDocumentsRequestBody createBookingDocumentsRequest(String documentType, String outputMode) {
        List<BookingDocumentsRequestBody.Documents> documentsList = new ArrayList<>();
        BookingDocumentsRequestBody.Documents document = new BookingDocumentsRequestBody.Documents();

        document.setOutputMode(outputMode);
        document.setType(documentType);
        documentsList.add(document);

        return BookingDocumentsRequestBody
                .builder()
                .documents(documentsList)
                .build();
    }

    /**
     * Create Request Body for Booking Documents Email
     *
     * @param documentType
     * @param outputMode
     * @return
     */
    private BookingDocumentsRequestBody createBookingDocumentsRequestMail(String documentType, String outputMode) {
        List<BookingDocumentsRequestBody.Documents> documentsList = new ArrayList<>();
        BookingDocumentsRequestBody.Documents document = new BookingDocumentsRequestBody.Documents();
        List<String> deliverToEmailList = new ArrayList<>();
        deliverToEmailList.add("john.smith@someemail.com");

        document.setOutputMode(outputMode);
        document.setType(documentType);
        documentsList.add(document);

        return BookingDocumentsRequestBody
                .builder()
                .documents(documentsList)
                .deliverToEmailList(deliverToEmailList)
                .build();
    }

    @When("^the system has received a valid requestBookingDocuments with email for a ([^\"]*) with ([^\"]*)$")
    public void theSystemHasReceivedAValidRequestBookingDocumentsWithEmailForADocumentTypeWithOutputMode(String documentType, String outputMode) {
        BookingDocumentsRequestBody requestBody = createBookingDocumentsRequestMail(documentType, outputMode);
        BookingPathParams pathParams = BookingPathParams.builder()
                .bookingId(testData.getData(SerenityFacade.DataKeys.BOOKING_ID))
                .path(BOOKING_DOCUMENTS)
                .build();

        bookingDocumentsService = serviceFactory.getBookingDocumentsService(
                new BookingDocumentsRequest(
                        HybrisHeaders.getValidWithToken(testData.getChannel(), testData.getAccessToken()).build(), pathParams, requestBody));

        bookingDocumentsService.invoke();
    }

    @Then("^I will receive a error message (ERR-REQUEST-DOCUMENTS-\\d+)$")
    public void iWillReceiveAErrorMessageERRREQUESTDOCUMENTS(String errorCode) {
        bookingDocumentsService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
    }

    @Then("^it will generate the flight details email$")
    public void itWillGenerateTheFlightDetailsEmail() {
        bookingDocumentsService.assertThat().responseHasEmail();
    }
}
