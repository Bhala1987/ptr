@FCPH-8757
Feature: Request to revalidate the basket for public API for flights only

  Scenario Outline: Request recalculatePrices service with invalid basket content
    Given I am using the channel <channel>
    And I have the basket content with invalid request <parameter>
    When I trigger the recalculatePrices service with invalid request <parameter>
    Then I will receive an error code as "<error>"
    Examples:
      | channel      | parameter                                   | error           |
      | PublicApiB2B | Empty_BasketContent                         | SVC_100187_2004 |
      | PublicApiB2B | BasketContent_ContentMissingOutboundDetails | SVC_100022_2040 |
      | PublicApiB2B | BasketContent_MissingCustomerContext        | SVC_100022_2032 |
      | PublicApiB2B | BasketContent_MissingCustomerAddress        | SVC_100022_2036 |
      | PublicApiB2B | BasketContent_MissingCustomerEmail          | SVC_100022_2030 |
      | PublicApiB2B | BasketContent_MissingPassengerList          | SVC_100022_2034 |
      | PublicApiB2B | BasketContent_InvalidFlightKey              | SVC_100012_1003 |
      | PublicApiB2B | BasketContent_InvalidPassengerId            | SVC_100022_2073 |
      | PublicApiB2B | BasketContent_MissingPassengerId            | SVC_100022_2059 |
      | PublicApiB2B | BasketContent_MissingDefaultCardType        | SVC_100022_2009 |
      | PublicApiB2B | BasketContent_InvalidCurrency               | SVC_100022_2010 |
      | PublicApiB2B | BasketContent_MissingFlightKey              | SVC_100022_2047 |
      | PublicApiB2B | BasketContent_MissingFlightDetails          | SVC_100022_2040 |

  Scenario Outline: Return an message to the channel and where no price change has been identified
    Given I am using the channel <channel>
    And I search for flight with passenger mix <passengerMix> and details as
      | journey     | single |
      | origin      | LTN    |
      | destination | ALC    |
    And I have valid basket content with passenger mix as <passengerMix> and <fareType>
    When I trigger the recalculatePrices service
    Then I should get the success response along with basket
    And I should get the message in additional information informing that there is no change
    And I will verify that basket has no changes
    Examples:
      | channel      | passengerMix | fareType |
      | PublicApiB2B | 1 adult      | Standard |

  @negative
  Scenario Outline:Return an updated basket to the channel and price change message returned
    Given I am using the channel <channel>
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I have basket content with passenger mix as <passengerMix> and <fareType> and criteria as <criteria>
    When I trigger the recalculatePrices service
    Then I should get the success response along with basket
    And I should get the message in additional information informing about change
    Then I should get the success response with "SVC_100187_1001"
    And I should get the affected data with SVC_100187_2003
    And I will verify that basket has changes
    Examples:
      | channel      | passengerMix | fareType | criteria            |
      | PublicApiB2B | 2 adult      | Standard | flight price change |

  Scenario Outline:Return an updated basket to the channel if there is fees changes
    Given I am using the channel <channel>
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I have basket content with passenger mix as <passengerMix> and <fareType> and criteria as <criteria>
    When I trigger the recalculatePrices service
    Then I should get the success response along with basket
    And I will verify that basket has changes
    Examples:
      | channel      | passengerMix | fareType | criteria              |
      | PublicApiB2B | 1 adult      | Standard | passenger fees change |
      | PublicApiB2B | 1 adult      | Standard | passenger tax change  |

  Scenario Outline:Return an updated basket to the channel and price change message returned with return flight
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passengerMix>
    And I have basket with '<fareType>' fare as '<journeyType>' journey to build basket content
    And I have basket content with faretype as '<fareType>' and journeyType as '<journeyType> with criteria <criteria>
    When I trigger the recalculatePrices service
    Then I should get the success response along with basket
    And I should get the message in additional information informing about change
    And I should get the affected data with Basket price changes identified.
    And I will verify that basket has changes
    Examples:
      | channel      | passengerMix | journeyType      | fareType | criteria            |
      | PublicApiB2B | 2 adult      | outbound/inbound | Standard | flight price change |

    @manual
  Scenario Outline:Return an updated basket to the channel if one of the flight is not available
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passengerMix>
    And I have basket with '<fareType>' fare as '<journeyType>' journey to build basket content
    And I have basket content
    When I request recalculate price for basket content with <index> flight not available for <journey> journey
    Then I should get the success response along with basket
    And I should get the message in additional information informing about change
    And I should get the affected data with "Flight not available"
    And I will verify that the <index> flight with journey as <journey> is removed from basket
    And I will verify that the basket price is updated
    Examples:
      | channel      | passengerMix | journeyType      | fareType | index | journey  |
      | PublicApiB2B | 2 adult      | outbound/inbound | Standard | 1'st  | outbound |
      | PublicApiB2B | 2 adult      | outbound/inbound | Standard | 1'st  | inbound  |

