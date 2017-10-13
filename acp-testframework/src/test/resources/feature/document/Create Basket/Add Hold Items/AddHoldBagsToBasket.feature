Feature: Add Hold Items to Basket
#Remove tag regression due to stock level we can not run this test on CI or SYSTEST therefore to be run locally only
#We don't have hold item products associated to AD and Public API channel so we will get compromised exception for all other channel than Digital

  @FCPH-3512
  Scenario Outline: Receive incorrect request to add hold items to basket
    Given I have added the flight with passengers as "1 Adult" and from the channel "<Channel>" to the basket
    And I have received a valid addHoldBagProduct request for the channel "<Channel>"
    But request miss the mandatory field "<Field>" for Holdbag defined in the service contract
    When I validate the request addHoldBagProduct
    Then I will return a message "<Error>" to the channel for addHoldBagProduct
    Examples:
      | Channel | Field       | Error           |
      | Digital | productCode | SVC_100000_3036 |
    Examples:
      | Channel           | Field       | Error           |
      | ADAirport         | productCode | SVC_100000_3036 |
      | ADCustomerService | basketCode  | SVC_100013_1001 |
      | PublicApiMobile   | productCode | SVC_100000_3036 |
#      | PublicApiB2B      | productCode | SVC_100000_3036 |

  Scenario Outline: Excess weight add without hold bag BR_01471
    Given I have added the flight with passengers as "1 Adult" and from the channel "<Channel>" to the basket
    And I have not received addHoldBagProduct request for the channel "<Channel>"
    When I validate the request addHoldBagProduct
    Then I will return a message "<Error>" to the channel for addHoldBagProduct
    Examples:
      | Channel | Error           |
      | Digital | SVC_100000_3036 |
    Examples:
      | Channel           | Error           |
      | ADCustomerService | SVC_100000_3036 |
#      | ADAirport         | SVC_100012_3017,SVC_100288_2001 |
#      | PublicApiB2B      | SVC_100012_3017,SVC_100288_2001 |

  Scenario Outline: Return error if we try to add multiple hold items with excess weight
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    And I have received a valid addHoldBagProduct request for the channel "<Channel>"
    When I validate the request addHoldBagProduct
#    When I request to add product as "Hold Bag" with excess weight quantity as "<excessWeightQuantity>" to all flights for specific passenger
    Then I will return a message "<Error>" to the channel for addHoldBagProduct
    Examples:
      | Channel | Mix     | quantity | product        | fareType | journeyType | excessWeightQuantity | Error           |
      | Digital | 2 adult | 2        | HoldBagProduct | Standard | single      | 2                    | SVC_100000_3038 |

  @FCPH-8218
  Scenario Outline: Maximum hold items by the passenger and channel for adding hold item to all passengers all flight BR_00075
    Given I am using the channel <Channel>
    And I have the threshold set for "Hold Bag" and the passenger mix "<Mix>" for "<Channel>"
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I  add "Hold Bag" for all passengers on flight until I reach the threshold
    Then I will return an warning "<Warning>" to the channel
    And I clear the basket
  @Sprint24
    Examples:
      | Channel         | Mix                   | quantity | product        | fareType | journeyType | Warning         |
      | Digital         | 1 adult               | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | PublicApiMobile | 1 child               | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | Digital         | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
    Examples:
      | Channel         | Mix                  | quantity | product        | fareType | journeyType | Warning         |
      | PublicApiB2B    | 1 adult              | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | PublicApiMobile | 1 adult, 1 infant OL | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | PublicApiB2B    | 1 adult, 1 infant OL | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |


#  Following scenario is a duplicate of the above scenario, separated because it need to run as part of the Sprint27, 414 story
  @Sprint27
  @FCPH-414
  Scenario Outline: Maximum hold items by the passenger and channel for adding hold item to all passengers all flight BR_00075
    Given I am using the channel <Channel>
    And I have the threshold set for "Hold Bag" and the passenger mix "<Mix>" for "<Channel>"
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I  add "Hold Bag" for all passengers on flight until I reach the threshold
    Then I will return an warning "<Warning>" to the channel
    And I clear the basket
    Examples:
      | Channel         | Mix                   | quantity | product        | fareType | journeyType | Warning         |
      | Digital         | 1 adult               | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | Digital         | 1 child               | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | PublicApiMobile | 1 adult, 1 infant OL  | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | PublicApiB2B    | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |


  @FCPH-8218
  Scenario Outline: Maximum hold items by the passenger and channel for adding hold item to all passengers for specific with flight with override as true BR_00075
    Given I am using the channel <Channel>
    And I have the threshold set for "Hold Bag" and the passenger mix "<Mix>" for "<Channel>"
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I  add "Hold Bag" for all passengers on specific flight until I reach the threshold with override as "true"
    Then I will return an warning "<Warning>" to the channel
    And I clear the basket
    Examples:
      | Channel | Mix     | quantity | product        | fareType | journeyType | Warning         |
      | Digital | 1 adult | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
