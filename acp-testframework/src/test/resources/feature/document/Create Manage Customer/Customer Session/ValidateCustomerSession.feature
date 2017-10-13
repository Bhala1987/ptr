# We cannot wait in automation, or change the property configuration
@manual
@TeamD
@Sprint30
@FCPH-9991
Feature: Validate the customer session for create booking

  Scenario: Treat the customer as annoymous if the customer logged in session has expired for flight search
    Given I am using one of this channel Digital, ADAirport
    And set the session timeout to 60 seconds in the properties file
    And I login with the customer
    And I do flight search and add a flight to basket
    And verify in the back office that the cart is associated to the logged in customer
    And wait for 60 seconds
    When I do flight search and add a flight to basket
    Then verify in the back office that the cart is associated to the anonymous user

  Scenario Outline: Treat the customer as annoymous if the customer logged in session has expired for specific Channel request
    Given I am using one of this channel Digital, ADAirport
    And set the session timeout to 60 seconds in the properties file
    And I login with the customer
    And I do flight search and add a flight to basket
    And I add a <product> to the basket
    And verify in the back office that the cart is associated to the logged in customer
    And wait for 60 seconds
    When I do flight search and add a flight to basket
    And I add a <product> to the basket
    Then verify in the back office that the cart is associated to the anonymous user

    Examples:
      | product            |
      | addHoldItem        |
      | addSportsEquipment |
      | addSeatProduct     |

