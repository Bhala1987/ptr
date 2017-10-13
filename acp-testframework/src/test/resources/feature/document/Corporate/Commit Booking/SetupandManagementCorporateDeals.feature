Feature: Set up Booking Type for Deal

  @TeamD
  @Sprint30
  @backoffice:FCPH-10683
  Scenario: Set Booking type against the Deal
    Given that I'm on the Deal folder
    When I select to create a Deal
    Then I must select a bookingType

  @TeamD
  @Sprint30
  @backoffice:FCPH-10683
  Scenario: Update Booking type
    Given that I'm on the Deal folder
    When I Update a Deal
    Then I can Update bookingType

  @TeamD
  @Sprint30
  @backoffice:FCPH-10683
  Scenario: Update Booking type
    Given that I'm on the Deal folder
    When I Update a Deal with empty booking type
    Then I will see an error message

  @TeamD
  @Sprint30
  @backoffice:FCPH-10683
  Scenario: Search for deal by Booking type
    Given that I'm on the Deal folder
    When I'm searching for an existing deal
    Then I am able to search using the booking type

  @TeamD
  @Sprint30
  @backoffice:FCPH-10683
  Scenario: Display results including Booking type
    Given that I'm on the deals folder
    And I'm searching for an existing deal
    When the search results are returned
    Then I should be able to see the booking type associated to the Deal

  @manual
  @TeamD
  @Sprint30
  @FCPH-10683
  Scenario: Generate error file entry if Booking type is not provided for a deal
    Given a file in which booking type is not provided for a deal
    When I upload the file
    Then the error file entry I'm unable to load due to missing booking type for the deal is generated

  @manual
  @TeamD
  @Sprint30
  @FCPH-10683
  Scenario: Generate error file entry if Booking type is not provided for a deal
    Given a file in which a booking type is not valid
    When I upload the file
    Then the error file entry I'm unable to load due to booking type not found for the deal is generated


