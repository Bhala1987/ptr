package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.PaymentModeModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods.PaymentMethod;
import com.hybris.easyjet.fixture.alei.invokers.responses.paymentmethods.PaymentType;
import com.hybris.easyjet.fixture.hybris.invoke.response.PaymentMethodsResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by daniel on 26/11/2016.
 * assertion wrapper for payment methods response object, provides reusable assertions to all tests
 */
public class PaymentMethodsAssertion extends Assertion<PaymentMethodsAssertion, PaymentMethodsResponse> {

    public PaymentMethodsAssertion(PaymentMethodsResponse paymentMethodsResponse) {

        this.response = paymentMethodsResponse;
    }

    public void paymentMethodsWereReturned() {
        assertThat(response.getPaymentMethods().size())
                .isGreaterThan(0)
                .withFailMessage("No payment methods were returned.");
    }


    public void paymentMethodsReturnedContainVoucher() {
        assertThat(
                response.getPaymentMethods().stream()
                        .anyMatch(paymentMethod -> "voucher".equalsIgnoreCase(paymentMethod.getCode()))
        ).isTrue();
    }

    public void paymentModesReturnedAreIn(List<PaymentModeModel> paymentModeModels) {
        List<String> paymentModeCodes = paymentModeModels.stream()
                .map(PaymentModeModel::getPaymenttypes)
                .collect(Collectors.toList());

        response.getPaymentMethods().forEach(
                paymentMethod -> assertThat(paymentModeCodes).contains(paymentMethod.getCode())
        );
    }

    public void applicablePaymentMethodAreReturnedToChannel(List<PaymentMethod> paymentMethods) {
//        TODO: Revisit payment method. Here is the rule :
//        ELV is only valid for EUR currency basket
//        German market - Language of user has to be German
//        ELV payment can only be made when departure days i greater than 14 days away
//        Sector doesn't drive this behaviour
        assertThat(paymentMethods.equals(response.getPaymentMethods()));
    }

    public void applicablePaymentMethodAreReturned() {
        assertThat(response.getPaymentMethods().size()).isNotZero();
    }

    public void returnedPaymentMethodBasedOnBasketType(List<String> allowedPaymentMethod) {
        List<String> returnedPaymentMethods = response.getPaymentMethods().stream()
                .map(PaymentMethodsResponse.PaymentMethod::getPaymentMethod)
                .collect(Collectors.toList());

        allowedPaymentMethod.forEach(
                paymentMethod -> assertThat(returnedPaymentMethods).contains(paymentMethod)
        );
    }

    public void shouldNotReturnedPaymentMethods(List<String> notAllowedPaymentMethod) {
        List<String> returnedPaymentMethods = response.getPaymentMethods().stream()
                .map(PaymentMethodsResponse.PaymentMethod::getPaymentMethod)
                .collect(Collectors.toList());

        notAllowedPaymentMethod.forEach(
                paymentMethod -> assertThat(returnedPaymentMethods).doesNotContain(paymentMethod)
        );
    }

    public void shouldNotReturnedPaymentMethod(List<PaymentType> notAllowedPaymentMethod) {
        List<String> returnedPaymentMethods = response.getPaymentMethods().stream()
                .map(PaymentMethodsResponse.PaymentMethod::getCode)
                .collect(Collectors.toList());

        List<String> eiServicePaymentMethods = notAllowedPaymentMethod.stream()
                .map(PaymentType::getCode)
                .collect(Collectors.toList());

        eiServicePaymentMethods.forEach(
                paymentMethod -> assertThat(returnedPaymentMethods).doesNotContain(paymentMethod)
        );
    }

    public void shouldReturnedPaymentMethod(List<PaymentType> allowedPaymentMethod) {
        List<String> returnedPaymentMethods = response.getPaymentMethods().stream()
                .map(PaymentMethodsResponse.PaymentMethod::getCode)
                .collect(Collectors.toList());

        List<String> eiServicePaymentMethods = allowedPaymentMethod.stream()
                .map(PaymentType::getCode)
                .collect(Collectors.toList());

        eiServicePaymentMethods.forEach(
                paymentMethod -> assertThat(returnedPaymentMethods).contains(paymentMethod)
        );
    }

    public void shouldReturnCorrectPaymentTypes(List<PaymentType> paymentTypes) throws EasyjetCompromisedException {
        List<String> returnedPaymentMethods = response.getPaymentMethods().stream()
                .map(PaymentMethodsResponse.PaymentMethod::getPaymentMethod)
                .collect(Collectors.toList());

        List<String> eiPaymentTypes = paymentTypes.stream()
                .map(PaymentType::getPaymentMethod)
                .collect(Collectors.toList());

        returnedPaymentMethods.forEach(
                paymentMethod -> assertThat(eiPaymentTypes).contains(paymentMethod)
        );

        response.getPaymentMethods().forEach(paymentMethod ->
                verifyPaymentMethods(paymentTypes.stream()
                                .filter(eiPaymentTypes1 -> eiPaymentTypes1.getCode().equals(paymentMethod.getCode()))
                                .findFirst()
                                .get(),
                        paymentMethod
                )
        );
    }

    public void shouldContainExpectedPaymentType(List<PaymentType> paymentTypes) throws EasyjetCompromisedException {
        for (PaymentMethodsResponse.PaymentMethod type : response.getPaymentMethods()) {
            if (type.getPaymentMethod().equals(paymentTypes.get(0).getPaymentMethod())) {
                verify(type, paymentTypes);
                break;
            }
        }
    }

    private void verify(PaymentMethodsResponse.PaymentMethod type, List<PaymentType> eiPaymentTypes) {
        assertThat(type.getPaymentMethod().equals(eiPaymentTypes.get(0).getPaymentMethod()));
        assertThat(type.getCode().equals(eiPaymentTypes.get(0).getCode()));
    }

    private void verifyPaymentMethods(PaymentType responsePaymentType, PaymentMethodsResponse.PaymentMethod eiPaymentMethod) {
        assertThat(
                responsePaymentType.getPaymentMethod().equalsIgnoreCase(eiPaymentMethod.getPaymentMethod())
        ).isTrue();

        assertThat(
                responsePaymentType.getPaymentMethodId().equalsIgnoreCase(eiPaymentMethod.getPaymentMethodId())
        ).isTrue();
    }
}
