package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by giuseppedimartino on 30/03/17.
 */
@Getter
@Setter
public abstract class AbstractConfirmation<T extends AbstractConfirmation.OperationConfirmation> extends Response {
    protected T confirmation;

    @JsonGetter("operationConfirmation")
    public T getOperationConfirmation() {
        return confirmation;
    }

    @JsonSetter("operationConfirmation")
    public void setOperationConfirmation(T operationConfirmation) {
        this.confirmation = operationConfirmation;
    }

    @JsonGetter("updateConfirmation")
    public T getUpdateConfirmation() {
        return confirmation;
    }

    @JsonSetter("updateConfirmation")
    public void setUpdateConfirmation(T updateConfirmation) {
        this.confirmation = updateConfirmation;
    }

    @JsonGetter("bookingConfirmation")
    public T getBookingConfirmation() {
        return confirmation;
    }

    @JsonSetter("bookingConfirmation")
    public void setBookingConfirmation(T bookingConfirmation) {
        this.confirmation = bookingConfirmation;
    }

    @JsonGetter("eligibilityConfirmation")
    public T getEligibilityConfirmation() {
        return confirmation;
    }

    @JsonSetter("eligibilityConfirmation")
    public void setEligibilityConfirmation(T eligibilityConfirmation) {
        this.confirmation = eligibilityConfirmation;
    }

    @JsonGetter("resetPasswordConfirmation")
    public T getResetPasswordConfirmation() {
        return confirmation;
    }

    @JsonSetter("resetPasswordConfirmation")
    public void setResetPasswordConfirmation(T resetPasswordConfirmation) {
        this.confirmation = resetPasswordConfirmation;
    }

    @JsonGetter("deleteConfirmation")
    public T getDeleteConfirmation() {
        return confirmation;
    }

    @JsonSetter("deleteConfirmation")
    public void setDeleteConfirmation(T deleteConfirmation) {
        this.confirmation = deleteConfirmation;
    }

    @JsonGetter("registrationConfirmation")
    public T getRegistrationConfirmation() {
        return confirmation;
    }

    @JsonSetter("registrationConfirmation")
    public void setRegistrationConfirmation(T registrationConfirmation) {
        this.confirmation = registrationConfirmation;
    }

    @JsonGetter("generatePasswordConfirmation")
    public T getGeneratePasswordConfirmation() {
        return confirmation;
    }

    @JsonSetter("generatePasswordConfirmation")
    public void setGeneratePasswordConfirmation(T generatePasswordConfirmation) {
        this.confirmation = generatePasswordConfirmation;
    }

    @JsonGetter("flightInterestConfirmation")
    public T getFlightInterestConfirmation() {
        return confirmation;
    }

    @JsonSetter("flightInterestConfirmation")
    public void setFlightInterestConfirmation(T flightInterestConfirmation) {
        this.confirmation = flightInterestConfirmation;
    }

    @Getter
    @Setter
    public static class OperationConfirmation {
        private String href;
    }

}