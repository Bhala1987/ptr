package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BookingHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CommitBookingRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.CustomerDeviceContext;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.PaymentMethodFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.CommitBookingRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_ID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * Created by giuseppedimartino on 26/04/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class SaveBookingSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BookingHelper commitBookingHelper;
    @Autowired
    private BasketHelper basketHelper;

    @When("^I commit the booking$")
    public void iCommitTheBooking() throws Throwable {
        basketHelper.getBasket(testData.getData(BASKET_ID));
        BasketsResponse basket = basketHelper.getBasketService().getResponse();
        CommitBookingRequestBody requestBody = CommitBookingRequestBody.builder()
                .overrideWarning(true)
                .basketCode(basket.getBasket().getCode())
                .bookingReason(basket.getBasket().getBookingReason())
                .bookingType(basket.getBasket().getBasketType())
                .customerDeviceContext(
                        CustomerDeviceContext.builder()
                                .device("WHOCARES")
                                .ipAddress("10.10.10.10")
                                .operationalSystem("ZX81")
                                .build()
                )
                .paymentMethods(
                    Arrays.asList(
                        PaymentMethodFactory.generateDebitCardPaymentMethod(basket.getBasket())
                    )
                ).build();

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        CommitBookingService commitBooking = serviceFactory.commitBooking(new CommitBookingRequest(headers.build(), requestBody));
        testData.setData(SERVICE, commitBooking);

                basketHelper.getBasket(basket.getBasket().getCode(), testData.getChannel());
                commitBooking.invoke();
    }
}