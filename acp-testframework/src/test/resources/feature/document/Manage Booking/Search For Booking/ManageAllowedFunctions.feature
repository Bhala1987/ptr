@Sprint30
@TeamA
@backoffice:FCPH-10590
Feature: Set Up to Manage Allowed Functions for Bookings

  Background:
    Given I am in the back office

  Scenario: Able to define permissions for specific View Booking actions
    When I see the Manage Allowed Functions for View Bookings
    Then I can set the Channels that are allowed
    And I can set the Booking Types that are allowed
    And I can set the Access Type (passenger, booker, agent) that are allowed

  Scenario: Able to define permissions for specific Change Passenger Details actions
    When I see the Manage Allowed Functions for Change Passenger Details
    Then I can set the Channels that are allowed
    And I can set the Booking Types that are allowed
    And I can set the Access Type (passenger, booker, agent) that are allowed

  Scenario: Able to change permissions for specific actions
    When I see the Manage Allowed Functions for view booking and Change Passenger Details
    Then I can change the Channels that are allowed
    And I can change the Booking Types that are allowed
    And I can change the Access Type (passenger, booker, agent) that are allowed