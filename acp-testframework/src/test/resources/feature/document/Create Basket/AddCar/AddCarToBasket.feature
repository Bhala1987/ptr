@Sprint32 @TeamC @FCPH-3674
Feature: Add car to basket

  @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Error when request is missing mandatory fields
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product to the basket without mandatory objects <mandatoryObjects> having required fields <fields>
    Then I see an error with code <errorCode>
    Examples:
      | channel   | passengerMix | journeyType | mandatoryObjects | fields                                                                     | errorCode       |
      | ADAirport | 1 Adult      | SINGLE      | location         | pickUpStation,pickUpDate,pickUpTime,dropOffStation,dropOffDate,dropOffTime | SVC_100800_1000 |
      | Digital   | 1 Adult      | RETURN      | carHireProduct   | rateID,carCategoryCode,carCategoryName,totalPrice,currency                 | SVC_100800_1000 |
      | ADAirport | 1 Adult      | SINGLE      | driver           | passengerCode                                                              | SVC_100800_1015 |

  @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Error when drop location is different country from pickup
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product with pickup and dropoff location in different countries
      | pickUpAirport  | <pickUpAirport>  |
      | dropOffAirport | <dropOffAirport> |
      | pickUpStation  | <pickUpStation>  |
      | dropOffStation | <dropOffStation> |
    Then I receive an error with code SVC_100800_1011
    Examples:
      | channel   | passengerMix          | journeyType | pickUpAirport | dropOffAirport | pickUpStation | dropOffStation |
      | ADAirport | 1 Adult, 1 Infant OOS | SINGLE      | ALC           | LTN            | ALCT01        | LTNT01         |
      | Digital   | 1 Adult, 1 Infant OL  | RETURN      | LTN           | ALC            | LTNT01        | ALCT01         |

  @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Error when car driver age is less than 18 years
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    And I have updated age for adult passenger to <newAge> age
    When I request to add car product associated with passenger type <passengerType>
    Then I receive an error with code SVC_100800_1001
    Examples:
      | channel   | passengerMix         | newAge | passengerType | journeyType |
      | ADAirport | 1 Adult, 1 Infant OL | 0      | infant        | SINGLE      |
      | Digital   | 1 Adult, 1 Child     | 0      | child         | RETURN      |
      | Digital   | 1 Adult              | 17     | adult         | SINGLE      |

  @manual @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Car added to the basket
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product to the basket
    Then Car product is added to the basket
    And I see the total price is updated with car hire product
    Examples:
      | channel   | passengerMix          | journeyType |
      | ADAirport | 1 Adult, 1 Infant OOS | SINGLE      |
      | Digital   | 1 Adult, 1 Infant OL  | RETURN      |

  @manual @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Only one car can be added to the basket
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product to the basket
    And I request to add another car product to the basket
    Then I receive an error with code SVC_100800_1000
    Examples:
      | channel   | passengerMix         | journeyType |
      | Digital   | 1 Adult, 1 Infant OL | SINGLE      |
      | ADAirport | 1 Adult, 1 Infant OL | RETURN      |

  @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Maximum number of x car equipments can be added
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product to the basket with <noOfCarEquipments> car equipments
    Then I receive an error with code SVC_100800_1014
    Examples:
      | channel   | passengerMix         | noOfCarEquipments | journeyType |
      | Digital   | 1 Adult, 1 Infant OL | 5                 | SINGLE      |
      | ADAirport | 1 Adult, 1 Infant OL | 6                 | RETURN      |

  @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Car hire period should not be more than 28 days
    Given Inbound and outbound flight date difference is more than 28 days
    And Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product to the basket
    Then I receive an error with code SVC_100800_1006
    Examples:
      | channel   | passengerMix | journeyType |
      | Digital   | 1 Adult      | RETURN      |
      | ADAirport | 1 Adult      | RETURN      |

  @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Drop off date cannot be beyond 1st inbound flight date
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product to the basket with <dropOffDate> dropOff date and <dropOffTime> dropOff time
    Then I receive an error with code SVC_100800_1007
    Examples:
      | channel   | passengerMix         | journeyType | dropOffDate             | dropOffTime |
      | Digital   | 1 Adult, 1 Infant OL | RETURN      | sameAsInBoundFlightDate | 1           |
      | ADAirport | 1 Adult              | RETURN      | afterInboundFlightDate  | 0           |

  @Sprint32 @TeamC @FCPH-3674
  Scenario Outline: Pick up date cannot be before outbound flight arrival date
    Given Using channel <channel> with passenger mix <passengerMix> for journey <journeyType>
    When I request to add car product to the basket with <pickUpDate> pickUp date and <pickUpTime> pickUp time
    Then I receive an error with code SVC_100800_1008
    Examples:
      | channel   | passengerMix         | journeyType | pickUpDate                | pickUpTime |
      | Digital   | 1 Adult, 1 Infant OL | SINGLE      | sameAsOutBoundArrivalDate | -3         |
      | ADAirport | 1 Adult, 1 Infant OL | RETURN      | beforeOutBoundArrivalDate | 0          |