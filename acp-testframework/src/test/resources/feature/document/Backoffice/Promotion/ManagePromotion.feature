@TeamA @Sprint31
@backoffice:FCPH-4082
Feature: Search for a Voucher in the back office

  Scenario: View Manage Promotions page
    Given I'm in the backoffice
    When I search for promotions on the left navigator
    And click on the Promotions option from the search results
    Then Manage Promotions page will be displayed
    And it will have an initial list of published or unpublished promotions if added previously

  Scenario: Manage Promotions screen
    Given that I'm on the Manage Promotions page
    When I select to create a new promotion
    Then the Manage Promotions Screen will appear
    And I will have to enter below details
      | Promotion type                            |
      | Code (Unique value)                       |
      | Promotion Title                           |
      | Description of the promotion              |
      | Offer amount/Percentage                   |
      | Active Flag Y / N                         |
      | Status (Unpublished/ Published)           |
      | Priority                                  |
      | Rule Group (Product Level or Order level) |
      | Restrictions for the promotion            |

  Scenario: Select Promotion Type
    Given that I'm on the Manage Promotion page
    When I view the list of available conditions and actions
    Then I can create the following <promotion types>
      |Promotion Types|
      |Buy X Get Y at a fixed price discount amount|
      |Percentage Discount|
      |Order threshold fixed discount amount|

  Scenario: Select Qualifying Products
    Given that I'm on the Manage Promotions screen
    When I select to qualifying products to the promotion
    Then I should be able to select from any <<Product Type>>
      |Product Type|
      |Hold Item|
      |Cabin Bags|
      |Sports Equipment|
      |Excess Weight|
      |Seating products|

  Scenario: Validation of creation of promotion
    Given that I'm Manage Promotions screen
    When I have entered all the mandatory fields
    Then the Done button will be enabled
      |Mandatory Fields|
      |Code|
      |-Promotion type-|
      |-Identifier-|
      |-Promotion Title-|
      |-Offer amount/Percentage-|
      |-Active Flag-|
      |-Priority-|
      |-Product Level or Order level-|

  Scenario:Create of Promotion
    Given that I'm Manage promotions screen
    And all mandatory fields are complete
    When select Done
    Then Promotion will be stored
    And audit is created which includes user ID, creation date time

  Scenario: Promotion Search Fields are displayed
    Given that I'm on the Manage Promotions page
    When I want to search for a promotion
    Then I will be able to search by below fields
    | Code|
  |Promotion Type|
  |Promotion Restriction Type|
  |Promotion Products        |
  |Rule Group (Order or Product Level)|
  |Promotion Name                     |

  Scenario: Update Existing Promotion
    Given that I'm on the Manage Promotions page
    When I select a promotion from the search results screen
    Then the Manage Promotions Screen will appear
    And I will be able to amend some or all of the details about the promotion

  Scenario: User is able to clone an existing promotion
    Given that I'm on the Manage Promotions screen
    When I select to clone the promotion
    Then all the properties of the existing promotion will be copied into the new promotion

  Scenario: Store audit of the changes to promotions
    Given that I'm on the Manage Promotions page
    When I update a promotion
    And I select Done
    Then I will store the updated promotion
    And an audit is created which includes user ID, creation date time and modified values (previous value, current value)