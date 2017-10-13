Feature: Validate APIs for a passenger in the basket
  @FCPH-292 @Sprint26
  Scenario Outline: 1 - Validate APIs for a passenger in the basket
    Given I am using <channel> channel
    And I have sectors set in backoffice with APIs required <isApis>
    And I have a basket with a valid flight with 1 adult added via <channel>
    When  I do the booking
    Then  I do the apisRequired to <isApis> against the flights on the booking
    And   I will set the APIS <status> for the passenger on the booking
    Examples:
      | channel           | status | isApis |
      | Digital           | RED    | true   |
      | PublicApiMobile   | Green    | true   |
      | ADAirport         | RED    | true   |
      | ADCustomerService | inapplicable    | true   |

#commented as its duplicate scenarios
#
#      | Digital           | Green  | true   |
#      | PublicApiMobile   | Green  | true   |
#      | ADAirport         | Green  | true   |
#      | ADCustomerService | Green  | true   |
#
#
#      |Digital          |  inapplicable    |False   |
#      |PublicApiMobile  |  inapplicable    |False   |
#      |ADAirport        |  inapplicable    |False   |
#      |ADCustomerService|  inapplicable    |False   |

  @Sprint26
  @FCPH-292
  Scenario Outline: 2 -  Validate APIs for a passenger in the basket PublicApi
    Given I am using <channel> channel
    And I have sectors set in backoffice with APIs required <isApis>
    When I do the booking with valid basket content for <channel>
    Then a booking reference is returned
    And  I do the apisRequired to <isApis> against the flights on the booking
    And   I will set the APIS <status> for the passenger on the booking
    Examples:
      | channel      | status | isApis |
      | PublicApiB2B | RED    | true   |
      | PublicApiB2B | Green  | true   |
      | PublicApiB2B | inapplicable | False  |

  @Sprint27 @FCPH-7737
  Scenario Outline: Save Travel Document
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    When I send a request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then I will check Passenger Status and APIS
    Examples: Save Travel Document to passenger on a single flight BR_01860, BR_01873
      | channel           | passenger | condition | fareType | flight | fieldToChange | typePassenger |
      | Digital           | 1 adult   | true      | Standard | 1      |               | adult         |
      | ADCustomerService | 1 adult   | false     | Flexi    | 1      |               | adult         |
    Examples: Save Travel Document to passenger's other flights - no errors
      | channel           | passenger | condition | fareType | flight | fieldToChange | typePassenger |
      | Digital           | 1 adult   | true      | Standard | 2      | IdentityCard  | adult         |

  @Sprint27 @FCPH-7737
  Scenario Outline: Update field on the apis booking
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    When I send a request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then I <should> get a warning <warning> just on desired passenger
    Examples: Update infant or adult name on the booking if different from the one on the Travel Document given for APIS BR_00137
      | channel           | passenger           | condition | fareType | flight | fieldToChange      | typePassenger | should | warning         |
      | Digital           | 1 adult             | true      | Flexi    | 1      | DifferentName      | adult         | true   | SVC_100201_2002 |
      | ADCustomerService | 1 adult; 1 child    | true      | Standard | 1      | DifferentName      | child         | true   | SVC_100201_2002 |
      | PublicApiMobile   | 1 adult; 1,1 infant | true      | Standard | 1      | DifferentName      | infant        | false  |                 |
    Examples: Generate Travel Document expiry date before STD warning message BR_00134
      | channel           | passenger           | condition | fareType | flight | fieldToChange      | typePassenger | should | warning         |
      | Digital           | 1 adult             | true      | Standard | 1      | ValidExpiredDate   | adult         | true   | SVC_100201_2001 |
      | PublicApiMobile   | 1 adult             | true      | Flexi    | 1      | InvalidExpiredDate | adult         | true   | SVC_100201_2001 |






























  
  
  
  
  
  
  
  
  
  