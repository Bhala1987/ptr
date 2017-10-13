package com.hybris.easyjet.exceptions;

/**
 * Created by tejal on 12/12/2016.
 * Custom exception class which allows tests errors to be thrown and reported as 'compromised' by Serenity
 */
public class HybrisCompromisedException extends Exception {

    /**
     * @param message the message that you wish to include in the exception being thrown
     */
    public HybrisCompromisedException(HybrisCompromisedExceptionMessages message) {
        super(message.getMessage());
    }

    public HybrisCompromisedException(String message) {
        super(message);
    }

    /**
     * A list of enumerated exception messages
     */
    public enum HybrisCompromisedExceptionMessages {
        FLIGHT_NOT_ADDED("Flight is not added to the basket."),
        INCORRECT_DATE("Date is not parsable.");
        private final String message;

        /**
         * @param message error message string
         */
        HybrisCompromisedExceptionMessages(String message) {
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
