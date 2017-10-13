Feature: - Apply Price Override and Promotions to the basket

  @FCPH-3427
  Scenario Outline: Validate mandatory field for apply override price
    Given I have in my basket a direct flight with different passenger mix "1 Adult, 1 Child, 1 Infant OL" using channel "<Channel>"
    And I have added product to my basket
    And the channel has initiated a price override
    But the request miss to specify the mandatory field "<Field>"
    When I receive the request to apply the discount to the basket
    Then return error messages "<Error>" to the channel
    Examples:
      | Channel           | Field               | Error           |
      | Digital           | reasonCode          | SVC_100363_2002 |
      | ADAirport         | overrideTotalAmount | SVC_100363_2006 |
      | ADCustomerService | basketCode          | SVC_100363_2008 |
      | PublicApiMobile   | reasonCode          | SVC_100363_2002 |
      | PublicApiB2B      | overrideTotalAmount | SVC_100363_2006 |
#      | Digital           | overrideTotalAmount | SVC_100363_2006 |
#      | Digital           | basketCode          | SVC_100363_2008 |
#      | ADAirport         | reasonCode          | SVC_100363_2002 |
#      | ADAirport         | basketCode          | SVC_100363_2008 |
#      | ADCustomerService | reasonCode          | SVC_100363_2002 |
#      | ADCustomerService | overrideTotalAmount | SVC_100363_2006 |
#      | PublicApiMobile   | overrideTotalAmount | SVC_100363_2006 |
#      | PublicApiMobile   | basketCode          | SVC_100363_2008 |
#      | PublicApiB2B      | reasonCode          | SVC_100363_2002 |
#      | PublicApiB2B      | basketCode          | SVC_100363_2008 |


  @FCPH-3427
  Scenario Outline: Apply discount reason to the basket
    Given I have in my basket a direct flight with different passenger mix "1 Adult, 1 Child, 1 Infant OL" using channel "<Channel>"
    And I have added product to my basket
    And the channel has initiated a price override
    When I receive the request to apply the discount to the basket
    Then I will add a discount line including the discount reason for the requested amount
    And I will update the total price of the basket less the discount
  @regression
    Examples:
      | Channel |
      | Digital |
    Examples:
      | Channel           |
      | ADAirport         |
      | ADCustomerService |
      | PublicApiMobile   |
      | PublicApiB2B      |

  @FCPH-3427
  Scenario Outline: Apply discount reason to the Passenger
    Given I have in my basket a direct flight with different passenger mix "1 Adult, 1 Child, 1 Infant OL" using channel "<Channel>"
    And I have added product to my basket
    And the channel has initiated a price override
    When I receive a valid request to apply a discount to the passenger
    Then I will add a discount line in the the passenger price including the discount reason for the requested amount
    And update the total of the passenger
    And I will update the total price of the basket less the discount
    Examples:
      | Channel           |
      | Digital           |
      | ADAirport         |
      | PublicApiB2B      |

  @FCPH-3427
  Scenario Outline: Apply discount reason to the Product
    Given I have in my basket a direct flight with different passenger mix "1 Adult, 1 Child, 1 Infant OL" using channel "<Channel>"
    And I have added product to my basket
    And the channel has initiated a price override
    When I receive a valid request to apply a discount to the product
    Then I will add a discount line in the the product price including the discount reason for the requested amount
    And update the total of the product
    And I will update the total price of the basket less the discount
    Examples:
      | Channel           |
      | Digital           |
      | ADCustomerService |
      | PublicApiB2B      |

  @FCPH-3428
  Scenario Outline: Validate mandatory field for remove override price
    Given I have in my basket a direct flight with different passenger mix "1 Adult, 1 Child, 1 Infant OL" using channel "<Channel>"
    And I have added product to my basket
    And the channel has initiated a price override
    And I receive the request to apply the discount to the basket
    And I have received a removePriceOverride request
    But the request to remove the item miss to specify the mandatory field "<Field>"
    When I validate the request removePriceOverride
    Then return error messages "<Error>" to the channel
    Examples:
      | Channel           | Field        | Error           |
      | Digital           | basketCode   | SVC_100364_2001 |
      | ADAirport         | discountCode | SVC_100364_2002 |
      | ADCustomerService | basketCode   | SVC_100364_2001 |
      | PublicApiMobile   | discountCode | SVC_100364_2002 |
      | PublicApiB2B      | basketCode   | SVC_100364_2001 |
#      | Digital           | discountCode | SVC_100364_2002 |
#      | ADAirport         | basketCode   | SVC_100364_2001 |
#      | ADCustomerService | discountCode | SVC_100364_2002 |
#      | PublicApiMobile   | basketCode   | SVC_100364_2001 |
#      | PublicApiB2B      | discountCode | SVC_100364_2002 |

  @FCPH-3428
  Scenario Outline: Remove override price from basket level
    Given I have in my basket a direct flight with different passenger mix "1 Adult, 1 Child, 1 Infant OL" using channel "<Channel>"
    And I have added product to my basket
    And the channel has initiated a price override
    And I receive the request to apply the discount to the basket
    And I have received a removePriceOverride request
    When I validate the request removePriceOverride
    Then I will remove the Discount item from the basket
    And Update the total price of the basket
    Examples:
      | Channel           |
      | Digital           |
      | ADCustomerService |
      | PublicApiB2B      |

  @FCPH-3428
  Scenario Outline: Remove override price from product level
    Given I have in my basket a direct flight with different passenger mix "1 Adult, 1 Child, 1 Infant OL" using channel "<Channel>"
    And I have added product to my basket
    And the channel has initiated a price override
    And I receive a valid request to apply a discount to the product
    And I have received a removePriceOverride request
    When I validate the request removePriceOverride
    Then I will remove the Discount item from the basket on product level
    And I will Update the price of the line item
    And Update the total price of the basket
    Examples:
      | Channel           |
      | Digital           |
      | ADCustomerService |
      | PublicApiB2B      |