#      | Digital         | 1 child               | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
#      | PublicApiMobile | 1 adult               | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
#      | PublicApiMobile | 1 adult, 1 infant OL  | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
#      | PublicApiB2B    | 1 child               | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
#      | PublicApiB2B    | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |

  @FCPH-8218
  Scenario Outline: Maximum hold items by the passenger and channel for specific passenger on specific flight BR_00075
    Given I am using the channel <Channel>
    And I have the threshold set for "Hold Bag" and the passenger mix "<Mix>" for "<Channel>"
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I  add "Hold Bag" until I reach the threshold
    Then I will return an error "<Error>" to the channel
    And I clear the basket
  @local
    Examples:
      | Channel         | Mix                  | quantity | product        | fareType | journeyType | Error           |
      | PublicApiMobile | 1 adult, 1 infant OL | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | Digital         | 1 adult              | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | Digital         | 1 child              | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
    Examples:
      | Channel         | Mix                   | quantity | product        | fareType | journeyType | Error           |
      | PublicApiMobile | 1 adult, 1 child      | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |
      | PublicApiB2B    | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      | SVC_100288_2005 |

  @FCPH-3512
  Scenario Outline: Add Hold Bag product to the basket for specific flight specific passenger
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Hold Bag" to specific passenger on specific flight
    Then I should add the hold bag product to specific passengers for specific flight
    And add the price of the hold items and update the basket total
    Examples:
      | Channel | Mix     | quantity | product        | fareType | journeyType |
      | Digital | 2 adult | 1        | HoldBagProduct | Standard | single      |
