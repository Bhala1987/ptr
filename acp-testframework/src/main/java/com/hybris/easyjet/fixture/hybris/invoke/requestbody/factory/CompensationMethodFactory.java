package com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory;

import com.hybris.easyjet.fixture.hybris.invoke.requestbody.CreateCompensationRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.basket.common.Basket;

/**
 * Created by Niyi Falade on 19/09/17.
 */
public class CompensationMethodFactory {

    public static CreateCompensationRequestBody generateVoucherCompensation(Basket.Passenger passenger, String currency, String compensationAmount) {

        return CreateCompensationRequestBody.builder()
                .passengerCode(passenger.getCode())
                .primaryReasonCode("AB8399")
                .secondaryReasonCode("AB6567")
                .currencyCode(currency)
                .amount(compensationAmount)
                .paymentMethod(CreateCompensationRequestBody.PaymentMethod.builder().paymentType("VOUCHER")
                        .email("test@abc.com")
                        .nameOnVoucher("Simon black").build()).build();
    }

    public static CreateCompensationRequestBody generateChequeCompensation(Basket.Passenger passenger, String currency, String compensationAmount) {

        return CreateCompensationRequestBody.builder()
                .passengerCode(passenger.getCode())
                .primaryReasonCode("AB8399")
                .secondaryReasonCode("AB6567")
                .currencyCode(currency)
                .amount(compensationAmount)
                .paymentMethod(CreateCompensationRequestBody.PaymentMethod.builder().paymentType("CHEQUE")
                        .nameOnCheque("Simon black")
                        .address(CreateCompensationRequestBody.PaymentMethod.Address.builder()
                                .addressLine1("52, Main Street")
                                .addressLine2("Flat 2B")
                                .addressLine3("")
                                .city("Oxford")
                                .county_state("Oxfordshire")
                                .country("GBR")
                                .postalCode("OX11 2ES").build()).build())
                .build();
    }

    public static CreateCompensationRequestBody generateBankAccountCompensation(Basket.Passenger passenger, String currency, String compensationAmount) {

        return CreateCompensationRequestBody.builder()
                .passengerCode(passenger.getCode())
                .primaryReasonCode("AB8399")
                .secondaryReasonCode("AB6567")
                .currencyCode(currency)
                .amount(compensationAmount)
                .paymentMethod(CreateCompensationRequestBody.PaymentMethod.builder().paymentType("BANKACCOUNT")
                        .accountNumber("09689933")
                        .bankName("ABC Bank")
                        .bankCity("Bolton")
                        .bankSortCode("00-00-00").build())
                .build();
    }

    public static CreateCompensationRequestBody generateCreditFileCompensation(Basket.Passenger passenger, String currency, String compensationAmount) {

        return CreateCompensationRequestBody.builder()
                .passengerCode(passenger.getCode())
                .primaryReasonCode("AB8399")
                .secondaryReasonCode("AB6567")
                .currencyCode(currency)
                .amount(compensationAmount)
                .paymentMethod(CreateCompensationRequestBody.PaymentMethod.builder().paymentType("CREDITFILEFUND")
                        .creditFileName("ACTIVE_GBP")
                        .build())
                .build();
    }

    public static CreateCompensationRequestBody generateVoucherCompensation(String passengerCode, String flightKey, String currency, String paymentType, String nameOnVoucher) {

        return CreateCompensationRequestBody.builder()
                .passengerCode(passengerCode)
                .flightKey(flightKey)
                .primaryReasonCode("AB8399")
                .secondaryReasonCode("AB6567")
                .currencyCode("GBP")
                .amount("100")
                .paymentMethod(CreateCompensationRequestBody.PaymentMethod.builder().paymentType(paymentType)
                        .email("test@abc.com")
                        .nameOnVoucher(nameOnVoucher).build()).build();
    }

    public static CreateCompensationRequestBody generateAllDataCompensation
            (String passengerCode, String flightKey, String currency,
             String paymentType, String nameOnVoucher, String email,
             String creditFile, String accNumber, String bankName, String bankCity, String sortCode,
             String address1, String address2, String address3, String city,
             String county, String country, String postCode, String nameOnCheque) {

        return CreateCompensationRequestBody.builder()
                .passengerCode(passengerCode)
                .flightKey(flightKey)
                .primaryReasonCode("AB8399")
                .secondaryReasonCode("AB6567")
                .currencyCode(currency)
                .amount("100")
                .paymentMethod(CreateCompensationRequestBody.PaymentMethod.builder().paymentType(paymentType)
                        .email(email)
                        .nameOnCheque(nameOnCheque)
                        .nameOnVoucher(nameOnVoucher)
                        .creditFileName(creditFile)
                        .accountNumber(accNumber)
                        .bankName(bankName)
                        .bankCity(bankCity)
                        .bankSortCode(sortCode)
                           .address(CreateCompensationRequestBody.PaymentMethod.Address.builder()
                                .addressLine1(address1)
                                .addressLine2(address2)
                                .addressLine3(address3)
                                .city(city)
                                .county_state(county)
                                .country(country)
                                .postalCode(postCode).build()).build()).build();
    }
}
