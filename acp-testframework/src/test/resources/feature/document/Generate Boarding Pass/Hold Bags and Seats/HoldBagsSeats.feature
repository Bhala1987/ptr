@FCPH-9848
@Sprint27
Feature: Boarding pass
  @manual
  Scenario: 6 - Special Assistance Icon BR_01435
    Given that I have received a valid boarding pass request
    When the requested passenger has a SSR codes linked to them
    And the SSR code has special Assistance flag set to true
    Then add special assistance icon on boarding pass
    And SSR Content  slot

  @manual
  Scenario Outline: 7 - Boarding pass for APIS routes
    Given that I have received a valid boarding pass request
    When the sector requires APIS
    Then the submitted document ID number and document type is shown on the boarding pass
    Examples:
    |Document type   |Document type and ID displayed|
    |Passport        |123456789 (P)                 |
    |Identity Card   |123456789 (I)                 |
    |Group Passport  |123456789 (G)                 |
    |Refugee document|123456789 (R)                 |

  @manual
  Scenario: 8 - Boarding pass for Purchased seat
    Given that I have received a valid boarding pass request
    When the passenger has purchased a seat on the flight
    Then Purchased seat icon is added to the boarding pass

  @manual
  Scenario: 9 - Boarding pass for hold items
    Given that I have received a valid boarding pass request
    When the passenger have hold items
    Then hold component will be added on the boarding pass with Total Number of Hold Bags
    And Total Weight of all Hold Bags plus Excess Weight in KG
    And Sports Equipment Name per type
    And Total Number of Sports Equipment per type and Cabin Bag(s)

  @manual
  Scenario: 11 - Boarding pass pdf generation
    Given that I have received a valid boarding pass request
    When I generate the boarding pass
    Then store the boarding pass in a shared file system
    And generate URL to the location of the boarding pass
    And generate version for  boarding pass

  @manual
  Scenario: 12 - Send URL to the channel
    Given that I have received a valid boarding pass request
    When I generate the boarding pass
    Then I will send the URL to the channel to the location of the boarding pass

  @manual
  Scenario: 13 - store boarding pass details for flight against the booking
    Given that I have received a valid boarding pass request
    When the boarding pass has been generated
    Then I will store date and time generated
    And Version number
    And Channel it was generated from (eg web, mobile, airport)
    And User ID who  generated it (eg customer id, agent id)
    And Action which generated the boarding pass (Check-in, reprint)
    And Flight key the boarding pass was generated for
