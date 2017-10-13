@TeamA @Sprint31
@backoffice:FCPH-9836
Feature: Search for a Voucher in the back office

  Scenario:Search for a voucher in the back office
    Given I am in the back office
    When I search for a Voucher
    Then I can enter the following <search criteria>
      | Search Criteria              |
      | Code                         |
      | Name                         |
      | email address                |
      | Voucher Active Date from     |
      | Voucher Active Date to       |
      | Modification Reason          |
      | Active Flag                  |


  Scenario Outline:Display a list of vouchers which have match the criteria
    Given I am in the back office
    And I search for a Voucher
    And I enter search criteria <criteria>
    When I click on search
    Then I should see a list of vouchers which match the <criteria> entered
    And the search results will be in order of column heading as below
      | Voucher Code | Name | Voucher email address | Voucher Active Date from | Voucher Active Date to | Modification Reason | Active Flag |
    Examples:
      | criteria                     |
      | Code                         |
      | Name                         |
      | email address                |
      | Voucher Active Date from     |
      | Voucher Active Date to       |
      | Modification Reason          |
      | Active Flag                  |