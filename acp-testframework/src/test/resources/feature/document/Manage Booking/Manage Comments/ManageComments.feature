@wip @TeamD
Feature: Send Event To EI Layer For Manage Comments

  @schema @Sprint32 @FCPH-6183
  Scenario: 1 - Generate event when a Comment is added to a booking
    Given the channel ADCustomerService is used
    And I login as agent
    And I have committed a booking
    When I have received a valid addComments request for type Booking with comment add first comment
    Then I validate the json schema for add comment to booking event

  @schema @Sprint32 @FCPH-6183
  Scenario: 2 - Generate event when a Comment is update to a booking
    Given the channel ADAirport is used
    And I login as agent
    And I have committed a booking
    And I have received a valid addComments request for type Booking with comment add first comment
    And I return the result for addComments request
    When I update the added comment from the booking with Booking and update first comment
    Then I validate the json schema for update comment to booking event

  @schema @Sprint32 @FCPH-6183
  Scenario: 3 - Generate event when a Comment is deleted from a booking
    Given the channel ADAirport is used
    And I login as agent
    And I have committed a booking
    And I have received a valid addComments request for type Booking with comment add first comment
    And I return the result for addComments request
    When I delete the added comment from the booking
    Then I validate the json schema for remove comment to booking event

  @schema @Sprint32 @FCPH-6183
  Scenario: 4 - Generate event when a Comment is added to a profile
    Given the channel ADAirport is used
    And I login as agent
    And I create a new valid customer
    When I add a comment to a customer
    Then I validate the json schema for add comment to customer event

  @schema @Sprint32 @FCPH-6183
  Scenario: 5 - Generate event when a Comment is update to a profile
    Given the channel ADAirport is used
    And I login as agent
    And I create a new valid customer
    When I update a customer comment
    Then I validate the json schema for update comment to customer event

  @schema @Sprint32 @FCPH-6183
  Scenario: 6 - Generate event when a Comment is deleted to a profile
    Given the channel ADAirport is used
    And I login as agent
    And I create a new valid customer
    When I remove a customer comment
    Then I validate the json schema for remove comment to customer event









