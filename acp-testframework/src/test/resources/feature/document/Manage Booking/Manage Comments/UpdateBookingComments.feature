@Sprint29 @FCPH-6041 @TeamD @ADTeam
Feature: ACP Update Booking Comments from Channel

  Scenario Outline: Generate an error message if the booking is unable to be identified
    Given I am using the channel <channel>
    And I login as agent
    When I update a comment from an invalid booking with <CommentType> and <Comment>
    Then I will receive an error with code 'SVC_100219_1001'
    Examples:
      | channel   | CommentType | Comment              |
      | ADAirport | Passenger   | partial payment done |

  Scenario Outline: Generate error message if the comment ID is not associated to the requested booking id
    Given I am using the channel <channel>
    And the commit booking is done for the channel <channel> for flight from LTN to ALC
    And I login as agent
    When I attempt to update a comment that doesn't exist from the booking with <CommentType> and <Comment>
    Then I will receive an error with code 'SVC_100219_1002'
    Examples:
      | channel   | CommentType | Comment              |
      | ADAirport | Passenger   | partial payment done |

  Scenario Outline: Error message when channel not allowed to update a comment BR_01941, BR_01950
    Given I am using the channel <Channel>
    And the commit booking is done for the channel <Channel> for flight from LTN to ALC
    And I login as agent
    And I have received a valid addComments request for type <CommentType> with comment <Comment>
    And I return the result for addComments request
    And I am using <ChannelForUpdate> channel
    When I update the added comment from the booking with <CommentType> and <Comment>
    Then I will receive an error with code 'SVC_100300_1004'
    Examples:
      | Channel   | ChannelForUpdate | CommentType | Comment              |
      | ADAirport | Digital          | Passenger   | partial payment done |
      | ADAirport | Digital          | Booking     | partial payment done |

  @manual
  Scenario Outline: Update the comment on the booking and verify audit entries in the backoffice
    Given I am using the channel <Channel>
    And the commit booking is done for the channel <Channel> for flight from LTN to ALC
    And I login as agent
    And I have received a valid addComments request for type <CommentType> with comment <Comment>
    And I return the result for addComments request
    When I update the added comment from the booking with <CommentType> and <UpdateComment>
    Then the comment should be updated on the booking.
    And I will store Channel, User ID, comment type, Free Text Comment, created Date Time Stamp
    Examples:
      | Channel   | CommentType | Comment              | UpdateComment     |
      | ADAirport | Passenger   | partial payment done | full payment done |
      | ADAirport | Booking     | partial payment done | full payment done |

  @ADTeam
  @BR:BR_01940
  Scenario Outline: Update the comment on the booking and return updated comment in get booking
    Given I am using the channel <Channel>
    And the commit booking is done for the channel <Channel> for flight from LTN to ALC
    And I login as agent
    And I have received a valid addComments request for type <CommentType> with comment <Comment>
    And I return the result for addComments request
    And I get the booking
    When I update the added comment from the booking with <CommentType> and <UpdateComment>
    Then the comment should be updated on the booking.
  @regression
    Examples:
      | Channel   | CommentType | Comment              | UpdateComment     |
      | ADAirport | Passenger   | partial payment done | full payment done |
    Examples:
      | Channel   | CommentType | Comment              | UpdateComment     |
      | ADAirport | Booking     | partial payment done | full payment done |





