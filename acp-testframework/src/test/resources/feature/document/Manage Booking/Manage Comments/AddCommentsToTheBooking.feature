@FCPH-471
@Sprint25
Feature: Store the comments against the booking

  Scenario Outline: Generate error for invalid request
    Given I am using the channel <channel>
    And I login as agent
    When I have received a valid addComments request and invalid bookingID with <CommentType> and <Comment>
    Then I will receive an error with code 'SVC_100024_1000'
    Examples:
      | channel   | CommentType | Comment              |
      | ADAirport | Passenger   | partial payment done |

  Scenario Outline: Verify the addComments request is working for appropriate channels
    Given I am using the channel <channel>
    And the commit booking is done for the channel <channel> for flight from LTN to ALC
    And I login as agent
    When I have received a valid addComments request for type <type> with comment <comment>
    Then I will return the result <ResultShouldBe> and <error> based on the channel configuration
    Examples:
      | channel   | type      | comment              | ResultShouldBe | error           |
      | ADAirport | Passenger | Test comment entered | SUCCESS        |                 |
      | ADAirport | Booking   | Test comment entered | SUCCESS        |                 |
      | Digital   | Passenger | Test comment entered | FAIL           | SVC_100300_1004 |
      | ADAirport   | "InvalidPassenger" | Test comment entered | FAIL           | SVC_100300_1002 |


  Scenario Outline: Store comments on the booking
    Given I am using the channel <channel>
    And the commit booking is done for the channel <channel> for flight from LTN to ALC
    And I login as agent
    And I have received a valid addComments request for type <type> with comment <comment>
    And I return the result for addComments request
    When I get the booking
    Then I could see the comments added
    Examples:
      | channel   | type      | comment              |
      | ADAirport | Passenger | Test comment entered |
  @regression
    Examples:
      | channel           | type    | comment              |
      | ADCustomerService | Booking | Test comment entered |

