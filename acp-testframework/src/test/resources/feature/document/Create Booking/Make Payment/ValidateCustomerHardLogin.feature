Feature: Validate if the customer is hard logged in when making a booking

  @TeamD
  @Sprint30
  @FCPH-9981
  Scenario: Generate a error message if the customer is not hard logged in - getPaymentMethodsForChannel
    Given one of this channel Digital, PublicApiMobile is used
    And I added a flight to the basket
    When I send the request to getPaymentMethodsForChannel service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9981
  Scenario: Generate a error message if the customer is not hard logged in - commitBooking
    Given one of this channel Digital, PublicApiMobile is used
    And I added a flight to the basket
    And I have a valid payment method
    And I updated the passenger information
    When I send the request to commitBooking service
    Then the channel will receive an error with code SVC_100000_2089

  @TeamD
  @Sprint30
  @FCPH-9981
  Scenario: Generate a error message if the customer is not hard logged in - getBooking
    Given one of this channel Digital, PublicApiMobile is used
    And I have committed a booking
    And I send a request to customer logout service
    When I send the request to getBooking service
    Then the channel will receive an error with code SVC_100000_2089