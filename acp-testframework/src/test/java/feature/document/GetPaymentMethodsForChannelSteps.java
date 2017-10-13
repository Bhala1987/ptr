package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.PaymentMethodsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.BasketsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.PaymentMethodsService;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CUSTOMER_ID;

/**
 * Created by giuseppedimartino on 26/04/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class GetPaymentMethodsForChannelSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private BasketHelper basketHelper;

    @Given("^I have got the payment method as '(.*)'$")
    public void iHaveGotThePaymentMethod(String bookingType) throws Throwable {
        BasketsResponse basket = basketHelper.getBasketService().getResponse();

        PaymentMethodsQueryParams paymentMethodsQueryParams = PaymentMethodsQueryParams.builder()
                .basketId(basket.getBasket().getCode())
                .bookingTypeCode(bookingType)
                .customerId(testData.getData(CUSTOMER_ID))
                .build();

        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        PaymentMethodsService paymentMethodsService = serviceFactory.getPaymentMethods(new PaymentMethodsRequest(headers.build(), paymentMethodsQueryParams));
        paymentMethodsService.invoke();

        paymentMethodsService.assertThat().paymentMethodsWereReturned();

    }
}