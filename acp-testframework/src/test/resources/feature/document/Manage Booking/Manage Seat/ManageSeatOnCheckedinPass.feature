Feature: Receive request to return seat map - as part of Manage Booking

  @Sprint32 @TeamC @FCPH-3258 @local
  Scenario Outline: Validate error on passenger requesting seat is checked in
    Given I am using <channel> channel
      When I request to change the purchased seat on a passenger already checked in
    Then I <should> receive an error SVC_100400_2112 based on the channel
    Examples:
      | channel      | should |
      | Digital      | true   |
      | ADAirport    | false  |
