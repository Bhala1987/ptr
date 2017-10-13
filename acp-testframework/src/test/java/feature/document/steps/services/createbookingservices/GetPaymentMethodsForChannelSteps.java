package feature.document.steps.services.createbookingservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.PaymentMethodsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.BasketContent;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Card;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.PaymentMethod;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PaymentMethodsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.PaymentMethodsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.PaymentMethodsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import feature.document.steps.helpers.CardHelper;
import feature.document.steps.services.createbasketservices.GetBasketSteps;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static org.assertj.core.api.Java6Assertions.fail;

/**
 * GetPaymentMethodsForChannelSteps handle the communication with the getPaymentMethodsForChannel service (aka getPaymentMethods).
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetPaymentMethodsForChannelSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Steps
    private GetBasketSteps getBasketSteps;

    private PaymentMethodsService paymentMethodsService;
    private PaymentMethodsQueryParams.PaymentMethodsQueryParamsBuilder paymentMethodsQueryParams;

    private Card creditCardDetails;
    private Boolean isCreditCard;
    private String currencyCode;
    private Double paymentAmount;

    private void setQueryParameter() {
        paymentMethodsQueryParams = PaymentMethodsQueryParams.builder()
                .basketId(testData.getData(BASKET_ID))
                .bookingType(testData.getData(BOOKING_TYPE))
                .customerId(testData.getData(CUSTOMER_ID));
    }

    private void invokePaymentMethodsService() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        paymentMethodsService = serviceFactory.getPaymentMethods(new PaymentMethodsRequest(headers.build(), paymentMethodsQueryParams.build()));
        testData.setData(SERVICE, paymentMethodsService);
        paymentMethodsService.invoke();
    }

    private void sendPaymentMethodsRequest() {
        setQueryParameter();
        invokePaymentMethodsService();
    }

    private void selectMockedCard() throws EasyjetCompromisedException {
        creditCardDetails = CardHelper.getMockedPayment();
        isCreditCard = false;
    }

    private void selectRandomCard() throws EasyjetCompromisedException {
        List<PaymentMethodsResponse.PaymentMethod> paymentMethods = testData.getData(PAYMENT_METHODS);
        List<PaymentMethodsResponse.PaymentMethod> cardPaymentMethods = paymentMethods.stream()
                .filter(paymentMethod -> paymentMethod.getPaymentMethod().equalsIgnoreCase("card"))
                .collect(Collectors.toList());

        creditCardDetails = CardHelper.getValidCard(cardPaymentMethods);

        isCreditCard = cardPaymentMethods.stream()
                .filter(paymentMethod -> paymentMethod.getCode().equals(creditCardDetails.getCardType()))
                .findFirst().get()
                .getIsCreditCard();
    }

    private void setCardRequestBody() throws EasyjetCompromisedException {
        PaymentMethod.PaymentMethodBuilder paymentMethodBody = PaymentMethod.builder()
                .paymentMethod("card")
                .paymentCode(creditCardDetails.getCardType())
                .card(creditCardDetails)
                .paymentCurrency(currencyCode)
                .paymentAmount(paymentAmount)
                .savePaymentMethod(false);

        testData.setData(PAYMENT_METHOD, paymentMethodBody.build());
    }

    private void setCardParameters() {
        if (testData.getData(CHANNEL).equals("PublicApiB2B")) {
            BasketContent basketContent = testData.getData(BASKET_CONTENT);
            currencyCode = basketContent.getCurrency().getCode();
            if (isCreditCard) {
                paymentAmount = basketContent.getTotalAmountWithCreditCard();
            } else {
                paymentAmount = basketContent.getTotalAmountWithDebitCard();
            }
        } else {
            getBasketSteps.sendGetBasketRequest(testData.getData(BASKET_ID));
            BasketService basketService = testData.getData(BASKET_SERVICE);
            Basket basket = basketService.getResponse().getBasket();

            currencyCode = basket.getCurrency().getCode();
            if (basket.getTotalAmountWithDebitCard() < 0 || basket.getTotalAmountWithCreditCard() < 0) {
                fail("Cart total is negative.");
            } else if (Objects.nonNull(basket.getPriceDifference())) {
                if (isCreditCard) {
                    paymentAmount = basket.getPriceDifference().getAmountWithCreditCard();
                } else {
                    paymentAmount = basket.getPriceDifference().getAmountWithDebitCard();
                }
            } else {
                if (isCreditCard) {
                    paymentAmount = basket.getTotalAmountWithCreditCard();
                } else {
                    paymentAmount = basket.getTotalAmountWithDebitCard();
                }
            }
        }
    }

    @Step
    @And("^I have a valid payment method$")
    public void getMockedCardPaymentMethod() throws EasyjetCompromisedException {
        selectMockedCard();
        setCardParameters();
        setCardRequestBody();
    }

    @Step
    public void getValidCardPaymentMethod() throws EasyjetCompromisedException {
        sendPaymentMethodsRequest();
        testData.setData(PAYMENT_METHODS, paymentMethodsService.getResponse().getPaymentMethods());
        selectRandomCard();
        setCardParameters();
        setCardRequestBody();
    }

    @When("^I send the request to getPaymentMethodsForChannel service$")
    public void getPaymentMethodsForChannel() {
        sendPaymentMethodsRequest();
    }
}