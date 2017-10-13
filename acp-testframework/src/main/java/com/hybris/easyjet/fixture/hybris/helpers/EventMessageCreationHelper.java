package com.hybris.easyjet.fixture.hybris.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.database.hybris.dao.BookingDao;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.EventMessagePathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation.CreateBookingCancelledEventMessage;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation.CommentEventMessageRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation.CreateBookingEventMessageRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation.CreateCustomerEventMessageRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.eventmessagecreation.UpdateBookingEventMessageRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.eventmessage.GenerateEventMessageRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.AgentLoginResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.CustomerProfileService;
import com.hybris.easyjet.fixture.hybris.invoke.services.eventmessagecreation.EventMessageService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.EventMessagePathParams.EventPaths.*;

/**
 * Created by tejaldudhale on 08/08/2017.
 */
@Component
public class EventMessageCreationHelper {

    private static final Logger LOG = Logger.getLogger(EventMessageCreationHelper.class.getName());

    private static final String EJ_STORE_ID = "ejCoreStore";
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private SerenityFacade testData;
    private ProcessingReport report;
    private JsonNode mySchema;
    private UpdateBookingEventMessageRequestBody.UpdateBookingEventMessageRequestBodyBuilder updateRequestBody;
    private EventMessageService updateBookingEventService;
    private String bookingRef;
    private EventMessagePathParams eventMessagePathParams;


