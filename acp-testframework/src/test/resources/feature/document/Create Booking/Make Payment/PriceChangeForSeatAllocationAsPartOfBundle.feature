@manual
@FCPH-8439
Feature: Price change for seat allocation as part of bundle for commit booking for Digital & Public API mobile

  Scenario Outline: Allocate seat for the new price as part of bundle
    Given I have a basket with a valid flight added via <channel>
    When I receive new prices from seating service
    Then seat will allocate for the new price
    And continue with commit booking

    Examples:
      | channel         |
      | Digital         |
      | PublicApiMobile |


  Scenario Outline: Generate an error code when the price change for higher seat band
    Given I have a basket with a valid flight added via <channel>
    When I receive new prices from seating service for the higher band
    Then calculate the price difference and update the basket
    And deallocate the inventory
    And will fail the commit booking process
    And receive an error code <errorCode>

    Examples:
      | channel         | errorCode       |
      | Digital         | SVC_100022_3056 |
      | PublicApiMobile | SVC_100022_3056 |

