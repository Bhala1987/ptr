package feature.document.steps.helpers;

import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking.Card;
import com.hybris.easyjet.fixture.hybris.invoke.response.PaymentMethodsResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * CreditCardHelper generate a valid CreditCard for a given cardType.
 * It gets card information from the list provided by ej.
 *
 * @author gd <g.dimartino@reply.it>
 */
public class CardHelper {

    private static final List<String> availableCardList = Arrays.asList(Arrays.stream(CardDetails.values()).map(Enum::name).toArray(String[]::new));

    /**
     * Get a mocked card details to be used as payment method in commit booking
     *
     * @return a mocked card object
     */
    public static Card getMockedPayment() {
        String cardCode = "DM";
        CardDetails card = CardDetails.valueOf(cardCode);

        return Card.builder()
                .cardType(cardCode)
                .cardNumberOrToken(card.getNumber())
                .cardSecurityNumber(card.getCvv())
                .cardExpiryMonth(card.getMonth())
                .cardExpiryYear(card.getYear())
                .cardHolderName("Testing card")
                .build();
    }

    /**
     * Get a random valid card details to be used as payment method in commit booking
     *
     * @param allowedCardList is the filtered response of the getPaymentMethodsForChannel with only card type
     * @return a card object in the format expected by the commitBooking
     * @throws EasyjetCompromisedException if the CardDetails enum doesn't include details for none of the cards in allowedCardList
     */
    public static Card getValidCard(List<PaymentMethodsResponse.PaymentMethod> allowedCardList) throws EasyjetCompromisedException {

        List<PaymentMethodsResponse.PaymentMethod> cards = allowedCardList.stream()
                .filter(cardType -> availableCardList.contains(cardType.getCode()))
                .collect(Collectors.toList());

        String cardCode;
        if (cards.isEmpty()) {
            throw new EasyjetCompromisedException("No available data for any of the payment methods allowed");
        } else {
            cardCode = cards.get(new Random().nextInt(cards.size())).getCode();
        }

        CardDetails card = CardDetails.valueOf(cardCode);

        return Card.builder()
                .cardType(cardCode)
                .cardNumberOrToken(card.getNumber())
                .cardSecurityNumber(card.getCvv())
                .cardExpiryMonth(card.getMonth())
                .cardExpiryYear(card.getYear())
                .cardHolderName("Testing card")
                .build();
    }

    enum CardDetails {
        AX("370000000000002", "7373", "8", "2018"), // American Express
        MC("5555444433331111, 5555555555554444", "737, 737", "8, 8", "2018, 2018"), // MasterCard
        //        MC("2223000048410010", "737", "8", "2018"), // MasterCard - Invalid card number
//        MC("5212345678901234", "737", "8", "2018"), // MasterCard - Payment declined
        DC("36006666333344", "737", "8", "2018"), // Diners Club
        DL("4400000000000008", "737", "8", "2018"), // Visa Debit
        //        SW("6759649826438453", "737", "8", "2018"), // UK Maestro - Payment declined
        TP("100100100100103", "737", "8", "2018"), // UATP / Airplus
        DM("5573471234567898", "123", "8", "2018"), // Debit MasterCard
        VI("4111111111111111, 4444333322221111", "737, 737", "8, 8", "2018, 2018"), // Visa
        //        VI("4212345678901237", "737", "8", "2018"), // Visa - Payment declined
        CB("4059350000000050", "123", "8", "2018");// Carte Bleue

        private final String[] number;
        private final String[] cvv;
        private final String[] month;
        private final String[] year;
        private int selected;

        CardDetails(String number, String cvv, String month, String year) {
            this.number = number.split(",\\s");
            this.cvv = cvv.split(",\\s");
            this.month = month.split(",\\s");
            this.year = year.split(",\\s");
            this.selected = new Random().nextInt(this.number.length);
        }

        public String getNumber() {
            return number[selected];
        }

        public String getCvv() {
            return cvv[selected];
        }

        public String getMonth() {
            return month[selected];
        }

        public String getYear() {
            return year[selected];
        }
    }

}