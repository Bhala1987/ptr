@Sprint32
Feature: Update taxes, fees and discounts with new currency in the basket

  @TeamD
  @FCPH-11466
    Scenario: Convert the taxes in the basket and Apply the DCC margin
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I added a flight to the basket from LTN to ALC with Standard fare
    When I changed the currency
    Then the Tax value is converted from the old currency to the new currency value
    And the original currency is stored against the basket

  @TeamD
  @FCPH-11466
  Scenario: Convert the Internet Discount in the basket and Apply the DCC margin
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I added a flight to the basket from LTN to ALC with Standard fare
    When I changed the currency
    Then the Internet Discount value is converted from the old currency to the new currency value
    And the original currency is stored against the basket

  @TeamD
  @FCPH-11466
  Scenario: Store the margin amount and margin percentage on the booking
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I added a flight to the basket from LTN to ALC with Standard fare
    And I changed the currency
    When I proceed to commit the booking
    Then the booking is completed
    And the original currency is stored against the booking
    And the new price in the booking is same as in the basket
    And I store the margin percentage on the OrderEntry

  @TeamD
  @FCPH-11466
  Scenario: Send the margin amount and margin percentage to down stream systems
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I added a flight to the basket from LTN to ALC with Standard fare
    And I changed the currency
    When I proceed to commit the booking
    Then the booking is completed
    And I validate the json schema for created booking event

  @TeamD
  @FCPH-11466
  Scenario: Send the margin amount and margin percentage to down stream systems for amendable basket
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I added a flight to the basket from LTN to ALC with Standard fare
    And I changed the currency
    And I proceed to commit the booking
    And the booking is completed
    And I sent the request to createBasket service
    And I add with sport items for 1 passenger
    When I proceed to commit the booking
    Then the booking is completed
    And I validate the json schema for updated booking event


