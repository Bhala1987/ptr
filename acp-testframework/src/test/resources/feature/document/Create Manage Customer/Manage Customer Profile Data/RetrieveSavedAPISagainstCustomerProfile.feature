@FCPH-3437
Feature: Retrieve Saved APIS against Customer Profile

  # Story 3437:
  # Consider we have the config X = 16 months
  # Today's Date: 30 March 2017
  # 16 months back from today: 30 November 2015
  # Test1: 29 November 2015 - should not retrieve the results
  # Test2: 30 November 2015 - should not retrieve the results
  # Test3: 01 December 2015 - should  retrieve the results
  # Test4: Today 30 March 2017 - should  retrieve the results

  @regression
  Scenario: getAdvancePassengerInformation – Return saved APIS BR_00146 past X=16 months
    Given I have APIs stored for a customer for less than 16 months
    When I request for APIs for that customer
    Then I will return the API details for that customer

  # Reason this has to be manual is because we can't create a document with the past date, we can update the date in the database but as we can't clear the hybris cache(not the memcache)
  @manual
  Scenario: getAdvancePassengerInformation - Not Return saved APIS BR_00146 older X=16 months
    Given I have APIs stored for a customer for greater than 16 months
    When I request for APIs for that customer
    Then I will not return the API details for that customer

  Scenario: getSavedPassengerInformation – Return saved APIS BR_00146 past X=16 months
    Given am using channel ADAirport
    And I have APIs stored for a "passenger" for less than X months
    When I request for APIs for that passenger
    Then I will return the passenger API details successfully

  # Reason this has to be manual is because we can't create a document with the past date, we can update the date in the database but as we can't clear the hybris cache(not the memcache)
  @manual
  Scenario: getSavedPassengerInformation – Not Return saved APIS BR_00146 older X=16 months
    Given I have APIs stored for a customer for greater than 16 months
    When I request for APIs for that customer
    Then I will not return the passenger API details successfully

  Scenario: getAdvancePassengerInformation – No saved Apis found from the profile
    Given I have No APIs stored for a customer for less than X = 16 months
    When I request for APIs for that customer
    Then I will not return the API details

  Scenario: getSavedPassengerInformation – No saved Apis found from the profile for a saved passenger
    Given I have no APIs stored for a "passenger" for less than X months
    When I request for APIs for that passenger
    Then I will not return the passenger API details