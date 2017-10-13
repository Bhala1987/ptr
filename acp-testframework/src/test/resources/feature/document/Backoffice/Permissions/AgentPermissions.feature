@TeamA
@Sprint32
@FCPH-11464
@backoffice
@manual

Feature: Set up Agent Permissions : With Admin login

  Scenario Outline: 1 - Able to define capabilities that can be controlled
    Given that I am in the back office with admin login
    When I see the Agent Permissions Set Up
    Then I see the below <capability> that can be controlled

    Examples:

      | capability                      |
      | ManageSignificantOtherDetails   |
      | DeleteCustomerProfile           |
      | GenerateNewPassword             |
      | CorrectPassengerName            |
      | AddNIF                          |
      | ChangePassengerNames            |
      | ChangeNIF                       |
      | ChangeSSRdetails                |
      | AddEJPlusdetails                |
      | ChangePassengerAge              |
      | AddSSRdetails                   |
      | AddPassengerContactDetails      |
      | ChangePassengerContactDetails   |
      | ChangeEJPlusdetails             |
      | AmendAPIS                       |
      | UpdateICTSflag                  |
      | AddAPIS                         |
      | AddRemoveMoveInfantOnLap        |
      | ChangeAPIS                      |
      | ViewBookingComments             |
      | ViewICTSFlag                    |
      | DisplayPassengersDetailsBooking |
      | ViewPaymentHistory              |

  Scenario Outline: 2 - Able to define user group permissions for each capability- Creating new user group permission
    Given that I am in the back office with admin login
    When I see the Agent Permissions Set Up
    And click add principal permissions
    And in an backoffice wizard, select category,capability,usergroup
    Then I can choose "<accessstatus>" as an access status and done to create a new User Group permission

    Examples:
      | accessstatus |
      | ALLOWED      |
      | DENIED       |

  Scenario Outline: 3 - Able to change user group permissions for a capability - Allowed to Denied
    Given that I am in the back office with admin login
    When I see the Agent Permissions Set Up
    And search with "<category>","<capability>","<usergroup>" and an access status "<accessstatus>"
    And I can see agent permission record with valid "<category>","<capability>","<usergroup>","<accessstatus>" in editable mode
    Then I should be able change User Group permissions setup by specifying "<updateaccessstatus>" and save

    Examples:
      | category       | capability              | usergroup        | accessstatus | updateaccessstatus |
      | Manage Booking | PriceOverride           | cctier2          | ALLOWED      | DENIED             |
      | Manage Booking | BookRescueMissedFlight  | itteam           | ALLOWED      | DENIED             |
      | Corporates     | ManageCorporateBookings | cctier2          | ALLOWED      | DENIED             |
      | Corporates     | ManageCorporateBookings | ccgroupseriesb2b | ALLOWED      | DENIED             |
      | Corporates     | ManageCorporateBookings | ccgroupseriesb2b | ALLOWED      | DENIED             |

  Scenario Outline: 4 - Able to change user group permissions for a capability - Denied to Allowed
    Given that I am in the back office with admin login
    When I see the Agent Permissions Set Up
    And search with "<category>","<capability>","<usergroup>" and an access status "<accessstatus>"
    And I can see agent permission record with valid "<category>","<capability>","<usergroup>","<accessstatus>" in editable mode
    Then I should be able change User Group permissions setup by specifying "<updateaccessstatus>" and save

    Examples:
      | category       | capability                | usergroup        | accessstatus | updateaccessstatus |
      | Check In       | CheckInOnBehalfOfCustomer | cctier3          | DENIED       | ALLOWED            |
      | Check In       | CheckInOnBehalfOfCustomer | cctier2          | DENIED       | ALLOWED            |
      | Check In       | CheckInOnBehalfOfCustomer | security         | DENIED       | ALLOWED            |
      | Manage Booking | PriceOverride             | finance          | DENIED       | ALLOWED            |
      | Disruptions    | ViewCrisisLockedFlight    | ccgroupseriesb2b | DENIED       | ALLOWED            |
