Feature: ACP Retrieve Flights and Prices Requirements

  @TeamD
  @Sprint31
  @FCPH-10768
  @BR:BR_01700
  Scenario: Return an error if Channel not allowed to make a group booking
    Given one of this channel Digital, PublicApiMobile, PublicApiB2B is used
    And I want to do a group booking
    When I send the request to getFlights service
    Then the channel will receive an error with code SVC_100148_3037

  @TeamD
  @Sprint31
  @FCPH-10768
  Scenario Outline: Generate Error Where fare type set For Channel
    Given one of this channel ADAirport, ADCustomerService is used
    And I want to do a group booking with <fareType> fare
    When I send the request to getFlights service
    Then the channel will receive an error with code SVC_100148_3038
    Examples:
      | fareType |
      | Standard |
      | Flexi    |

  @TeamD
  @Sprint31
  @FCPH-10768
  @BR:BR_01710 @regression @defect:FCPH-12064
  Scenario: Calculate Group Booking Offer Price
    Given one of this channel ADAirport, ADCustomerService is used
    And I want to do a group booking
    When I sent the request to getFlights service
    Then list of available flight for group booking is returned
