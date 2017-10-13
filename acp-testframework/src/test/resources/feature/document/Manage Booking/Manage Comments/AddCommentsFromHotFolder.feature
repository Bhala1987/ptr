Feature: ACP Adds comments from revenue protection team to bookings and customer profile

  @manual
  @TeamD
  @Sprint29
  @FCPH-6060
  Scenario: Generate email to inform of errors which not able to load
    Given the revenue protection comments file placed in hot folder
    When there are failed items which are unable to load
    Then send the list of failed items which have been unable to load in an email

  @manual
  @TeamD
  @Sprint29 @Sprint31
  @FCPH-6060 @FCPH-10860
  Scenario: Generate Error if the booking reference can not be identified
    Given the revenue protection comments file placed in hot folder
    When an invalid booking reference mentioned for some items
    Then each failed item appear as an individual line in the error report generated
    And each item contains the reason why the data was not able to import
    And each item includes date/time it failed to import
    And continue to process the other valid items on the file

  @manual
  @TeamD
  @Sprint29 @Sprint31
  @FCPH-6060 @FCPH-10860
  Scenario: Generate error if the booking reference is blank
    Given the revenue protection comments file placed in hot folder
    When a booking reference is blank for some items
    Then each failed item appear as an individual line in the error report generated
    And each item contains the reason why the data was not able to import
    And each item includes date/time it failed to import
    And continue to process the other valid items on the file

  @manual
  @TeamD
  @Sprint29 @Sprint31
  @FCPH-6060 @FCPH-10860
  Scenario Outline: Add comment to booking and customer profile
    Given the revenue protection comments file placed in hot folder
    When a valid booking reference mentioned
    Then the comments will be added to the Booking with <entries>
    And the comments will be added to the customer profile with <entries>
    Examples:
      | entries                                                                                                                  |
      | Channel (Revenue Protection), User ID (Revenue Protection), Comment type (Free Text), Comment, Created Date / Time Stamp |

  @manual
  @TeamD
  @Sprint31
  @FCPH-10860
  Scenario: Generate error if the Comment is blank
    Given the revenue protection comments file placed in hot folder
    When a Comment is blank for some items
    Then each failed item appear as an individual line in the error report generated
    And each item contains the reason why the data was not able to import
    And each item includes date/time it failed to import
    And continue to process the other valid items on the file

  @manual
  @TeamD
  @Sprint31
  @FCPH-10860
  Scenario: Generate email to inform of errors which not able to load
    Given the revenue protection comments file placed in hot folder
    When there are failed items which are unable to load
    Then send the list of failed items which have been unable to load in an email
    And a CSV file will attached