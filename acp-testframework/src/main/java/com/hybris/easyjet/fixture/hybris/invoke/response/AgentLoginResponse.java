package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.LoginResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rajakm on 08/05/2017.
 */
public class AgentLoginResponse extends LoginResponse<AgentLoginResponse.AuthenticationConfirmationResponse> {

    @Getter
    @Setter
    public static class AuthenticationConfirmationResponse extends LoginResponse.AuthenticationConfirmation {
        private Agent agent;
        private Authentication authentication;
        private Authorisation authorisation;
        private List<Message> messages;
    }

    @Getter
    @Setter
    public static class Agent {
        private Address address;
        private String agentId;
        private List<Object> agentLocations = null;
        private String defaultCurrency;
        private String defaultLanguage;
        private String employeeEmail;
        private Name name;
    }

    @Getter
    @Setter
    public static class Authorisation {
        private List<AgentGroup> agentGroups = null;
        private List<Object> agentPermissions = null;
    }

    @Getter
    @Setter
    public static class AgentGroup {
        private String code;
        private String name;
    }

    @Getter
    @Setter
    public static class Address {
        private String addressLine1;
        private String country;
        private String postalCode;
    }

    @Getter
    @Setter
    public static class Name {
        private String firstName;
        private String fullName;
        private String lastName;
        private String title;
    }

    @Getter
    @Setter
    public static class Message{
        private String code;
        private String message;

    }
}
