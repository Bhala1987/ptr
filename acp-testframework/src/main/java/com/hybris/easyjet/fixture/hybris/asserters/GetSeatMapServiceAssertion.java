package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.CurrencyModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.GetSeatMapResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jamie on 21/03/2017.
 */
public class GetSeatMapServiceAssertion extends Assertion<GetSeatMapServiceAssertion, GetSeatMapResponse> {
    private final GetSeatMapResponse getSeatMapResponse;

    double theCreditFee = 1.05;


    public GetSeatMapServiceAssertion(GetSeatMapResponse getSeatMapResponse) {
        this.getSeatMapResponse = getSeatMapResponse;
    }

    public GetSeatMapServiceAssertion seatMapIsNotReturned() {
        assertThat(getSeatMapResponse).isNull();
        return this;
    }

    public GetSeatMapServiceAssertion flightKeyIsCorrect(String aFlightKey) {
        assertThat
            ( getSeatMapResponse.getFlight().getFlightKey() )
        .isEqualTo
            ( aFlightKey )
        ;

        return this;
    }

    public GetSeatMapServiceAssertion currencyIsCorrect(String aCurrency) {
        assertThat
            ( getSeatMapResponse.getCurrencyCode() )
        .isEqualTo
            ( aCurrency )
        ;

        return this;
    }

    public GetSeatMapServiceAssertion seatMapContainsAircraftCode(String aAircraftCode) {
        assertThat
            ( getSeatMapResponse.getFlight().getAircraftType() )
        .isEqualTo(
            ( aAircraftCode )
        );

        return this;
    }

    public GetSeatMapServiceAssertion seatOfferPriceIsZero(GetSeatMapResponse.Product seatProductToCheck) {

        getSeatMapResponse.getProducts().forEach(product ->{
            if ( product.getId().equals(seatProductToCheck.getId() ) ){
                assertThat( product.getOfferPrices().getWithCreditCardFee() ).isEqualTo(0);
                assertThat( product.getOfferPrices().getWithDebitCardFee() ).isEqualTo(0);
            }
        });

        return this;
    }

    public GetSeatMapServiceAssertion seatFinalOfferIsDifferenceBetweenSeatProducts(CurrencyModel currency, GetSeatMapResponse.Product seatProductToCheck, GetSeatMapResponse.Product seatProductInBasket) {

        //TODO: Hardcoding credit fee for now. we need a proper solution for rounding policies+credit card fees. -JH

        getSeatMapResponse.getProducts().forEach(productToCheck ->{
            if ( productToCheck.getId().equals( seatProductToCheck.getId() ) ){
                getSeatMapResponse.getProducts().forEach(productInBasket ->{
                    if ( productInBasket.getId().equals(seatProductInBasket.getId() ) ){
                        BigDecimal productTC = new BigDecimal(productToCheck.getBasePrice());
                        BigDecimal productIB = new BigDecimal(productInBasket.getBasePrice());
                        BigDecimal expectedOfferPrice = productTC.subtract(productIB).setScale(2, RoundingMode.HALF_UP);

                        assertThat(BigDecimal.valueOf(productToCheck.getOfferPrices().getWithCreditCardFee())).isEqualTo((expectedOfferPrice.multiply(new BigDecimal(theCreditFee)).setScale(2, RoundingMode.HALF_UP)));
                        assertThat(BigDecimal.valueOf(productToCheck.getOfferPrices().getWithDebitCardFee())).isEqualTo(expectedOfferPrice.stripTrailingZeros());
                    }
                });
            }
        });

        return this;
    }

    public GetSeatMapServiceAssertion offerPriceIsBasePriceForAllSeatProducts() {
        getSeatMapResponse.getProducts().forEach(productToCheck ->{
            assertThat( Double.valueOf(productToCheck.getBasePrice()) ).isEqualTo(productToCheck.getOfferPrices().getWithDebitCardFee() );
            assertThat( roundTheDouble( Double.valueOf(productToCheck.getBasePrice())* theCreditFee )).isEqualTo( productToCheck.getOfferPrices().getWithCreditCardFee());
        });
        return this;
    }

    private Double roundTheDouble(Double aPriceToRound){
        return new BigDecimal(aPriceToRound.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
