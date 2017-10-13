@FCPH-3680
Feature: Update Basket for Add Direct Flight - Deal Discount applied

  Scenario Outline: Based on deal parameters the deal is applied to basket for channel
    When My basket contains deal based on "<channel>" and "<parameters>" and passengerMix "1 Adult"
    Then the deal is applied to basket
    Examples:
      | channel           | parameters                         |
      | PublicApiB2B      | ApplicationId,OfficeId,CorporateId |
      | PublicApiMobile   | ApplicationId,OfficeId,CorporateId |
      | Digital           | ApplicationId,OfficeId,CorporateId |
      | ADAirport         | ApplicationId,OfficeId,CorporateId |
      | ADCustomerService | ApplicationId,OfficeId,CorporateId |
#      | Digital           | ApplicationId,OfficeId             |
#      | PublicApiB2B      | ApplicationId,OfficeId             |
#      | ADAirport         | ApplicationId,OfficeId             |
#      | ADCustomerService | ApplicationId,OfficeId             |
#      | PublicApiMobile   | ApplicationId,OfficeId             |

  Scenario: Booking type is automatically assigned for Deal
    When My basket contains deal with passengerMix "1 Adult"
    Then the booking type value is "Business"

  Scenario: Booking reason is automatically assigned for Deal
    When My basket contains deal with passengerMix "1 Adult"
    Then the booking reason value is "Business"

  Scenario: Discount Tier and POS Fee are applied for passenger mix
    When My basket contains deal with PosFee and with passengerMix "1 adult, 1 child, 1 infant OOS"
    Then Discount Tier and POS fee are applied at passenger level

  Scenario: Calculate the total amount of basket after adding discount and posFee
    When My basket contains deal with passengerMix "1 Adult"
    Then the basket total amount is calculated

  @manual
  Scenario Outline: Different types of bundle added to the basket
    Given I setup the deal with different types of bundle in backoffice
    And I have found a valid flight with deal for different types of bundle
    When I added this flight to my basket for "<bundle>"
    Then the correct bundle is displayed in the basket
    Examples:
      | bundle   |
      | Standard |
      | Flexi    |

  @manual
  Scenario Outline: Discount Tier and POS Fee are applied based on currency
    Given I setup the deal with discount and posfee as "<currency>" in backoffice
    And I have found a valid flight with deal in "<currency>" for "1 Adult"
    When I added this flight to my basket with "<currency>"
    Then Discount Tier and POS fee are applied with "<currency>"
    Examples:
      | currency |
      | GBP      |
      | EUR      |

  @manual
  Scenario: Discount Tier and POS Fee are applied as percentage
    Given I setup the deal with discount and posfee as percentage in backoffice
    And I have found a valid flight with deal in "<percentage>" for "1 Adult"
    When I added this flight to my basket with "<percentage>"
    Then Discount Tier and POS fee are applied with "<percentage>"
