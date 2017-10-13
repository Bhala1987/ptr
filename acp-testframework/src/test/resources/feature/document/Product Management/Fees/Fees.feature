@TeamD
@Sprint32
Feature: Generate event when fee or tax is changed

  @backoffice:FCPH-10436
  @manual
  Scenario: Generate event to down stream system if a fee changed
    Given that a fee row exists in the back office
    When I change any fee in the back office
    Then I will generate an event to inform downstream systems

  @backoffice:FCPH-10436
  @manual
  Scenario: Generate event to down stream system if a tax is changed
    Given that a tax row exists in the back office
    When I change any tax in the back office
    Then I will generate an event to inform downstream systems