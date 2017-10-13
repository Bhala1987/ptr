Feature: Message of the Day set up

@TeamD
@Sprint31
@backoffice:FCPH-10881
Scenario: Create Message of the day
Given that I'm on the Message of the day folder
When I select to create a Message of the day
Then I can enter the message of the day
And I can enter a start date and time

@TeamD
@Sprint31
@backoffice:FCPH-10881
Scenario: Update an existing message of the day
Given that I'm on the Message of the day folder
When I select to update a message of the day
Then I can update the message of the day

@TeamD
@Sprint31
@backoffice:FCPH-10881
Scenario: Record creation date and time of message of the day
Given that I'm on the message of the day folder
When I create message of the day
Then the creation date, time and user ID will be stored
