Feature: Change Purchased Seat request

  @Sprint25 @FCPH-7644 @Sprint26 @FCPH-9121
  Scenario Outline: Validate a Change Purchased Seat request Digital and AD channel
    Given I am using channel <channel>
    And I want to proceed with add purchased seat UPFRONT
    And I am requesting to change a purchased seat for "<passenger-mix>" from "UPFRONT" to "UPFRONT" with fare type "Standard"
    But the request missing in the mandatory field "<field>"
    When I send the move seat product request
    Then I will generate the error message "<error>"
    Examples:
      | channel   | passenger-mix | field      | error           |
      | Digital   | 2 Adult       | seatNumber | SVC_100244_1004 |
      | ADAirport | 1 Adult       | basketId   | SVC_100015_1001 |

#    There is outstanding CR on Seating FCP-10270 where seating sends us duplicate id for Extra Legroom and Upfront Seat
#    Please  check FCP-10270 before removing the defect tag
#  @defect:FQT-1062
  @Sprint25 @FCPH-7644 @defect:FCP-10270
  Scenario Outline: Update basket Digital channel
    Given I am using channel <channel>
    And I want to proceed with add purchased seat <seat-from>
    And I am requesting to change a purchased seat for "1 Adult" from "<seat-from>" to "<seat-to>" with fare type "<fare-type>"
    When I send the move seat product request
    Then I will receive the successful response
    And I will verify the new seating configuration of passenger with applied discount "<discount-fare>"
    And I will verify any associated products with the old purchased seat has been removed
    And I will verify any associated products with the new purchased seat has been added with "<expected-product>" product
    And I will verify the new passenger totals price
    And I will verify the new flight totals price
    And I will verify the new basket totals
  @regression
    Examples:
      | channel | fare-type | seat-from | seat-to       | expected-product | discount-fare |
      | Digital | Standard  | STANDARD  | EXTRA_LEGROOM | 1                | false         |
    Examples:
      | channel | fare-type | seat-from     | seat-to       | expected-product | discount-fare |
      | Digital | Standard  | EXTRA_LEGROOM | STANDARD      | 0                | false         |
      | Digital | Flexi     | EXTRA_LEGROOM | STANDARD      | 0                | true          |
      | Digital | Flexi     | STANDARD      | EXTRA_LEGROOM | 0                | true          |

#    There is outstanding CR on Seating FCP-10270 where seating sends us duplicate id for Extra Legroom and Upfront Seat
#    Please  check FCP-10270 before removing the defect tag
#  @defect:FQT-1062
  @Sprint26 @FCPH-9121 @defect:FCP-10270
  Scenario Outline: Update basket AD channel
    Given I am using channel <channel>
    And I want to proceed with add purchased seat <seat-from>
    And I am requesting to change a purchased seat for "1 Adult" from "<seat-from>" to "<seat-to>" with fare type "<fare-type>"
    When I send the move seat product request
    Then I will receive the successful response
    And I will verify the new seating configuration of passenger with applied discount "<discount-fare>"
    And I will verify any associated products with the old purchased seat has been removed
    And I will verify any associated products with the new purchased seat has been added with "<expected-product>" product
    And I will verify the new passenger totals price
    And I will verify the new flight totals price
    And I will verify the new basket totals
  @regression
    Examples:
      | channel           | fare-type | seat-from     | seat-to       | expected-product | discount-fare |
      | ADCustomerService | Standard  | EXTRA_LEGROOM | EXTRA_LEGROOM | 1                | false         |
    Examples:
      | channel           | fare-type | seat-from     | seat-to       | expected-product | discount-fare |
      | ADAirport         | Standard  | EXTRA_LEGROOM | STANDARD      | 0                | false         |
      | ADAirport         | Flexi     | EXTRA_LEGROOM | STANDARD      | 0                | true          |
      | ADCustomerService | Flexi     | STANDARD      | EXTRA_LEGROOM | 0                | true          |
