@defect:FCPH-10892
Feature: Basic searching for a booking works using valid criteria

#Search parameters are non case sensitive so no validation issue for upper or lower case - See BR_01791
#Validate First Name is not the only Search parameter provided See BR_02120
#Validate Last Name is provided with Title or at least 1 character from First Name. See BR_02130
#Generate a error message as per Booking Search Pod Error Validation when not valid
#Only email, title, firstname, lastname, postcode and travelfromdate included in this Story

  @FCPH-364 @FCPH-369 @FCPH-6952
  Scenario Outline: Searching for an booking with search criteria in such way that there are no result
    When I search for a booking with unmatchable criteria using "<parameters>" and channel "<channel>"
    Then an error is returned saying that no search results match the criteria
    Examples:
      | parameters      | channel           |
      | firstName,title | ADAirport         |
      | lastName,title  | ADCustomerService |
      | postcode        | ADAirport         |
      | email           | ADAirport         |

  @FCPH-364 @FCPH-369 @FCPH-6952
  Scenario: Searching for an booking with missing channel
    When I search for the booking with a missing header "xposid"
    Then the error message is returned informing me that the header is required

  @FCPH-364 @FCPH-369 @FCPH-6952
  Scenario Outline: Search with empty query params
    Given there are valid bookings using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking but with empty "<fields>" using channel "<channel>"
    Then the booking is returned
    Examples:
      | fields                | channel           | mix     |
      | email                 | ADAirport         | 1 Adult |
      | email,referenceNumber | ADCustomerService | 1 Adult |
      | lastName,title        | ADAirport         | 1 Adult |

  @FCPH-364 @FCPH-369 @FCPH-6952
  @manual
  Scenario: All query params are empty
    When I search for a booking with empty query params
    Then an error is returned informing me that no query paramaters were provided

  @FCPH-364 @FCPH-369 @FCPH-6952
  Scenario Outline: Search is not performed when only firstname is provided
    Given there are valid bookings using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<field>" of customer using channel "<channel>"
    Then an <errorCode> is returned informing me that I cannot search by only "<field>"
    Examples:
      | field     | errorCode       | channel           | mix     |
      | firstName | SVC_100144_2002 | ADAirport         | 1 Adult |
      | lastName  | SVC_100144_2001 | ADCustomerService | 1 Adult |
