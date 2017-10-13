@FCPH-8378
Feature: Add excess weight to an existing hold item for a passenger on a flight

  Scenario Outline: Receive request to excess weight to a flight for a specific passenger
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    And I have received the request to add "2" products as "Hold Bag" to all passenger on all flights
    When I received request to add "<excessWtQuantity>" "Excess Weight" to "<holdItem>" hold bag of specific passenger on specific flight
    Then I should add the Excess Weight product to requested <holdItem> Hold Bag
    And I clear the basket
    Examples:
      | Channel | Mix                  | quantity | product        | holdItem | fareType | journeyType | excessWtQuantity |
      | Digital | 2 adult              | 1        | HoldBagProduct | 1'st     | Standard | single      | 2                |
      | Digital | 1 child              | 1        | HoldBagProduct | 1'st     | Standard | single      | 2                |
      | Digital | 1 adult ; 1,0 infant | 1        | HoldBagProduct | 2'nd     | Standard | single      | 1                |

  Scenario Outline: Receive error if we try to add excess weight without hold items BR_01471
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I received request to add "Excess Weight" without hold bag
    Then I will return a error "<Error>" to the channel for addHoldBagProduct
    And I clear the basket
    Examples:
      | Channel | Mix     | quantity | product        | fareType | journeyType | Error           |
      | Digital | 2 adult | 1        | HoldBagProduct | Standard | single      | SVC_100288_2015 |

  Scenario Outline: Receive error if we try to add excess weight to invalid hold item
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    And I have received the request to add "1" products as "Hold Bag" to all passenger on all flights
    When I received request to add "Excess Weight" to invalid hold item
    Then I will return a error "<Error>" to the channel for addHoldBagProduct
    And I clear the basket
    Examples:
      | Channel | Mix     | quantity | product        | fareType | journeyType | Error           |
      | Digital | 2 adult | 1        | HoldBagProduct | Standard | single      | SVC_100288_2016 |

  Scenario Outline: Generate Error message if passengers hold bag is over 32 KG
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<holdItemProduct>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    And I have added "1" products as "Hold Bag" to all passenger on all flights
    When I receive request to add "Excess Weight" more than 32 kg to "<holdItem>" hold bag of specific passenger on specific flight
    Then I will return a error "<Error>" to the channel for addHoldBagProduct
    And I clear the basket
    Examples:
      | Channel | Mix     | quantity | holdItemProduct | holdItem | fareType | journeyType | Error           |
      | Digital | 2 adult | 1        | HoldBagProduct  | 1'st     | Standard | single      | SVC_100288_2014 |

  Scenario Outline: Basket total price is updated
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    And I have received the request to add "2" products as "Hold Bag" to all passenger on all flights
    When I received request to add "<excessWtQuantity>" "Excess Weight" to "<holdItem>" hold bag of specific passenger on specific flight
    Then I should add the Excess Weight product to requested <holdItem> Hold Bag
    And add the price of the hold items and update the basket total
    And I clear the basket
    Examples:
      | Channel           | Mix                  | quantity | product        | holdItem | fareType | journeyType | excessWtQuantity |
      | Digital           | 1 adult ; 1,0 infant | 1        | HoldBagProduct | 1'st     | Standard | single      | 1                |
      | Digital           | 2 adult              | 1        | HoldBagProduct | 1'st     | Standard | single      | 1                |
      | Digital           | 1 child              | 1        | HoldBagProduct | 1'st     | Standard | single      | 2                |
      | ADCustomerService | 1 child              | 1        | HoldBagProduct | 1'st     | Standard | single      | 2                |

  @regression
  Scenario Outline: Return updated basket to channel
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    And I have received the request to add "2" products as "Hold Bag" to all passenger on all flights
    When I received request to add "<excessWtQuantity>" "Excess Weight" to "<holdItem>" hold bag of specific passenger on specific flight
    Then I should add the Excess Weight product to requested "<holdItem>" Hold Bag
    And I should add the credit card fees based on channel "<Channel>"
    And I clear the basket
    Examples:
      | Channel           | Mix                  | quantity | product        | holdItem | fareType | journeyType | excessWtQuantity |
      | Digital           | 2 adult              | 1        | HoldBagProduct | 1'st     | Standard | single      | 1                |
      | ADCustomerService | 1 adult ; 1,0 infant | 1        | HoldBagProduct | 2'nd     | Standard | single      | 1                |
    Examples:
      | Channel   | Mix     | quantity | product        | holdItem | fareType | journeyType | excessWtQuantity |
      | ADAirport | 1 child | 1        | HoldBagProduct | 1'st     | Standard | single      | 2                |

  Scenario Outline: Warning if there is price change - flexi bundle should add excess weight to hold item is added as part of bundle
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    And I have received the request to add "1" products as "Hold Bag" to all passenger on all flights
    When I received request to add "Excess Weight" to "<holdItem>" hold bag with different price
    Then I will return an warning "<Warning>" to the channel
    And I clear the basket
    Examples:
      | Channel | Mix     | quantity | product        | holdItem | fareType | journeyType | Warning         |
      | Digital | 2 adult | 1        | HoldBagProduct | 1'st     | Standard | single      | SVC_100288_2017 |