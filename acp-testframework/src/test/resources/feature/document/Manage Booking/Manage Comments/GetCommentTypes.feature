Feature: Get Comment Types

  @Sprint28 @FCPH-9252
  Scenario Outline: List of comment contexts / types
    Given I am using the channel <channel>
    When I make a request to getCommentTypes
    Then I should get list of <commentTypes> for <commentContext>
  @ADTeam
  @regression
    Examples:
      | channel           | commentContext | commentTypes             |
      | ADAirport         | BOOKING        | Booking,Passenger,Flight |
      | ADCustomerService | CUSTOMER       | Customer                 |
    Examples:
      | channel         | commentContext | commentTypes             |
      | Digital         | BOOKING        | Booking,Passenger,Flight |
      | PublicApiB2B    | BOOKING        | Booking,Passenger,Flight |
      | PublicApiMobile | CUSTOMER       | Customer                 |
      | PublicApiB2B    | CUSTOMER       | Customer                 |

  @Sprint28 @FCPH-9252
  Scenario Outline: Particular comment context
    Given I am using the channel <channel>
    When I make a request to getCommentTypes for a <commentContext>
    Then I should get <commentContext> only
  @ADTeam
    Examples:
      | channel           | commentContext |
      | ADAirport         | BOOKING        |
      | ADCustomerService | CUSTOMER       |
    Examples:
      | channel         | commentContext |
      | PublicApiB2B    | BOOKING        |
      | Digital         | BOOKING        |
      | PublicApiMobile | CUSTOMER       |
      | PublicApiB2B    | CUSTOMER       |

  @Sprint28 @FCPH-9252
  Scenario Outline: Error code for an invalid comment context
    Given I am using the channel <channel>
    When I make a request to getCommentTypes for a invalid commentContext
    Then I will receive an error with code 'SVC_100541_300'
  @ADTeam
    Examples:
      | channel   |
      | ADAirport |
    Examples:
      | channel      |
      | Digital      |
      | PublicApiB2B |