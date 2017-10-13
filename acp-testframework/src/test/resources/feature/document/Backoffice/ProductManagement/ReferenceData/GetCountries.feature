@FCPH-7998
Feature: Update getCountries to include international dialling code

  @backoffice:FCPH-7998
  Scenario: 1 - Add international dialing code to coutry in the back office
    Given that I'm on the country in the back office
    When I create a country in back office
    Then I will be able to enter a international dialling code for the country

  @backoffice:FCPH-7998
  Scenario: 2 - Update international dialling code for an existing country
    Given that I have created a country in the back office
    When I select to amend the country
    Then I can update/add international dialling code