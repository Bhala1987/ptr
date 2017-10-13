@Sprint27 @FCPH-6061
Feature: FCPH-6061 - Remove Booking Comments.
  As an authenticated agent user
  I should be able to delete comments
  So that I can manage and moderate comments appropriately

  Scenario: Test that an error is returned if the booking reference cannot be found.
    Given I am using the channel Digital
    And I login as agent
    When I delete any comment from an invalid booking
    Then I will receive an error with code 'SVC_100300_1001'

  Scenario: Test that an error is returned if the comment ID cannot be found within the booking.
    Given I am using the channel ADAirport
    And I login as agent
    When I attempt to delete a comment that doesn't exist from the booking
    Then I will receive an error with code 'SVC_100305_1001'

  Scenario Outline: Test that error is returned if the channel is unable to delete a comment due to channel restrictions.
    Given I am using the channel <Channel>
    And the commit booking is done for the channel <Channel> for flight from LTN to ALC
    And I login as agent
    And I have received a valid addComments request for type <CommentType> with comment <Comment>
    And I return the result for addComments request
    And I am using <Channel> channel
    When I delete the added comment from the booking
    Then I will receive an error with code 'SVC_100305_1002'

    Examples:
      | Channel | CommentType | Comment              |
      | Digital | Passenger   | Test comment entered |
      | Digital | Booking     | Test comment entered |

  Scenario Outline: Delete a comment from a booking
    Given I am using the channel <Channel>
    And the commit booking is done for the channel <Channel> for flight from LTN to ALC
    And I login as agent
    And I have received a valid addComments request for type <CommentType> with comment <Comment>
    And I return the result for addComments request
    When I delete the added comment from the booking
    Then the comment should be removed from the booking

    Examples:
      | Channel   | CommentType | Comment              |
      | ADAirport | Passenger   | Test comment entered |
      | ADAirport | Booking     | Test comment entered |