@Sprint24
@backoffice:FCPH-8414
Feature:Manage Cabin bags inline with hold bags

  Scenario: Create Cabin Bags the same way as other products e.g. Hold Bags
    Given that I'm in the Cabin bag product page in the backoffice
    When I click to create a new product
    Then I will see the following fields
      | Cabin bag code               |
      | Cabin bag name               |
      | Active from                  |
      | Active to                    |
      | Approval                     |
      | Catalog version              |
      | Cabin bag description        |
      | Vatable                      |
      | Enable flight level capping  |
      | Bundle templates             |
      | Bundle templates restricions |
      | Weight (Kg)                  |
      | Maximum weight (Kg)          |
      | Length (cm)                  |
      | Width (cm)                   |
      | Height (cm)                  |

  Scenario: Mandatory fields
    Given that I'm creating a cabin bag product
    When I fill the fields
    Then I must enter 'Cabin bag code'
    And I must enter 'Cabin bag name'

  Scenario: Unique code
    Given that I'm creating a new product
    And I have entered a code that already exist
    When I click to create the product
    Then I see a error message

  Scenario: Active from date
    Given that I'm on the Cabin bag product creation screen
    When I enter a Active from date
    Then I will be able to enter only a date greater than or equal to today's date

  Scenario: Active to date
    Given that I'm on the Cabin bag product creation screen
    When I enter a Active to date
    Then I will be able to enter only a date greater than the Active from date

  Scenario: Bundle template
    Given that I'm on the Cabin bag product creation screen
    When I set a Bundle template
    Then I will be able to select active Bundle to set the Bundle Restriction for the Cabin Bag product

  Scenario: Bundle restrictions
    Given that I'm on the Cabin bag product creation screen
    And I selected the same bundle for the bundle template and the bundle restriction
    When I click to create the product
    Then I see a error message

  Scenario: Store the product
    Given that I'm on the Cabin bag product creation screen
    And I have entered at least the mandatory fileds
    When I click on Done
    Then I will record creation date, time and user ID

  Scenario: Modifiy a Cabin bag product item
    Given that I'm in the Cabin bag product page in the backoffice
    When I double click on the product
    Then I will be able to amend the following fields
      | Cabin bag name               |
      | Active from                  |
      | Active to                    |
      | Approval                     |
      | Catalog version              |
      | Cabin bag description        |
      | Vatable                      |
      | Enable flight level capping  |
      | Bundle templates             |
      | Bundle templates restricions |
      | Weight (Kg)                  |
      | Maximum weight (Kg)          |
      | Length (cm)                  |
      | Width (cm)                   |
      | Height (cm)                  |

  Scenario: Store the Modification date and time
    Given that I'm amending an existing Product
    When I click on Done
    Then I will record modification date, time and user ID


  @Sprint25
  @backoffice:FCPH-8449
  Scenario: Set a channel restriction against a Product type
    Given that I have want to create a new product type
    When I go to manage product type
    Then I can select to add a channel restriction to the product type
