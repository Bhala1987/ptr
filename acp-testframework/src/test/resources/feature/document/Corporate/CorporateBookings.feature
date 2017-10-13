Feature: Corporate Bookings

  @TeamD
  @Sprint31
  @FCPH-10687
  Scenario: Set the booking type to standard customer if no deal is found for Add flight
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And a deal with invalid application id, invalid office id and no corporate id is defined
    When I added a flight to the basket
    Then the basket booking type is STANDARD_CUSTOMER

  @TeamD
  @Sprint31
  @FCPH-10687
  Scenario: Set the booking type to standard customer if no deal is found for commit booking
    Given a channel is used
    And a deal with invalid application id, invalid office id and no corporate id is defined
    When I have committed a booking
    Then the booking booking type is STANDARD_CUSTOMER

  @TeamD
  @Sprint31
  @FCPH-10687
  Scenario: Set Booking type based on deal applied
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And a deal with valid application id, valid office id and valid corporate id is defined
    When I added a flight to the basket
    Then the basket booking type is based on deal

  @TeamD
  @Sprint31
  @FCPH-10687
  Scenario: Set Booking type based on deal applied for commit Booking
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And a deal with valid application id, valid office id and valid corporate id is defined
    When I have committed a booking
    Then the booking booking type is based on deal