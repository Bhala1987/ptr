Feature: Validate retrieve customer

  @TeamD
  @Sprint31
  @FCPH-11264 @regression
  Scenario: Generate a error message if the customer is not hard logged in - getCustomerprofile
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to getCustomerProfile service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint31
  @FCPH-11264
  Scenario: Generate a error message if the customer is not hard logged in - updateCustomerProfile
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to updateCustomerProfile service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - getSignificantOthers
    Given one of this channel Digital, PublicApiMobile is used
    And a customer with significant others exist in the database
    When I send the request to getSignificantOthers service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - updateSignificantOther
    Given one of this channel Digital, PublicApiMobile is used
    And a customer with significant others exist in the database
    When I send the request to updateSignificantOthers service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - getDependents
    Given one of this channel Digital, PublicApiMobile is used
    And a customer with dependents exist in the database
    When I send the request to getDependents service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario Outline: Generate a error message if the customer is not hard logged in - updateDependent
    Given one of this channel Digital, PublicApiMobile is used
    And a customer with dependents exist in the database
    When I send the request to updateDependents service to <service>
    Then the channel will receive an error with code SVC_100000_2089
    Examples:
      | service                 |
      | update ejPlusCardNumber |
      | add identityDocuments   |
      | update savedSSRs        |

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - getSavedPassengers
    Given one of this channel Digital, PublicApiMobile is used
    And a customer with saved passenger exist in the database
    When I send the request to getSavedPassengers service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - addSavedPassenger
    Given one of this channel Digital, PublicApiMobile is used
    And a customer with saved passenger exist in the database
    When I send the request to addSavedPassengers service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - getAdvancedPassengerInformation
    Given one of this channel Digital, PublicApiMobile is used
    And a customer with APIs exist in the database
    When I send the request to getAdvancedPassengerInformation service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - setAPIS
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to setAPIs service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - updateSpecialServicesRequest
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to updateSpecialServiceRequests service
    Then the channel will receive an error with code SVC_100000_2089

#  @TeamD
#  @Sprint30
#  @FCPH-9980
#  getPreferences service will be implemented in a future story
  Scenario: Generate a error message if the customer is not hard logged in - getPreferences
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to getPreferences service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - updatePreferences
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to updatePreferences service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - getPaymentDetails
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to getPaymentDetails service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9980
  Scenario: Generate a error message if the customer is not hard logged in - managePaymentDetails
    Given one of this channel Digital, PublicApiMobile is used
    And a customer exist in the database
    When I send the request to managePaymentDetails service
    Then the channel will receive an error with code SVC_100000_2089