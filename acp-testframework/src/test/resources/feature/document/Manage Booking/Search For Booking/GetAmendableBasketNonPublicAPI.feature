@FCPH-2717
@Sprint25
@Sprint26
Feature: Create Amendable Booking to the Channel - not Public API

  Scenario Outline: Receive Get Amendable Basket Request
    Given I am using channel <channel>
    And I create a "COMPLETED" status booking for "1 Adult, 1 Child, 1 Infant OL"
    When I request an amendable basket for a booking
    Then I should receive amendable basket
  @regression
    Examples:
      | channel |
      | Digital |
    Examples:
      | channel           |
      | ADCustomerService |

  Scenario Outline: Error when there is an invalid booking reference
    Given I am using channel <channel>
    When I request an amendable basket for an invalid booking reference
    Then I should receive an error "SVC_100245_3001"
    Examples:
      | channel           |
      | Digital           |
      | ADCustomerService |

  @TeamA @Sprint29 @FCPH-10125
  Scenario: Test that linked flights are returned when a booking is made amendable.
    Given I am using channel Digital
    And I create a "COMPLETED" status return booking for "1 Adult, 1 Child, 1 Infant OL"
    When I request an amendable basket for a booking
    Then I should receive amendable basket
    And the basket should contain linked flights

  @manual
  Scenario Outline: Receive Get Amendable Basket Request
    Given I am using channel <channel>
    And I create a "<status>" status booking for "1 Adult"
    When I request an amendable basket for a booking
    Then I should receive amendable basket
    Examples:
      | status                              | channel           |
      | Past                                | ADAirport         |
      | Pending Cancellation                | ADAirport         |
      | Chargeback – Policy Rev. Protection | ADCustomerService |
      | Chargeback – Fraud Rev. Protection  | ADCustomerService |

  @manual
  Scenario Outline: Receive an error if channel and status combination not allowed
    Given I am using channel <channel>
    And I create a "<status>" status booking for "1 Adult"
    When I request an amendable basket for a booking
    Then I should receive an error "SVC_100245_3002"
    Examples:
      | status                              | channel           |
      | Customer Cancelled                  | ADAirport         |
      | Revenue Cancelled                   | ADAirport         |
      | Customer Cancelled                  | ADCustomerService |
      | Revenue Cancelled                   | ADCustomerService |
      | Customer Cancelled                  | Digital           |
      | Revenue Cancelled                   | Digital           |
      | Past                                | Digital           |
      | Pending Cancellation                | Digital           |
      | Chargeback – Policy Rev. Protection | Digital           |
      | Chargeback – Fraud Rev. Protection  | Digital           |