@FCPH-3340
Feature: Update Customer preferences

  Scenario Outline: 1 - Update Customer preferences
    Given I am using channel <Channel>
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    When I update my <Section> customer preferences
    Then I will receive a customer preferences update confirmation
    And the updated values will be returned when retrieving the customer profile
  @regression
    Examples:
      | Channel | Section |
      | Digital | full    |
    Examples:
      | Channel           | Section   |
#      | Digital           | communication |
#      | Digital           | travel        |
#      | Digital           | ancillary     |
#      | ADAirport         | full          |
#      | ADAirport         | communication |
#      | ADAirport         | travel        |
      | ADAirport         | ancillary |
#      | ADCustomerService | full      |
#      | ADCustomerService | communication |
      | ADCustomerService | travel        |
#      | ADCustomerService | ancillary     |
#      | PublicApiB2B      | full          |
#      | PublicApiB2B      | communication |
#      | PublicApiB2B      | travel        |
      | PublicApiB2B      | ancillary |
#      | PublicApiMobile   | full      |
      | PublicApiMobile   | communication |
#      | PublicApiMobile   | travel        |
#      | PublicApiMobile   | ancillary     |

  Scenario Outline: 2.1 - Unable to update full customer preferences
    Given I am using channel <Channel>
    And a valid customer profile has been created
    When I update my full customer preferences with <Invalid condition>
    Then the update customer preferences service returns error: <Error code>
    And I will not update the Customer's profile with the preferences
    Examples:
      | Channel           | Invalid condition                             | Error code      |
      | Digital           | invalid hold bag quantity                     | SVC_100208_2001 |
      | Digital           | invalid seat number                           | SVC_100208_2002 |
      | Digital           | toDate is past fromDate for opt out period    | SVC_100208_2004 |
      | Digital           | toDate is past fromDate for travelling period | SVC_100208_2004 |
      | Digital           | invalid date for opt out period               | SVC_100208_2005 |
      | Digital           | invalid date for travelling period            | SVC_100208_2005 |
      | ADAirport         | invalid season                                | SVC_100208_2006 |
      | Digital           | invalid frequency                             | SVC_100208_2007 |
      | ADCustomerService | invalid opt out marketing                     | SVC_100208_2008 |
      | Digital           | invalid airport code for travellingTo         | SVC_100208_2009 |
      | PublicApiB2B      | invalid airport code for preferred airports   | SVC_100208_2009 |
      | Digital           | missing communication preferences             | SVC_100208_2010 |
      | PublicApiMobile   | missing travel preferences                    | SVC_100208_2011 |
      | Digital           | missing ancillary preferences                 | SVC_100208_2012 |


  Scenario Outline: 2.2 - Unable to update communication customer preferences
    Given I am using channel <Channel>
    And a valid customer profile has been created
    When I update my communication customer preferences with <Invalid condition>
    Then the update customer preferences service returns error: <Error code>
    And I will not update the Customer's profile with the preferences
    Examples:
      | Channel           | Invalid condition                          | Error code      |
      | Digital           | toDate is past fromDate for opt out period | SVC_100208_2004 |
      | ADAirport         | invalid date for opt out period            | SVC_100208_2005 |
      | ADCustomerService | invalid frequency                          | SVC_100208_2007 |
      | PublicApiB2B      | invalid opt out marketing                  | SVC_100208_2008 |


  Scenario Outline: 2.3 - Unable to update travel customer preferences
    Given I am using channel <Channel>
    And a valid customer profile has been created
    When I update my travel customer preferences with <Invalid condition>
    Then the update customer preferences service returns error: <Error code>
    And I will not update the Customer's profile with the preferences
    Examples:
      | Channel           | Invalid condition                             | Error code      |
      | Digital           | toDate is past fromDate for travelling period | SVC_100208_2004 |
      | PublicApiB2B      | invalid date for travelling period            | SVC_100208_2005 |
      | ADAirport         | invalid season                                | SVC_100208_2006 |
      | ADCustomerService | invalid airport code for travellingTo         | SVC_100208_2009 |
      | Digital           | invalid airport code for preferred airports   | SVC_100208_2009 |

  Scenario Outline: 2.4 - Unable to update ancillary customer preferences
    Given I am using channel <Channel>
    And a valid customer profile has been created
    When I update my ancillary customer preferences with <Invalid condition>
    Then the update customer preferences service returns error: <Error code>
    And I will not update the Customer's profile with the preferences
    Examples:
      | Channel           | Invalid condition         | Error code      |
      | ADCustomerService | invalid hold bag quantity | SVC_100208_2001 |
      | PublicApiB2B      | invalid seat number       | SVC_100208_2002 |
