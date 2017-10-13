@FCPH-9732
@Sprint27
Feature: Speedy Boarding Fast track Icon

    @manual
    Scenario: 1 - Boarding pass for fast track icon BR_04007
      Given that I have received a valid boarding pass request
      When the passenger has fast track product
      And the departing airport has fast track set to yes
      Then Fast track icon is added is added for selected passenger
      And Fast track content slot is displayed

    @manual
    Scenario: 2 - Boarding pass for speedy boarding BR_04008
      Given that I have received a valid boarding pass request
      When the passenger has speedy boarding product
      Then Speedy Boarding icon is added for selected passenger
