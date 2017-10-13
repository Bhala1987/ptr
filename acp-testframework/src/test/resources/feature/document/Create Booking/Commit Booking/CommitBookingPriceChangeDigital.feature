@Sprint25
@FCPH-463
Feature:Flight Price difference as part of commit booking Digital and Public API Mobile

  Scenario Outline: Continue with the new price channel
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a flight for 1 adult
    And I added it to the basket with 'Standard' fare as 'single' journey
    And I have updated the passenger information
    And I have got the payment method as 'STANDARD_CUSTOMER'
    But The base price is changed with information code 'SVC_100012_3008'
    And I commit the booking
    And I should receive a confirmation message with code 'SVC_100022_3012'
    And I should receive the new price and I verify that the price of the basket is updated
    When I commit the booking
    Then I created a successful booking with reference number
    Examples:
      | channel         |
      | PublicApiMobile |

  Scenario Outline: Not continue with the new price
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a flight for 1 adult
    And I added it to the basket with 'Standard' fare as 'single' journey
    And I have updated the passenger information
    And I have got the payment method as 'STANDARD_CUSTOMER'
    But The base price is changed with information code 'SVC_100012_3008'
    And I commit the booking
    And I should receive a confirmation message with code 'SVC_100022_3012'
    And I should receive the new price and I verify that the price of the basket is updated
    When I send the request to removeFlight from basket
    Then I receive a confirmation response for the removeFlight()
    And the '<removed>' flight has been removed from the basket
    Examples:
      | channel         | removed  |
      | Digital         | outbound |
