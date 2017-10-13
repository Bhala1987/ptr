@FCPH-7915
Feature: Check T's and C's for SSR

  Scenario: Validate SSR saved if T&C parameter exists if it is mandatory for updateSavedPassenger service
    Given I have added a passenger to an existing customer
    And I am updating SSRs that are mandatory to provide the Ts&Cs parameter
    When I process the request for savedSSR with Ts&Cs parameter
    Then I will store the SSR data details
    And I will verify the successfully response

  Scenario: Validate error if mandatory T&C parameter does not exists for updateSavedPassenger service
    Given I have added a passenger to an existing customer
    When I don’t include the mandatory Ts and Cs parameter while updating as SSR
    And I process the request for savedSSR with Ts&Cs parameter
    Then I will return an "SVC_100012_3032" error

  Scenario: Validate T&C parameter ignored if SSRs with T&C are not mandatory for updateSavedPassenger service
    Given I have added a passenger to an existing customer
    And I am updating SSRs that are not mandatory to provide the Ts&Cs parameter
    When I process the request for savedSSR with Ts&Cs parameter
    Then I will store the SSR data details
    And I will verify the successfully response

  Scenario: Validate SSR saved if T&C parameter exists if it is mandatory for updatePassenger service in basket
    Given I am updating SSRs for a passenger in basket
    When I include the mandatory Ts and Cs parameter for SSRs I am updating
    Then the passenger details should be updated with the SSR

  Scenario: Validate error if mandatory T&C parameter does not exists for updatePassenger service in basket
    Given I am updating SSRs for a passenger in basket
    When I do not include the mandatory Ts and Cs parameter for SSRs I am updating
    Then I should receive the "SVC_100012_3032" error message