#      | Digital | 1 adult, 1 infant OL | 1        | HoldBagProduct | Standard | single      |
#      | Digital | 1 child              | 1        | HoldBagProduct | Standard | single      |

  @FCPH-8218 @FCPH-3512
  Scenario Outline: Add hold item to the basket if linked to bundle or product
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    Then I will add the hold bag product by default and create an order line for all the passengers
    Examples:
      | Channel | Mix     | quantity | product        | fareType | journeyType |
      | Digital | 2 adult | 2        | HoldBagProduct | Flexi    | single      |

  @FCPH-8218 @FCPH-3512
  Scenario Outline: No Allocation of inventory for Digital BR_00950, BR_00951
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Hold Bag" to specific passenger on specific flight
    Then I will verify the stock level is the same for holdbag
  @local
    Examples:
      | Channel | Mix     | quantity | product        | fareType | journeyType |
      | Digital | 1 adult | 2        | HoldBagProduct | Standard | single      |

  @FCPH-8218
  Scenario Outline: Add Hold Bag product to all passengers on specific flight
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Hold Bag" to all passenger with quantity <quantity>
    Then I should add the hold bag product to all the passengers for specific flight
    And add the price of the hold items and update the basket total
    Examples:
      | Channel           | Mix                   | quantity | product        | fareType | journeyType |
      | Digital           | 2 adult               | 2        | HoldBagProduct | Standard | single      |
      | ADAirport         | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      |
      | PublicApiMobile   | 1 child               | 3        | HoldBagProduct | Standard | single      |
      | ADCustomerService | 2 adult               | 1        | HoldBagProduct | Standard | single      |

  @FCPH-8218
  Scenario Outline: Add Hold Bag product along with excess weight to all passengers on specific flight
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Hold Bag" with excess weight quantity as "<excessWeightQuantity>" to all passenger
    Then I should add the "Hold Bag" product to all the passengers for requested flight
    And  the quantity of hold bag items added should be same as requested quantity
    And I will add excess weight to each passenger
    And add the price of the hold items and update the basket total
    Examples:
      | Channel   | Mix     | quantity | product        | fareType | journeyType | excessWeightQuantity |
      | Digital   | 2 adult | 1        | HoldBagProduct | Standard | single      | 1                    |
      | ADAirport | 1 child | 1        | HoldBagProduct | Standard | single      | 3                    |
    Examples:
      | Channel           | Mix                   | quantity | product        | fareType | journeyType | excessWeightQuantity |
      | PublicApiMobile   | 1 child               | 1        | HoldBagProduct | Standard | single      | 1                    |
      | ADCustomerService | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      | 3                    |

  @FCPH-8218
  Scenario Outline: Add Hold Bag product to all flights for specific passenger
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Hold Bag" to all flights for specific passenger
    Then I will add the "Hold bag" product to specific passenger on all flights
    And add the price of the hold items and update the basket total
    Examples:
      | Channel         | Mix     | quantity | product        | fareType | journeyType |
      | Digital         | 2 adult | 1        | HoldBagProduct | Standard | single      |
      | PublicApiMobile | 1 child | 1        | HoldBagProduct | Standard | single      |
    Examples:
      | Channel           | Mix                   | quantity | product        | fareType | journeyType |
      | ADAirport         | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      |
      | ADCustomerService | 1 adult, 1 infant OL  | 1        | HoldBagProduct | Standard | single      |

  @FCPH-8218
  Scenario Outline: Add Hold Bag product with excess weight to all flights for specific passenger
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I request to add product as "Hold Bag" with excess weight quantity as "<excessWeightQuantity>" to all flights for specific passenger
    Then I will add the "Hold Bag" product to specific passenger on all flights
    And  the quantity of hold bag items added should be same as requested quantity
    And I will add excess weight to all flights for specific passenger
    And add the price of the hold items and update the basket total
    Examples:
      | Channel         | Mix     | quantity | product        | fareType | journeyType | excessWeightQuantity |
      | Digital         | 2 adult | 1        | HoldBagProduct | Standard | single      | 1                    |
      | PublicApiMobile | 1 child | 1        | HoldBagProduct | Standard | single      | 3                    |
    Examples:
      | Channel           | Mix                   | quantity | product        | fareType | journeyType | excessWeightQuantity |
      | ADAirport         | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      | 3                    |
      | ADCustomerService | 1 adult, 1 infant OOS | 1        | HoldBagProduct | Standard | single      | 1                    |

  @FCPH-8218
  Scenario Outline: Add Hold Bag product to all passengers on all flights
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 2 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Hold Bag" to all passenger on all flights
    Then I should add the "Hold bag" product to all the passengers for all flights
    And add the price of the hold items and update the basket total
  @Sprint24
    Examples:
      | Channel         | Mix     | quantity | product        | fareType | journeyType |
      | Digital         | 2 adult | 1        | HoldBagProduct | Standard | single      |
      | PublicApiMobile | 1 child | 1        | HoldBagProduct | Standard | single      |
    Examples:
      | Channel   | Mix                  | quantity | product        | fareType | journeyType |
      | ADAirport | 1 adult, 1 infant OL | 1        | HoldBagProduct | Standard | single      |

  @FCPH-8218
  Scenario Outline: Add Hold Bag product with excess weight to all passengers on all flights
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 2 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Hold Bag" with "<excessWeightQuantity>" excess weight to all passenger on all flights
    Then I should add the "Hold bag" product to all the passengers for all flights
    And I will add excess weight to all flights for specific passenger
    Examples:
      | Channel         | Mix                  | quantity | product        | fareType | journeyType | excessWeightQuantity |
      | Digital         | 2 adult              | 1        | HoldBagProduct | Standard | single      | 3                    |
      | PublicApiMobile | 1 child              | 1        | HoldBagProduct | Standard | single      | 1                    |
      | PublicApiB2B    | 2 adult              | 1        | HoldBagProduct | Standard | single      | 2                    |
      | ADAirport       | 1 adult, 1 infant OL | 1        | HoldBagProduct | Standard | single      | 3                    |

  @manual
  @FCPH-8218 @FCPH-3512
  Scenario: Inventory exceeds the capped threshold BR_00075
    Given I have added a flight to the basket
    And the requesting channel is Agent Desktop
    But the inventory exceeds the cap of the flight
    When I add product as 'Hold Bag'
    Then I should get SVC_100290_2007 error
    And a override flag based if the channel is allowed to override the message

  @FCPH-3512
  @pending
  Scenario Outline: Return updated basket to the channel BR_01340
    Given I have added the flight with passengers as "1 Adult" and from the channel "<Channel>" to the basket
    And I have received a valid addHoldBagProduct request for the channel "<Channel>"
    Then I will return the price including credit card fee for the channel "<Channel>"
    Examples:
      | Channel           |
      | Digital           |
      | ADCustomerService |

  @manual
  @FCPH-8218
  Scenario: Add product to sector which are not allowed for the bundles
    Given the "Hold Bag" has a bundle restriction set against the product
    When I add product as 'Hold Bag'
    And the basket contains a bundle which is not allowed with the product
    And I will return a message to the channel unable to add due to bundle restrictions
