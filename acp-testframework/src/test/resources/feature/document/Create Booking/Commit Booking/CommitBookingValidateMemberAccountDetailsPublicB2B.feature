@Sprint24
@FCPH-2635
Feature: Validate Member Account Details passed in Public Channel
  Scenario Outline:Verify that user is able to create Booking for non existing customer
    When I do the commit booking with non existing customer via <channel>
    Then I should be able to create successful booking with reference number
    And Booking is created from Cart and it has the flight details
    Then I do get customer profile
    And Booking is associated with newly created customer
    And booking should have details of newly created customer
    Examples:
      | channel      |
      | PublicApiB2B |

  Scenario: Validation error message returned for the invalid email address
    When I call the commit booking with missing parameter then we get respective error as below
      | BasketContent_InvalidEmail_Missing@      | SVC_100022_3025 |
      | BasketContent_InvalidEmail_MissingDomain | SVC_100022_3025 |


