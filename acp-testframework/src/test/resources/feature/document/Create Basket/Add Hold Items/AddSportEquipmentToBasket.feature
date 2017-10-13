Feature: Add Sports Equipment to Basket
#Remove tag regression due to stock level we can not run this test on CI or SYSTEST therefore to be run locally only

  @FCPH-7704
  Scenario Outline: Receive request to add sports equipment
    Given I have a valid flight in my basket for the channel "<Channel>"
    And I have received a valid addSportsEquipment request for the channel "<Channel>"
    But request miss the mandatory field "<Field>" defined in the service contract
    When I valid the request to addSportsEquipment
    Then I will return a message "<Error>" to the channel
  @local
    Examples:
      | Channel | Field       | Error           |
      | Digital | productCode | SVC_100000_3036 |
    Examples:
      | Channel           | Field       | Error           |
      | ADAirport         | productCode | SVC_100000_3036 |
      | ADCustomerService | basketCode  | SVC_100013_1001 |
      | PublicApiMobile   | productCode | SVC_100000_3036 |
      | PublicApiB2B      | basketCode  | SVC_100013_1001 |

  @FCPH-7704
  Scenario Outline: Maximum sports equipment items by the passenger and channel for specific passenger on specific flight BR_00075
    Given I am using the channel <Channel>
    And I have the threshold set for "Large Sporting Equipment" and the passenger mix "<Mix>" for "<Channel>"
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I  add "Large Sporting Equipment" until I reach the threshold
    Then I will return an error "<Error>" to the channel
    Examples:
      | Channel         | Mix                   | quantity | product            | fareType | journeyType | Error           |
      | PublicApiMobile | 1 adult, 1 infant OL  | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |
      | Digital         | 1 adult               | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |
      | PublicApiMobile | 1 adult, 1 child      | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |
      | PublicApiB2B    | 1 adult, 1 infant OOS | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |

  @FCPH-7704
  Scenario Outline: Maximum sports equipment items by the flight and channel (already exist something in the basket) BR_00075
    Given I have a valid flight in my basket for the channel "<Channel>" with passenger mix "<Mix>"
    And I have received a valid request for item "<Item>" for the channel "<Channel>"
    And request contains a valid threshold set for the item "<Item>" and the passenger mix "<Mix>"
    And I valid the request to addSportsEquipment
    But I have received a valid request for item "<Item>" for the channel "<Channel>"
    And request contains a valid threshold set for the item "<Item>" and the passenger mix "<Mix>"
    When I valid the request to addSportsEquipment
    Then I will return a message "<Error>" to the channel
    And the sport item will not be added to the basket
  @local
    Examples:
      | Item                     | Mix                   | Channel | Error           |
      | Large Sporting Equipment | 1 adult               | Digital | SVC_100290_2005 |
      | Large Sporting Equipment | 1 child               | Digital | SVC_100290_2005 |
      | Large Sporting Equipment | 1 adult, 1 infant OOS | Digital | SVC_100290_2005 |
    Examples:
      | Item                     | Mix                  | Channel         | Error           |
      | Large Sporting Equipment | 1 adult, 1 infant OL | Digital         | SVC_100290_2005 |
      | Large Sporting Equipment | 1 adult              | PublicApiMobile | SVC_100290_2005 |
      | Large Sporting Equipment | 1 child              | Digital         | SVC_100290_2005 |
      | Small Sporting Equipment | 1 adult, 1 infant OL | PublicApiB2B    | SVC_100290_2005 |

  @FCPH-7704
  Scenario Outline: Add product to the basket
    Given I have a valid flight in my basket for the channel "<Channel>" with currency "<Currency>"
    And I have received a valid addSportsEquipment request for the channel "<Channel>"
    When I valid the request to addSportsEquipment
    Then I will create a order line for each sector and passenger
    And add the price of the hold item to the basket total
    And update the total price of the basket
  @local
    Examples:
      | Channel         | Currency |
      | Digital         | GBP      |
      | PublicApiMobile | EUR      |
    Examples:
      | Channel           | Currency |
      | ADAirport         | EUR      |
      | ADCustomerService | GBP      |
      | PublicApiB2B      | EUR      |

  @FCPH-7704
  Scenario Outline: Allocate inventory for AD BR_00950, BR_00951
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Small Sporting Equipment" to specific passenger on specific flight
    Then I will decrement the stock level for the flight for the number of requested hold items
    And I clear the basket
  @local
    Examples:
      | Channel   | Mix     | quantity | product            | fareType | journeyType |
      | ADAirport | 1 adult | 1        | SmallSportsProduct | Standard | single      |

  @Sprint24 @FCPH-7704 @FCPH-8219
  Scenario Outline: Allocate inventory for AD BR_00950, BR_00951
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Small Sporting Equipment" to specific passenger on specific flight
    Then I will verify the stock level is the same for holdbag
    And I clear the basket
  @local
    Examples:
      | Channel | Mix     | quantity | product            | fareType | journeyType |
      | Digital | 2 adult | 1        | SmallSportsProduct | Standard | single      |

  @FCPH-7704
  @manual
  Scenario: Inventory exceeds the capped threshold BR_00075
    Given I have added a flight to the basket
    And the requesting channel is Agent Desktop
    But the inventory exceeds the cap of the flight
    When I add product as 'Sports Equipment'
    Then I should get SVC_100290_2007 error
    And a override flag based if the channel is allowed to override the message

  @FCPH-7704
  Scenario Outline: Return updated basket to the channel BR_01340
    Given I have a valid flight in my basket for the channel "<Channel>" with currency "<Currency>"
    When I have received a valid request to add product as "Small Sporting Equipment" to specific passenger on specific flight
    Then I will return the price including credit card fee depending on the channel
    Examples:
      | Channel      | Currency |
      | Digital      | GBP      |
      | ADAirport    | EUR      |
      | PublicApiB2B | GBP      |

  @Sprint24 @FCPH-8219
  Scenario Outline: Add Sports Equipment product to all passengers on specific flight and return updated basket to channel
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 2 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Large Sporting Equipment" to all passenger with quantity <quantity>
    Then I should add the "Sports Equipment" product to all the passengers for requested flight
    And add the price of the hold item and update the basket total
    And I clear the basket
    Examples:
      | Channel           | Mix                  | quantity | product            | fareType | journeyType |
      | Digital           | 2 adult              | 1        | LargeSportsProduct | Standard | single      |
      | ADAirport         | 2 adult              | 1        | LargeSportsProduct | Standard | single      |
      | ADAirport         | 1 adult, 1 infant OL | 1        | LargeSportsProduct | Standard | single      |
      | ADCustomerService | 1 adult, 1 child     | 1        | LargeSportsProduct | Standard | single      |

  @Sprint24 @FCPH-8219
  Scenario Outline: Add Sports Equipment to all flights for specific passenger
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Large Sporting Equipment" to all flights for specific passenger
    Then I will add the "Sports Equipment" product to specific passenger on all flights
    And add the price of the hold item and update the basket total
    And I clear the basket
    Examples:
      | Channel | Mix     | quantity | product            | fareType | journeyType |
      | Digital | 2 adult | 1        | LargeSportsProduct | Standard | single      |

  @Sprint24 @FCPH-8219
  Scenario Outline: Verify that updated basket is return to channel
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Large Sporting Equipment" to all flights for specific passenger
    Then I will add the "Sports Equipment" product to specific passenger on all flights
    And add the price of the hold item and update the basket total
    And I clear the basket
    Examples:
      | Channel | Mix     | quantity | product            | fareType | journeyType |
      | Digital | 2 adult | 1        | LargeSportsProduct | Standard | single      |

  @Sprint24 @FCPH-8219
  Scenario Outline: Add Sports Equipment product to all passengers on all flights
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Large Sporting Equipment" to all passenger on all flights
    Then I should add the "Sports Bag" product to all the passengers for all flights
    And I clear the basket
    Examples:
      | Channel         | Mix                   | quantity | product            | fareType | journeyType |
      | ADAirport       | 2 adult               | 1        | LargeSportsProduct | Standard | single      |
      | PublicApiB2B    | 1 adult, 1 infant OOS | 1        | LargeSportsProduct | Standard | single      |
      | PublicApiMobile | 1 child               | 1        | LargeSportsProduct | Standard | single      |

  @Sprint24 @FCPH-8219
  Scenario Outline: Maximum sports bag by the passenger and channel for adding sports item to all passengers all flight BR_00075
    Given I am using the channel <Channel>
    And I have the threshold set for "Large Sporting Equipment" and the passenger mix "<Mix>" for "<Channel>"
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I  add "Large Sporting Equipment" for all passengers on flight until I reach the threshold
    Then I will return an warning "<Warning>" to the channel
    And I clear the basket
    Examples:
      | Channel         | Mix                  | quantity | product            | fareType | journeyType | Warning         |
      | Digital         | 1 adult, 1 infant OL | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |
      | PublicApiMobile | 1 adult              | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |
      | PublicApiB2B    | 1 child              | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |

  @Sprint24 @FCPH-8219
  Scenario Outline: Maximum sports bag items by the passenger and channel for adding hold item to all passengers for specific  flight with override as true BR_00075
    Given I am using the channel <Channel>
    And I have the threshold set for "Large Sporting Equipment" and the passenger mix "<Mix>" for "<Channel>"
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I  add "Large Sporting Equipment" for all passengers on specific flight until I reach the threshold with override as "true"
    Then I will return an warning "<Warning>" to the channel
    And I clear the basket
    Examples:
      | Channel         | Mix                   | quantity | product            | fareType | journeyType | Warning         |
      | Digital         | 1 adult               | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |
      | PublicApiB2B    | 1 adult, 1 infant OOS | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |
      | PublicApiMobile | 1 adult, 1 infant OL  | 1        | LargeSportsProduct | Standard | single      | SVC_100290_2005 |

  @Sprint24 @FCPH-8219 @local
  Scenario Outline: Allocate inventory for AD BR_00950, BR_00951
    Given I am using the channel <Channel>
    And I searched a flight for '<Mix>' with stock level for '<quantity>' '<product>'
    And I added 1 flights to basket with passenger mix "<Mix>",'<fareType>' and '<journeyType>'
    When I have received a valid request to add product as "Large Sporting Equipment" to all passenger on all flights
    Then I should add the "Large Sporting Equipment" product to all the passengers for all flights
    Then I will decrement the stock level for the flight for the number of requested hold items
    Examples:
      | Channel   | Mix     | quantity | product            | fareType | journeyType |
      | ADAirport | 1 adult | 1        | LargeSportsProduct | Standard | single      |
