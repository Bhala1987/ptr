Feature: Retrieve a customer's profile

  @FCPH-342 @FCPH-341
  @regression
  Scenario: Profile exists and is returned
    Given a customer profile exists
    When I search for the profile
    Then a profile is returned

  @FCPH-342 @FCPH-341
  Scenario: Profile does not exist
    Given a customer profile does not exist
    When I search for the profile
    Then a profile error is returned

  @manual
  @FCPH-486
  Scenario Outline: Advanced profile is returned
    Given a customer profile exists with a full set of Data
    When I search for the profile with the prefer header settled to "<preferValue>" ,X-POS-Id settled to "<channelValue>" and pathParameter sections settled to "<sectionsValue>"
    Then The advanced profile is returned accordingly to the prefer header settled to "<preferValue>" ,X-POS-Id settled to "<channelValue>" and pathParameter sections settled to "<sectionsValue>"
    Examples:
      | preferValue | sectionsValue | channelValue |
      | FULL        |               | Digital      |
      | PARTIAL     | dependents    | Digital      |
      | PARTIAL     | dependents    | ADAirport    |
      | BASIC       |               | Digital      |
      | BASIC       |               | ADAirport    |
      | FULL        |               | ADAirport    |

  @FCPH-7697
  Scenario Outline: Return the up to date customer profile
    Given I am using channel <Channel>
    And a valid customer profile has been created
    And I sent a request to UpdateCustomerDetail
    And I sent a request to SavedPassenger
    And I sent a request to SetAPI
    And I sent a request to UpdateSpecialRequest
    And I sent a request to CreateIdentityDocument
    And I sent a request to SearchFlight "<Channel>"
    And I sent a request to AddFlight "<Channel>"
    And I sent a request to CommitBooking "<Channel>"
    When I sent a request to GetCustomerProfile
    Then I will receive the updated values to the channel
    Examples:
      | Channel   |
      | ADAirport |

  @pending
  @FCPH-7697
  Scenario: Return saved passenger for the customer profile
    Given I create a Customer
    And I sent a request to SavedPassenger
    And I sent a request to UpdateSpecialRequest
    And I sent a request to CreateIdentityDocument
    When I sent a request to GetCustomerProfile
    Then I will receive the saved passenger for the customer to the channel

  @manual
  @FCPH-7697
  Scenario: Return EJ plus membership has expired BR_3000
    Given I check the expiry date of the ej plus memberhip
    And I did the login
    When I sent a request to GetCustomerProfile
    Then I will add additional information message in the response to the channel

  @manual
  @FCPH-7697
  Scenario: Return saved APIS from the profile BR_00146
    When I sent a request to GetCustomerProfile
    And the saved passenger, customer, has stored APIS
    Then I will return the APIS details which have been stored against in the customer profile for less than "x" months (x=16)

  @manual
  @FCPH-7697
  Scenario: Store the date the profile is returned to the channel
    When I sent a request to GetCustomerProfile
    Then I will record the date, time the profile was retrieved

  @Sprint26
  @FCPH-8770
  Scenario Outline: Retrieve customer profile with status to a channel
    Given a customer profile exists with status <Status>
    When I search a the profile using the channel <Channel>
    Then a profile is returned with the result as <Result>
    Examples:
      | Channel           | Status   | Result  |
      | ADAirport         | ACTIVE   | Success |
      | ADAirport         | DELETED  | Error   |
      | ADAirport         | ARCHIVED | Error   |
      | ADAirport         | RECYCLED | Error   |
      | ADCustomerService | DELETED  | Success |
      | ADCustomerService | ACTIVE   | Success |
      | ADCustomerService | ARCHIVED | Success |
      | ADCustomerService | RECYCLED | Success |
      | PublicApiB2B      | ACTIVE   | Success |
      | PublicApiB2B      | DELETED  | Error   |
      | PublicApiB2B      | ARCHIVED | Error   |
      | PublicApiB2B      | RECYCLED | Error   |
      | PublicApiMobile   | ACTIVE   | Success |
      | PublicApiMobile   | DELETED  | Error   |
      | PublicApiMobile   | ARCHIVED | Error   |
      | PublicApiMobile   | RECYCLED | Error   |
      | Digital           | ACTIVE   | Success |
      | Digital           | DELETED  | Error   |
      | Digital           | ARCHIVED | Error   |
      | Digital           | RECYCLED | Error   |

