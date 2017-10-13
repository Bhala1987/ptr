@Sprint28 @FCPH-3959
Feature: Initiate the cancellation of a full booking.

  As a passenger on a flight
  I want to be able to cancel my full booking
  So that I can reflect a change in my plans and be refunded appropriate funds

  Scenario: Error when an invalid booking reference is used.
    Given I am using Digital channel
    And a customer account exists with a known password
    When I request to cancel my booking using an invalid booking reference.
    Then I will receive an error with code 'SVC_100021_1003'

  Scenario: Error when the passenger is not logged in.
    Given I am using Digital channel
    When I request to cancel my booking using an invalid booking reference.
    Then I will receive an error with code 'SVC_100021_1004'

  Scenario: Error when the channel is unable to cancel the booking
    Given ADAirport do the commit booking with "2 Adult"
    And I am using PublicApiMobile channel
    When I request to cancel my booking
    Then I will receive an error with code 'SVC_100021_1001'

  Scenario: Error when the bundle is flexi and the channel is not permitted to cancel the booking
    Given I am using Digital channel
    And I do a commit booking with 1 for 1 adult with true APIS using Flexi
    And a customer account exists with a known password
    When I request to cancel my booking
    Then I will receive an error with code 'SVC_100021_1002'

  Scenario Outline: Refund amount is calculated correctly for the given payment method when cancelling within 24hrs
    Given that I have added a flight "ALC" to "LTN" to the basket with currency GBP from Digital
    And a customer account exists with a known password
    And I do commit booking with card and <Payment Details>
    And I will generate a make payment request to the payment service
    When I request to cancel my booking
    Then the refund amount should be calculated appropriately for the given payment details and booking age
    And the response should contain Primary Reason Code "CC24H" and Primary Reason Name "Customer Cancellation 24 hours"
  @regression
    Examples:
      | Payment Details                             |
      | DM-5573471234567898-123-8-2018-Testing card |
    Examples:
      | Payment Details                             |
      | MC-5573471234567898-123-8-2018-Testing card |

  # Depends on FCPH-9158.
  @manual
  Scenario: Error when the booking has already been canceled
    Given Digital do the commit booking with "2 Adult"
    And I login with valid credentials
    And I request to cancel my booking
    And my booking is cancelled
    When I request to cancel my booking
    Then I will receive an error with code 'SVC_0_0'

  @manual
  Scenario: Zero value refund when the booking is cancelled after 24hrs
    Given I have made a booking
    And the booking date is > 24hrs in the past
    When I attempt to cancel that booking
    Then the refund amount returned should be 0
    And the response should contain Primary Reason Code "CCP24H" and Primary Reason Name "Customer Cancellation Post 24 hours"
