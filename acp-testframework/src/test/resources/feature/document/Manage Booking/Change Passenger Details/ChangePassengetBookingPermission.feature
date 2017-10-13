@Sprint32
@FCPH-10591
@TeamA
Feature: Apply Manage Booking Permissions for View Booking Details and Change Passenger Details

Given that the channel has initiated a manage booking action

  Scenario Outline: that the channel has initiated a manage booking action -Update Passender details
    Given I am using <channel> channel
    When I commit booking with <fareType> fare and <passenger> passenger
    And I update below details with for couple of passenger
      | name        |
      | phonenumber |
    And I get the amendable basket with updated details
    Then I will determine the booking is editable for the action

    Examples:
      | channel | fareType | passenger |
      | Digital | Standard | 3 Adult   |


  Scenario Outline: that the channel has initiated a manage booking action -  Add IOL to Pax

    Given I am using <channel> channel
    When I commit booking with <fareType> fare and <passenger> passenger
    When I send the request to add Infant OL to an adult
    And I update below details with for couple of passenger

      | name        |
      | phonenumber |
    Then I receive an updated basket
    And I see an Infant on Lap product in the basket
    And I see an infant passenger in the basket
    And I see the Infant on Lap assigned to the passenger
    And I get the amendable basket with updated details
    Then I will determine the booking is editable and infantonlap to passenger

    Examples:
      | channel | fareType | passenger |
      | Digital | Standard | 3 Adult   |


  @TeamA @Sprint32 @FCPH-10591
  Scenario Outline: that the channel has initiated a manage booking action -Move Infant on Lap to another Adult on the booking
    Given I am using <channel> channel
    And I have amendable basket for <fareType> fare and <passenger> passenger
    When I send a request to associate infant to another Adult with seat
    Then the basket should be updated with the new association
    Examples:
      | channel | passenger            | fareType |
      | Digital | 2 adult, 1 infant OL | Standard |


  Scenario Outline:  that the channel has initiated a manage booking action: Add New APIS doc to customer
    Given I am using <channel> channel
    When I created an amendable basket for <fareType> fare and <passenger> passenger without apis
    And add APIs and recommit booking
    And channel send getbooking request
    Then booking should have added with APIs

    Examples:
      | channel  | fareType | passenger |
      | Digital  | Standard | 3 Adult   |


  Scenario Outline: that the channel has initiated a manage booking action: Add an SSR in the basket.
    Given basket contains return flight for 2 Adult passengers Standard fare via the Digital channel
    And I do the commit booking
    And the booking is amendable
    When passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | WCHR |                  | false                     |
    Then I get the amendable basket with WCHR SSR

    Examples:
      | fareType | passenger |
      | Standard | 3 Adult   |
