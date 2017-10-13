@FCPH-207 @FCPH-3677 @FCPH-209
Feature: Save and retrieve a search to a profile

# Note1: Definition of a unique search : Not having the same any of the departure airport, arrival airport,  passenger mix, outbound date or inbound date
# Note2: We can configure for maximum saves allowed for a profile.
# Note3: We never modifies an existing search that is store against a profile.

  Background:
    Given that the configurations is in place for maximum saves allowed


  Scenario: Validate successful save to a profile
    And customer has "0" searches saved to the profile
    When I made a new search for flights successfully
    Then this search should be saved

  Scenario: Verify multiple saves to a profile
    And customer has "1" searches saved to the profile
    When I made a new search for flights successfully
    Then this search should be saved

  Scenario: Duplicate search shouldn't be saved twice
    Given customer has "0" searches saved to the profile
    When I made a new search for flights successfully
    And I made the same search again
    Then search should be saved only once

  @regression
  Scenario: Validate latest search over writes the oldest search
    Given customer has "maximum allowed" searches saved to the profile
    When I made a new search for flights successfully
    Then this search should be saved
    And oldest search should be removed

  Scenario: Verify no search results retrieved
    Given customer has "0" searches saved to the profile
    Then I should see no search results

  Scenario: Verify the search saved if the request is through a different session
    Given customer has "0" searches saved to the profile
    When I made a new search for flights successfully
    And I made another new search for flights from a different session
    Then I should see two searches saved to the profile

  @pending
  @manual
  Scenario: Past date results can't be retrieved
    Given customer has "0" search saved to the profile with the past date
    When I retrieve the recent searches
    Then I should see no search results

  Scenario: Recent search can be saved if the outbound date is changed
    And customer has "0" searches saved to the profile
    When I made a new search for flights successfully
    And I made the same search again with different outbound date
    Then I should see two searches saved to the profile


  Scenario: Recent search can be saved if the inbound date is added
    And customer has "0" searches saved to the profile
    When I made a new search for flights successfully
    And I made the same search again with different inbound date
    Then I should see two searches saved to the profile

  Scenario: Recent search can be saved if the inbound date is changed
    And customer has "0" searches saved to the profile
    When I made a new return search for flights successfully
    And I made the same search again with different inbound date
    Then I should see two searches saved to the profile