    public void validateBookingCreationMessage(String bookingRefCode) throws Exception {
        CreateBookingEventMessageRequestBody requestBody = CreateBookingEventMessageRequestBody.builder().build();
        requestBody.setBookingReferenceCode(bookingRefCode);
        requestBody.setBaseStoreUid(EJ_STORE_ID);
        eventMessagePathParams = EventMessagePathParams.builder().path(CREATE_BOOKING_EVENT).build();
        EventMessageService createBookingEventService = serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams, requestBody));
        createBookingEventService.invoke();
        mySchema = getJsonSchema(CommonConstants.JSONSCHEMAS.BOOKING);
        validateSchema(mySchema, createBookingEventService.getResponse().getValue());
        createBookingEventService.assertThat().isValid(report);
    }

    private void setUpdateEventRequestBody(){
        updateRequestBody = UpdateBookingEventMessageRequestBody.builder()
                .bookingReferenceCode(bookingRef)
                .baseStoreUid(EJ_STORE_ID)
                .versionId(bookingDao.getBookingVersionId(bookingRef));
    }

    private void invokeUpdateBookingEvent(){
        eventMessagePathParams = EventMessagePathParams.builder().path(UPDATE_BOOKING_EVENT).build();
        updateBookingEventService = serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams,updateRequestBody.build()));
        updateBookingEventService.invoke();
    }

    public void validateBookingUpdateMessage(String bookingRefCode) throws Exception {
        bookingRef = bookingRefCode;
        setUpdateEventRequestBody();
        invokeUpdateBookingEvent();
        mySchema = getJsonSchema(CommonConstants.JSONSCHEMAS.UPDATE_BOOKING);
        validateSchema(mySchema, updateBookingEventService.getResponse().getValue());
        updateBookingEventService.assertThat().isValid(report);
    }

    public void validateCustomerCreationMessage(String customerID) throws Exception {
        CreateCustomerEventMessageRequestBody requestBody = CreateCustomerEventMessageRequestBody.builder().build();
        requestBody.setCustomerId(customerID);
        eventMessagePathParams = EventMessagePathParams.builder().path(CREATE_CUSTOMER_EVENT).build();
        EventMessageService createCustomerEventMessageService = serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams, requestBody));
        createCustomerEventMessageService.invoke();
        mySchema = getJsonSchema(CommonConstants.JSONSCHEMAS.CREATE_CUSTOMER);
        validateSchema(mySchema, createCustomerEventMessageService.getResponse().getValue());
        createCustomerEventMessageService.assertThat().isValid(report);
    }

    public void validateCustomerUpdateMessage(String customerID) throws Exception {
        CreateCustomerEventMessageRequestBody requestBody = CreateCustomerEventMessageRequestBody.builder().build();
        requestBody.setCustomerId(customerID);
        eventMessagePathParams = EventMessagePathParams.builder().path(UPDATE_CUSTOMER_EVENT).build();
        EventMessageService updateCustomerEventMessageService = serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams, requestBody));
        updateCustomerEventMessageService.invoke();
        mySchema = getJsonSchema(CommonConstants.JSONSCHEMAS.UPDATE_CUSTOMER);
        validateSchema(mySchema, updateCustomerEventMessageService.getResponse().getValue());
        updateCustomerEventMessageService.assertThat().isValid(report);
    }

    public void validateBookingCancelledMessage(String bookingRefCode) throws Exception {
        bookingRef = bookingRefCode;
        EventMessageService bookingCancellationMessage = getBookingCancellationMessage(bookingRef);
        mySchema = getJsonSchema(CommonConstants.JSONSCHEMAS.BOOKING_CANCELLED);

        validateSchema(mySchema, bookingCancellationMessage.getResponse().getValue());
        bookingCancellationMessage.assertThat().isValid(report);
    }

    public void validateCustomerCommentMessage(String commentCode, String type) throws Exception {
        CommentEventMessageRequestBody requestBody = CommentEventMessageRequestBody.builder().build();
        requestBody.setCommentCode(commentCode);
        EventMessageService eventMessageService;
        switch (type){
            case "add":
                eventMessagePathParams = EventMessagePathParams.builder().path(ADD_CUSTOMER_COMMENT_EVENT).build();
                break;
            case "update":
                eventMessagePathParams = EventMessagePathParams.builder().path(UPDATE_CUSTOMER_COMMENT_EVENT).build();
                break;
            case "remove":
                eventMessagePathParams = EventMessagePathParams.builder().path(REMOVE_CUSTOMER_COMMENT_EVENT).build();
                break;
                default:
                    throw new IllegalArgumentException();
        }
        eventMessageService = serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams, requestBody));
        eventMessageService.invoke();
         mySchema = getJsonSchema(CommonConstants.JSONSCHEMAS.CUSTOMER_COMMENT);
        validateSchema(mySchema, eventMessageService.getResponse().getValue());
        eventMessageService.assertThat().isValid(report);

    }

    public void validateBookingCommentMessage(String commentCode,String type) throws Exception {
        CommentEventMessageRequestBody requestBody = CommentEventMessageRequestBody.builder().build();
        requestBody.setCommentCode(commentCode);
        EventMessageService eventMessageService;
        switch (type){
            case "add":
                eventMessagePathParams = EventMessagePathParams.builder().path(ADD_BOOKING_COMMENT_EVENT).build();
                break;
            case "update":
                eventMessagePathParams = EventMessagePathParams.builder().path(UPDATE_BOOKING_COMMENT_EVENT).build();
                break;
            case "remove":
                eventMessagePathParams = EventMessagePathParams.builder().path(REMOVE_BOOKING_COMMENT_EVENT).build();
                break;
            default:
                throw new IllegalArgumentException();
        }
        eventMessageService = serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams, requestBody));
        eventMessageService.invoke();
         mySchema = getJsonSchema(CommonConstants.JSONSCHEMAS.BOOKING_COMMENT);
        validateSchema(mySchema, eventMessageService.getResponse().getValue());
        eventMessageService.assertThat().isValid(report);
    }

    private JsonNode getJsonSchema(String fileName) throws IOException {
        File file = new File(URLDecoder.decode(ClassLoader.getSystemResource("jsonSchema/" + fileName + ".json").getFile(), "UTF-8"));
        return JsonLoader.fromFile(file);
    }

    private void validateSchema(JsonNode schema, String jsonData) throws IOException, ProcessingException {
        try {
            final JsonNode data = JsonLoader.fromString(jsonData);
            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JsonValidator validator = factory.getValidator();
            report = validator.validate(schema, data, true);
        } catch (IOException | ProcessingException e) {
            LOG.info("Failed to validate json data");
            throw e;
        }
    }

    public EventMessageService getBookingCancellationMessage(String bookRef) {
        CreateBookingCancelledEventMessage requestBody = CreateBookingCancelledEventMessage.builder().build();
        requestBody.setBookingReferenceCode(bookRef);
        requestBody.setBaseStoreUid(EJ_STORE_ID);
        eventMessagePathParams = EventMessagePathParams.builder().path(DELETE_BOOKING_EVENT).build();
        EventMessageService updateCustomerEventMessageService = serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams, requestBody));
        updateCustomerEventMessageService.invoke();

        return updateCustomerEventMessageService;
    }

    public EventMessageService getCustomerRegisterMessage(String customerId) {
        CreateCustomerEventMessageRequestBody requestBody = CreateCustomerEventMessageRequestBody.builder().build();
        requestBody.setCustomerId(customerId);
        eventMessagePathParams = EventMessagePathParams.builder().path(CREATE_CUSTOMER_EVENT).build();
        EventMessageService createCustomerEventMessageService =
                serviceFactory.eventMessageService(new GenerateEventMessageRequest(HybrisHeaders.getBasicHeader(),eventMessagePathParams, requestBody));
        createCustomerEventMessageService.invoke();

        return createCustomerEventMessageService;
    }

    public void checkBookingCancelledEventContainsCorrectName(String bookingRef, String performer) {
        String name;
        String firstName;
        String lastName;

        EventMessageService bookingCancellationMessage = getBookingCancellationMessage(bookingRef);

        switch (performer) {
            case "customer":
                CustomerProfileService customerProfileService = testData.getData(SerenityFacade.DataKeys.GET_CUSTOMER_PROFILE_SERVICE);
                firstName = customerProfileService.getResponse().getCustomer().getBasicProfile().getPersonalDetails().getFirstName();
                lastName = customerProfileService.getResponse().getCustomer().getBasicProfile().getPersonalDetails().getLastName();
                name = firstName + " " + lastName;
                break;
            case "agent":
                AgentLoginResponse.Name agentLoginService = testData.getData(SerenityFacade.DataKeys.EMPLOYEE_NAME);
                firstName = agentLoginService.getFirstName();
                lastName = agentLoginService.getLastName();
                name = firstName + " " + lastName;
                break;
            default:
                throw new IllegalArgumentException("Performer " + performer + " not found ");
        }

        bookingCancellationMessage.assertThat()
                .checkThatMessageContainsCorrectName(bookingCancellationMessage.getResponse().getValue(), name);

    }


}
