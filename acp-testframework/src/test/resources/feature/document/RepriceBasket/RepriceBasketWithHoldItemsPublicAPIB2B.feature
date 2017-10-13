@Sprint26
@FCPH-8961
Feature: Request to revalidate the basket for public API for hold items and seat price change

  Scenario Outline: Request recalculatePrices service with invalid basket content for hold items
    Given I am using the channel <channel>
    And I search for flight with passenger mix <passengerMix> and details as
      | journey     | single |
      | origin      | LTN    |
      | destination | ALC    |
    And I have valid basket content with passenger mix as <passengerMix> and <fareType>
    And I add holdItem as <productType> for 1'st  passenger to my basket content
    And  I update the basket content with invalid request <parameter> for product <productType>
    When I trigger the recalculatePrices service with invalid request <parameter>
    Then I will receive an error code as "<error>"
    Examples:
      | channel      | passengerMix | fareType | productType        | parameter         | error           |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct     | Missing_Quantity  | SVC_100022_2100 |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct     | Missing_BasePrice | SVC_100022_2097 |
      | PublicApiB2B | 1 adult      | Standard | SmallSportsProduct | Missing_Code      | SVC_100022_2098 |

  Scenario Outline: Return an message to the channel and where no price change has been identified for products
    Given I am using the channel <channel>
    And I search for flight with passenger mix <passengerMix> and details as
      | journey     | single |
      | origin      | LTN    |
      | destination | ALC    |
    And I have valid basket content with passenger mix as <passengerMix> and <fareType>
    And I add holdItem as <productType> for <passengerIndex>  passenger to my basket content
    When I trigger the recalculatePrices service
    Then I should get the success response along with basket
    And I should get the message in additional information informing that there is no change
    And I will verify that basket has no changes
    Examples:
      | channel      | passengerMix | fareType | productType                       | passengerIndex |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct                    | 1'st           |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct,SmallSportsProduct | 1'st           |

  Scenario Outline: Return an message to the channel and where price change has been identified for respective hold item
    Given I am using the channel <channel>
    And I search for flight with passenger mix <passengerMix> and details as
      | journey     | single |
      | origin      | LTN    |
      | destination | ALC    |
    And I have valid basket content with passenger mix as <passengerMix> and <fareType>
    And I add holdItem as <productType> for <passengerIndex>  passenger to my basket content
    And I updated the price of <product> in my basket content
    When I trigger the recalculatePrices service
    Then I should get the success response along with basket
    Then I should get the success response with "SVC_100187_1001"
    And I should get the affected data with SVC_100187_2003
    And I should get the notification of <product> change in response
    And I will verify that basket has changes
    Examples:
      | channel      | passengerMix | fareType | productType                                           | passengerIndex | product             |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct                                        | 1'st           | HoldBagProduct      |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct,SmallSportsProduct                     | 1'st           | HoldBagProduct      |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct,SmallSportsProduct                     | 1'st           | SmallSportsProduct  |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct,SmallSportsProduct,ExcessWeightProduct | 1'st           | ExcessWeightProduct |
      | PublicApiB2B | 1 adult      | Standard | HoldBagProduct,SmallSportsProduct,ExcessWeightProduct | 1'st           | ExcessWeightProduct |
      | PublicApiB2B | 1 adult      | Standard | LargeSportsProduct                                    | 1'st           | LargeSportsProduct  |

  @regression
  Scenario Outline:Return an updated basket to the channel there is price change for seat
    Given I am using the channel <channel>
    And I have basket content with seats
    And I updated the price of <product> in my basket content for 1'st passenger
    When I trigger the recalculatePrices service
    Then I should get the success response along with basket
    And I should get the affected data with SVC_100187_2003
    And I should get the notification of <product> change in response
    Examples:
      | channel      | product |
      | PublicApiB2B | seat    |

