@Sprint32
@FCPH-10591
@TeamA
Feature: Apply Manage Booking Permissions for View Booking Details and Change Passenger Details

Given that the channel has initiated a manage booking action

#Scenario: Not able to perform an action based on Channel not being allowed
  Scenario Outline: that the channel has initiated a manage booking actio

    Given I am using <channel> channel
    When I commit a booking with <fareType> fare and <passenger> passenger
    And attempt to update below details with <invalidchannel>,"<bookingtype>","<accesstype>" for couple of passenger
      | name |
    Then I will determine the booking is not editable for the channel "<invalidchannel>"

    Examples:
      | channel | invalidchannel | fareType | passenger | bookingtype | accesstype |
      | Digital | AD Airport            | Standard | 3 Adult   | null        | Booker     |

  #Scenario: Not able to perform an action based on Booking Type not being allowed
  @manual
  Scenario Outline: that the channel has initiated a manage booking action
    Given I am using <channel> channel
    When I commit a booking with <fareType> fare and <passenger> passenger
    And I create amendable basket for the booking created
    And attempt to update below details with "<channel>","<invalidbookingtype>","<accesstype>" for couple of passenger
      | name |
    And the action is not allowed for the "<invalidbookingtype>"
    Then I will determine the booking is not editable for the bookingtype "<invalidbookingtype>"

    Examples:
      | channel  | fareType | passenger | invalidbookingtype | accesstype |
      | Digitacl | Standard | 3 Adult   | booktype           | Booker     |

    #Scenario: Not able to perform an action based on Access Type not being allowed
  @manual
  Scenario Outline: that the channel has initiated a manage booking action
    Given I am using <channel> channel
    When I commit a booking with <fareType> fare and <passenger> passenger
    And I create amendable basket for the booking created
    And attempt to update below details with "<channel>","<bookingtype>","<invalidaccesstype>" for couple of passenger
      | name |
    And the action is not allowed for the "<invalidaccesstype>"
    Then I will determine the booking is not editable for the accesstype "<invalidaccesstype>"

    Examples:
      | channel | fareType | passenger | bookingtype | invalidaccesstype |
      | Digital | Standard | 3 Adult   | null        | 3rdParty          |
