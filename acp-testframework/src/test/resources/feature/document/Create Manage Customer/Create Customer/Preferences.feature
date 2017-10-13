@FCPH-2752
Feature: Service returns available customer preferences

  @manual
  Scenario: Request returns all applicable customer preference fields
    Given there are available customer preference fields
    When the request for preferences is called
    Then an empty preferences available is returned

  @negative
  Scenario: verify the error message when Opted out marketing options are missing
    Given a valid request to create customer profile
    And the opted out marketing options are empty
    When the request is validated for negative scenario
    Then a "SVC_100047_2011" error is returned as expected

  @negative
  Scenario: verify the error message when passenger title is missing in request
    Given a valid request to create customer profile
    And the passenger title is empty
    When the request is validated for negative scenario
    Then a "SVC_100047_2034" error is returned as expected

 ##creating customer and get passenger types are two requests and in system test
 ##can not use these two requests together and hence all the below scenarios
 ##can check individually which has been covered in different feature files

  @manual
  Scenario: Receive request for market groups
    Given that the channel has received a request to create a new customer
    When I receive a request to getMarkgetGroup from the channel
    Then I will return a list of active market groups to the channel
