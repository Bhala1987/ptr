@Sprint28
Feature: Change passenger details without triggering additional fees.

  As a passenger on a booked flight
  I want to be able to change my own details
  So that I can update my details

  @FCPH-7733
  Scenario: Update passenger name with less characters then are required for a name change fee.
    Given I am using the channel Digital
    And I have created a new customer
    And I search for flight with 1 adult departing 'before' today plus the configure days with following details
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I added it to the basket with Standard fare as outbound/inbound journey
    And I provide basic passenger details
    When I do commit booking for given basket
    And the booking is amendable
    And I attempt to add infantOnLap to valid passenger
    And 1'st adult on outbound flight requests to update following details:
      | field     | noOfChars |
      | firstname | 1         |
      | lastname  | 2         |
    Then the basket should be updated to include the respective information for all flights with infant
    # And the ICTS status should be "Not Checked" for the adult

  @FCPH-7733
  Scenario Outline: Update passenger title.
    Given I am using the channel Digital
    And I have created a new customer
    And I search for flight with 2 adult; 1 child departing 'before' today plus the configure days with following details
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I added it to the basket with Standard fare as outbound/inbound journey
    And I provide basic passenger details
    When I do commit booking for given basket
    And the booking is amendable
    And 2'nd adult on outbound flight requests to update following details:
      | field | value   |
      | title | <title> |
    Then the basket should be updated to include the respective information for all flights without infant
    # And the ICTS status should be "Not Checked" for the adult

    Examples:
      | title |
      | mr    |
      | mrs   |
      | miss  |

  @FCPH-7733
  Scenario Outline: Update passengers name with invalid alpha values.
    Given I am using the channel <channel>
    And I have created a new customer
    And I search for flight with 2 adult; 1 child departing 'before' today plus the configure days with following details
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I added it to the basket with Standard fare as outbound/inbound journey
    And I provide basic passenger details
    When I do commit booking for given basket
    And the booking is amendable
    When 2'nd adult on outbound flight requests to update following details:
      | field     | noOfChars               |
      | firstname | <firstNameNoOfChars>    |
      | lastname  | <lastNameNumberOfChars> |
    Then I will receive an error with code 'SVC_100012_3023'
    And I will receive an error with code 'SVC_100012_3022'
    Examples:
      | channel   | firstNameNoOfChars | lastNameNumberOfChars |
      | ADAirport | 31                 | 51                    |
      | ADAirport | TOO_SHORT          | TOO_SHORT             |

  @FCPH-7733
  Scenario: Update passengers name with invalid alpha-numeric values.
    Given I am using the channel ADAirport
    And I have created a new customer
    And I search for flight with 2 adult; 1 child departing 'before' today plus the configure days with following details
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I added it to the basket with Standard fare as outbound/inbound journey
    And I provide basic passenger details
    When I do commit booking for given basket
    And the booking is amendable
    And 2'nd adult on outbound flight requests to update following details:
      | field     | noOfChars |
      | firstname | NON_ALPHA |
      | lastname  | NON_ALPHA |
    Then I will receive an error with code 'SVC_100012_3025'

  @FCPH-7734 @TeamA @Sprint30
  Scenario Outline: Test that when a passengers name is changed their SSRs are removed.
    Given I am using the channel <Channel>
    And I have created a new customer
    And I search for flight with 1 adult departing 'before' today plus the configure days with following details
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I added it to the basket with Standard fare as outbound/inbound journey
    And I provide basic passenger details
    When I do commit booking for given basket
    And the booking is amendable
    And passenger "1" adds SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | DPNA |                  | false                     |
      | MAAS |                  | false                     |
    And an SSR with the code "MAAS" should be associated to selected passenger on all flights
    And an SSR with the code "DPNA" should be associated to selected passenger on all flights
    And 1'st adult on outbound flight requests to update following details:
      | field     | noOfChars |
      | <Field>   | 5         |
    Then the selected passenger should have no SSRs in the basket

    Examples:
      | Channel           | Field     |
      | Digital           | firstname |
      | ADAirport         | lastname  |
