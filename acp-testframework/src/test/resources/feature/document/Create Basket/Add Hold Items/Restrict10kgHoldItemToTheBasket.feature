@Sprint24 @FCPH-8449 @Sprint25
Feature: Restrict 10 KG hold item to the basket

  Scenario Outline: Error message if channel is not allowed to purchase the product
    Given I have an hold bag product with a restriction for a channel
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    When I try to add that product with addHoldBagProduct
    Then I will return a message "<error>" to the channel for addHoldBagProduct
    Examples:
      | passenger        | journey          | fareType | error           |
      | 1 adult; 1 child | outbound/inbound | Standard | SVC_100288_2018 |

