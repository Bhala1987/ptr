package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Name;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.PassengerAPIS;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.PassengerDetails;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.SpecialRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;
import org.fluttercode.datafactory.impl.DataFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by giuseppecioce on 09/06/2017.
 */
public class CommitBookingFactory {


    public static final String BUSINESS = "BUSINESS";
    public static final String SAMPLE = "Sample";
    public static final String X_CLIENT_TRANSACTION_ID = "00000000-0000-0000-0000-000000000000";
    private static DataFactory df = new DataFactory();

    private CommitBookingFactory() {
    }

    public static CommitBookingRequestBody aBooking(Basket basket, List<PaymentMethod> paymentMethods, boolean overrideWarning)  {
        String bookingReason;
        if (basket.getBasketType().equals(BUSINESS)) {
            bookingReason = BUSINESS;
        } else {
            bookingReason = basket.getBookingReason();
        }

            return CommitBookingRequestBody.builder()
                    .basketCode(basket.getCode())
                    .bookingReason(bookingReason)
                    .bookingType(basket.getBasketType())
                    .customerDeviceContext(
                            CustomerDeviceContext.builder()
                                    .device("WHOCARES")
                                    .ipAddress("10.10.10.10")
                                    .operationalSystem("ZX81")
                                    .build()
                    )
                    .paymentMethods(paymentMethods)
                    .overrideWarning(overrideWarning)
                    .build();
    }
    public static CommitBookingRequestBody aBooking(Basket basket, List<PaymentMethod> paymentMethods, boolean overrideWarning,String channel)  {
        String bookingReason;
        if (basket.getBasketType().equals(BUSINESS)) {
            bookingReason = BUSINESS;
        } else {
            bookingReason = basket.getBookingReason();
        }
        CommitBookingRequestBody commitBookingRequestBody =  CommitBookingRequestBody.builder()
                .bookingReason(bookingReason)
                .bookingType(basket.getBasketType())
                .customerDeviceContext(
                        CustomerDeviceContext.builder()
                                .device("WHOCARES")
                                .ipAddress("10.10.10.10")
                                .operationalSystem("ZX81")
                                .build()
                )
                .paymentMethods(paymentMethods)
                .overrideWarning(overrideWarning)
                .build();
        if(channel.equalsIgnoreCase(CommonConstants.PUBLIC_API_B2B_CHANNEL))
        {
            try {
                commitBookingRequestBody.setBasketContent(BasketContentFactory.getBasketContent(basket));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        else {
            commitBookingRequestBody.setBasketCode(basket.getCode());
        }
        return commitBookingRequestBody;
    }
    public static Passenger aPassenger() {
        Name name = getName();
        return com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger.builder()
                .code("123456")
                .relatedAdult("")
                .passengerDetails(
                        PassengerDetails.builder()
                                .email(df.getEmailAddress())
                                .name(name)
                                .email("success" + name.getFirstName().replace(" ","") + "@abctest.com")
                                .phoneNumber(df.getNumberText(12))
                                .ejPlusCardNumber("")
                                .nifNumber("")
                                .passengerType("adult")
                                .build()
                )
                .age(1)
                .isLead(true)
                .saveToCustomerProfile(false)
                .updateSavedPassengerCode("")
                .passengerAPIS(
                        PassengerAPIS.builder()
                                .name(name)
                                .countryOfIssue("GBR")
                                .nationality("GBR")
                                .gender("MALE")
                                .documentType("PASSPORT")
                                .documentNumber("YT123"+df.getRandomChars(5).toUpperCase())
                                .documentExpiryDate("2099-01-01")
                                .dateOfBirth("1980-01-01")
                                .build()
                )
                .specialRequests(SpecialRequest.builder()
                        .build()
                )
                .build();
    }

    public static Passenger aPassengerWithApis() {
        Name name = getName();
        return com.hybris.easyjet.fixture.hybris.helpers.traveller.Passenger.builder()
                .code("123456")
                .passengerDetails(
                        PassengerDetails.builder()
                                .email(df.getEmailAddress())
                                .name(name)
                                .email("success" + name.getFirstName().replace(" ","") + "@abctest.com")
                                .phoneNumber(df.getNumberText(12))
                                .ejPlusCardNumber("")
                                .nifNumber("")
                                .passengerType("adult")
                                .build()
                )
                .age(1)
                .isLead(true)
                .saveToCustomerProfile(false)
                .updateSavedPassengerCode("")
                .passengerAPIS(
                        PassengerAPIS.builder()
                                .name(
                                        name
                                )
                                .countryOfIssue("GBR")
                                .nationality("GBR")
                                .gender("MALE")
                                .documentType("PASSPORT")
                                .documentNumber("YT123"+df.getRandomChars(5).toUpperCase())
                                .documentExpiryDate("2099-01-01")
                                .dateOfBirth("1980-01-01")
                                .build()
                )
                .specialRequests(SpecialRequest.builder()
                        .build()
                )
                .build();
    }

    public static Name getName() {
        Random random = new Random();

        return Name.builder()
                .firstName(df.getFirstName() + df.getRandomChars(random.nextInt(5) + 1))
                .lastName(df.getLastName() + df.getRandomChars(random.nextInt(5) + 1))
                .middleName(df.getName()+ df.getRandomChars(random.nextInt(5) + 1))
                .fullName(df.getName() + df.getRandomChars(random.nextInt(5) + 1))
                .title("mr")
                .build();
    }

    public static CommitBookingRequestBody aBookingPayloadWithBasketContent(BasketContent content) {

        return CommitBookingRequestBody.builder()
                .bookingReason("")
                .basketCode("")
                .basketContent(content)
                .overrideWarning(true)
                .build();
    }

    public static CommitBookingRequestBody aBookingWithRefundOrFee(Basket basket, boolean overrideWarning, Double paymentAmount, String currency, String reasonCode, String paymentContext, String pmtMethod) {
        String bookingReason;
        if (basket.getBasketType().equals(BUSINESS)) {
            bookingReason = BUSINESS;
        } else {
            bookingReason = basket.getBookingReason();
        }
        return CommitBookingRequestBody.builder()
                .basketCode(basket.getCode())
                .bookingReason(bookingReason)
                .bookingType(basket.getBasketType())
                .customerDeviceContext(
                        CustomerDeviceContext.builder()
                                .device("WHOCARES")
                                .ipAddress("10.10.10.10")
                                .operationalSystem("ZX81")
                                .build()
                )
                .overrideWarning(overrideWarning)
                .refundsAndFees(commitBookingRefundOrFee(paymentAmount,currency,reasonCode, paymentContext, pmtMethod))
                .build();
    }

    private static List<RefundOrFee> commitBookingRefundOrFee(Double amount, String currency, String reasonCode, String paymentContext, String paymentMethod) {
        return Arrays.asList(RefundOrFee.builder().amount(amount)
                .type(CommonConstants.REFUND)
                .currency(currency)
                .primaryReasonCode(reasonCode)
                .primaryReasonName("Customer Cancellation 24 hours")
                .originalPaymentMethodContext(paymentContext)
                .originalPaymentMethod(paymentMethod)
                .build());
    }
}
