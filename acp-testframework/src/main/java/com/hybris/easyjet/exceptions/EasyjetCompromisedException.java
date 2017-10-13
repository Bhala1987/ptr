package com.hybris.easyjet.exceptions;

/**
 * Created by dwebb on 12/12/2016.
 * Custom exception class which allows tests errors to be thrown and reported as 'compromised' by Serenity
 */
public class EasyjetCompromisedException extends Exception {

    /**
     * @param message the message that you wish to include in the exception being thrown
     */
    public EasyjetCompromisedException(EasyJetCompromisedExceptionMessages message) {
        super(message.getMessage());
    }

    public EasyjetCompromisedException(String message) {
        super(message);
    }

    /**
     * A list of enumerated exception messages
     */
    public enum EasyJetCompromisedExceptionMessages {
        INSUFFICIENT_DATA("There was insufficient data available to allow this test to be executed."),
        NO_FLIGHTS_IN_ERES("There were no available flights in eRes."),
        NO_FLIGHTS_IN_ERES_AND_HYBRIS("There were flights in eRes but none of them exist in Hybris."),
        INSUFFICIENT_UNIQUE_CURRENCY_FLIGHTS("There were not flights with unique base currency"),
        NO_CUSTOMER_DATA_IN_HYBRIS("There was no customer data in Hybris."),
        NO_DEALS_DATA_IN_HYBRIS("There was no deals data in Hybris."),
        INVALID_SEARCH_PARAMETER("Invalid search parameter"),
        NO_FLIGHTS_FOR_PRODUCT("There where no flights in hybris with sufficient availability for the required product"),
        INSUFFICIENT_STOCK_LEVEL_FLIGHTS("There were not stockflitght available for the flights");
        private final String message;

        /**
         * @param message error message string
         */
        EasyJetCompromisedExceptionMessages(String message) {
            this.message = message;
        }

        /**
         * @return the error message from the enum
         */
        public String getMessage() {
            return message;
        }

    }
}