#      | email     | SVC_100012_3028 | ADCustomerService | 1 Adult |

  @FCPH-364 @FCPH-369 @FCPH-6952
  Scenario Outline: Search with lastname requires at least one char in firstname
    Given there are valid bookings using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "lastName" and "<firstNameCharacters>" characters of the firstname using channel "<channel>"
    Then the booking "<returns>" is returned
    Examples:
      | firstNameCharacters | returns | channel   | mix     |
      | 0                   | error   | ADAirport | 1 Adult |
      | 1                   | result  | ADAirport | 1 Adult |
      | 2                   | result  | ADAirport | 1 Adult |

  @manual
  @FCPH-364 @FCPH-369 @FCPH-6952
  Scenario Outline: Search with different casing returns results
    Given there are valid bookings using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<parameters>" in "<case>" and channel "<channel>"
    Then the booking is returned
    Examples:
      | parameters                       | case  | channel           | mix     |
      | email                            | mixed | ADAirport         | 1 Adult |
      | firstName, lastName              | camel | ADCustomerService | 1 Adult |
      | firstName, title                 | upper | ADAirport         | 1 Adult |
      | title, lastName                  | mixed | ADCustomerService | 1 Adult |
      | title, firstName, lastName       | camel | ADCustomerService | 1 Adult |
      | referencenumber                  | upper | ADAirport         | 1 Adult |
      | referencenumber, title           | mixed | ADAirport         | 1 Adult |
      | referencenumber, title, lastName | lower | ADCustomerService | 1 Adult |
      | referencenumber, lastName        | mixed | ADAirport         | 1 Adult |
      | referencenumber, firstName       | lower | ADCustomerService | 1 Adult |
      | email, lastName                  | camel | ADAirport         | 1 Adult |
      | email, referencenumber           | upper | ADCustomerService | 1 Adult |

  @FCPH-364 @FCPH-369 @FCPH-6952
  Scenario Outline: Search with invalid query params for the channel
    Given there are valid bookings using channel "<Channel>" and passenger mix "<Mix>"
    When I search for the booking for channel "<Channel>" using invalid parameter
    Then an error is returned for the wrong parameter
    Examples:
      | Channel           | Mix     |
      | Digital           | 1 Adult |
      | ADAirport         | 1 Adult |
      | ADCustomerService | 1 Adult |
      | PublicApiB2B      | 1 Adult |
      | PublicApiMobile   | 1 Adult |

  @Sprint28
  @FCPH-368 @ADTeam
  Scenario Outline: Validate Basic Search Request, search criteria combination not valid
    Given there are valid bookings with passenger APIS using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<field>" of traveller "true"
    Then an "<errorCode>" is returned for booking search
    Examples:
      | field                | errorCode       | channel           | mix     |
      | dob, firstName       | SVC_100144_2002 | ADAirport         | 1 Adult |
      | dob, lastName        | SVC_100144_2001 | ADCustomerService | 1 Adult |
      | travelDocumentType   | SVC_100144_2010 | ADCustomerService | 1 Adult |
      | travelDocumentNumber | SVC_100144_2009 | ADCustomerService | 1 Adult |

  @Sprint28
  @FCPH-368 @ADTeam
  Scenario Outline: Validate Basic Search Request, Travel Document Type not known
    Given there are valid bookings with passenger APIS using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking of traveller "true" with an invalid "<value>" for "<field>"
    Then an "<errorCode>" is returned for booking search
    Examples:
      | field              | value | errorCode       | channel   | mix     |
      | travelDocumentType | DL    | SVC_100144_2010 | ADAirport | 1 Adult |

  @Sprint28
  @FCPH-368 @ADTeam
  Scenario Outline: Validate Basic Search Request, Travel Dates invalid
    Given there are valid bookings with passenger APIS using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking of traveller "true" with an invalid travel dates "<field>"
    Then an "<errorCode>" is returned for booking search
    Examples:
      | field                        | errorCode       | channel   | mix     |
      | travelfromdate, traveltodate | SVC_100144_2011 | ADAirport | 1 Adult |

  @Sprint28
  @FCPH-368 @ADTeam
  Scenario Outline: Retrieve bookings based on criteria supplied - no Travel To Date
    Given there are valid bookings with passenger APIS using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<field>" of traveller "true"
    Then the bookings matching the search criteria based on search parameters "<field>" are returned
    Examples:
      | field                               | channel   | mix     |
      | firstName, lastName, travelfromdate | ADAirport | 1 Adult |

  @Sprint28
  @FCPH-368 @ADTeam
  Scenario Outline: Retrieve bookings based on criteria supplied - Document Type and Document Reference
    Given there are valid bookings with passenger APIS using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<field>" of traveller "true"
    Then the bookings matching the search criteria based on search parameters "<field>" are returned
    Examples:
      | field                                                         | channel   | mix     |
      | firstName, lastName, travelDocumentType, travelDocumentNumber | ADAirport | 1 Adult |

  @Sprint28
  @FCPH-368 @ADTeam
  Scenario Outline: Retrieve bookings based on criteria supplied - DOB and Passenger Name
    Given there are valid bookings with passenger APIS using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<field>" of traveller "true"
    Then the bookings matching the search criteria based on search parameters "<field>" are returned
    Examples:
      | field                    | channel   | mix     |
      | firstName, lastName, dob | ADAirport | 1 Adult |

  @Sprint28
  @FCPH-368 @ADTeam
  Scenario Outline: Retrieve bookings based on criteria supplied - Both Customer and Passenger Name
    Given I have a booking with customer is travelling using channel "<channel>" and passenger mix "<mix>"
    And I have a booking with customer is not travelling using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<field>" of traveller "true" and booker "true"
    Then the booking is returned
    Examples:
      | field                                  | channel   | mix     |
      | firstName, lastName, dob               | ADAirport | 1 Adult |
      | firstName-booker, lastName-booker, dob | ADAirport | 1 Adult |

