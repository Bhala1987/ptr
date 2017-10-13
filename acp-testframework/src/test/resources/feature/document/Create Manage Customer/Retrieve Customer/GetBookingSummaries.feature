Feature: Receive request to provide booking summary for customer

  @TeamD
  @Sprint29
  @FCPH-10359
  Scenario: Generate error if channel is not allowed
    Given the channel ADAirport is used
    And I created a customer
    When I send a getBookingSummaries request
    Then the channel will receive an error with code SVC_100266_1002

  @TeamD
  @Sprint29
  @FCPH-10359
  Scenario: Generate error if customer is not able to be identified
    Given the channel Digital is used
    And I created a customer
    When I send a getBookingSummaries request with incorrect customer id
    Then the channel will receive an error with code SVC_100266_1004

  @TeamD
  @Sprint29
  @FCPH-10359
  Scenario: Generate error if customer is not hard logged in
    Given the channel Digital is used
    And a customer exist in the database
    When I send a getBookingSummaries request
    Then the channel will receive an error with code SVC_100266_1005

  @manual
  @TeamD
  @Sprint29
  @FCPH-10359
  @BR:BR_01020,BR_01040
  Scenario Outline: Return a list of bookings which are associated to the logged in customer based on BRs
    Given the channel Digital is used
    And I created a customer
    And I have the bookings available with the <status>
    When I send a getBookingSummaries request
    Then bookings within 6 months before the last past flight's STD are returned
    And I <result> bookings with any future flights based on status
    Examples:
      | status                                 | result          |
      | Complete (Active)                      | return          |
      | Customer Cancelled                     | return          |
      | Past                                   | return          |
      | Cancelled by Revenue Protection        | will not return |
      | Part Cancelled by Revenue Protection   | will not return |
      | Chargeback - Policy Revenue Protection | will not return |
      | Chargeback - Fraud Revenue Protection  | will not return |