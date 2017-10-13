@Sprint27
@FCPH-406
Feature: Get combined APIs from legacy API and Hybris

#  Please Note:
#  1. Using the customer <Customer ID: cus00000001, email: "a.rossi@reply.co.uk", password: "1234"> mostly to test this story on overall
#  2. Assuming the above customer has the golden Legacy data setup in AL for saved documents:
#      A. "documentNumber": "333333", "documentType": "ID_CARD"
#      B. "documentNumber": "1111111", "documentType": "PASSPORT"
#      C. "documentNumber": "1111111X", "documentType": "PASSPORT"
#  3. We can use the following end point to retrive the APIs for any customer from Legacy AL (Please Replace an email with the email we want to get):
#     http://s101-al-web07.fcp.easyjet.local:1235/commercial-cpm/v1/customers/a.rossi@reply.co.uk/legacy-saved-passengers/*/advance-passenger-information

  Scenario: Generate error when trying to get APIs for an invalid customer
    Given am using channel <channel>
    When I use an invalid customer invalidcustomer
    And I request APIs for that customer
    Then I should receive an error

  Scenario Outline: Return Error if the channel is not allowed to request
    Given am using channel <channel>
    And the customer cus00000001 has legacy APIs in AL
    When I request APIs for that customer
    Then I should return with no apis but an error "SVC_100200_2001"
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |

  @regression
  Scenario: Return APIs from Hybris only if we do not have any APIs in Legacy AL
    Given am using channel Digital
    And I have created a new customer
    But that customer has no Legacy APIs in AL
    And I have added new API as follows
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName  |
      | 1986-12-12  | 2050-12-12         | IN000001       | PASSPORT     | MALE   | GBR         | GBR            | full name |
      | 1986-12-12  | 2050-12-12         | IN000002       | PASSPORT     | MALE   | GBR         | GBR            | full name |
    When I request APIs for that customer
    Then I should receive the following APIs
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName  |
      | 1986-12-12  | 2050-12-12         | IN000001       | PASSPORT     | MALE   | GBR         | GBR            | full name |
      | 1986-12-12  | 2050-12-12         | IN000002       | PASSPORT     | MALE   | GBR         | GBR            | full name |

  Scenario: Return combined APIs from Hybris and Legacy AL
    Given am using channel Digital
    And the customer a.rossi@reply.co.uk and 1234 is logged in
    And has following legacy API in AL
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName               |
      | 2000-10-22  | 2020-10-22         | 333333         | ID_CARD      | MALE   | ITA         | ITA            | legacy_Alionel Alionel |
      | 2000-10-22  | 2020-10-22         | 1111111        | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne    |
      | 2000-10-22  | 2020-10-22         | 1111111X       | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne    |
    And no APIs in hybris for the same customer
    When I add new API as follows
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName  |
      | 1986-12-12  | 2050-12-12         | IN000001       | PASSPORT     | MALE   | GBR         | GBR            | full name |
      | 1986-12-12  | 2050-12-12         | IN000002       | PASSPORT     | MALE   | GBR         | GBR            | full name |
    When I request APIs for that customer
    Then I should receive the combined following APIs
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName               |
      | 2000-10-22  | 2020-10-22         | 333333         | ID_CARD      | MALE   | ITA         | ITA            | legacy_Alionel Alionel |
      | 2000-10-22  | 2020-10-22         | 1111111        | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne    |
      | 2000-10-22  | 2020-10-22         | 1111111X       | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne    |
      | 1986-12-12  | 2050-12-12         | IN000001       | PASSPORT     | MALE   | GBR         | GBR            | full name              |
      | 1986-12-12  | 2050-12-12         | IN000002       | PASSPORT     | MALE   | GBR         | GBR            | full name              |

  Scenario: Remove duplicates before returning the combined APIs
    Given am using channel Digital
    And the customer a.rossi@reply.co.uk and 1234 is logged in
    And has following legacy API in AL
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName            |
      | 2000-10-22  | 2020-10-22         | 1111111        | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne |
    And no APIs in hybris for the same customer
    When I add duplicate API as follows
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName    |
      | 2000-01-12  | 2080-01-12         | 1111111        | PASSPORT     | MALE   | GBR         | GBR            | Hybris name |
    When I request APIs for that customer
    Then I should receive no duplicates
    And for the duplicate records information from hybris is returned
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName    |
      | 2000-01-12  | 2080-01-12         | 1111111        | PASSPORT     | MALE   | GBR         | GBR            | Hybris name |

  @OnlyToLoginUseDigital
  Scenario Outline: Return expired documents for allowed channels
    Given am using channel <channel>
    And the customer a.rossi@reply.co.uk and 1234 is logged in
    And no APIs in hybris for the same customer
    And I add the following expired API in hybris
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName    |
      | 1990-01-10  | 2000-01-12         | 2222222        | PASSPORT     | MALE   | GBR         | GBR            | namee namee |
    When I request APIs for that customer
    Then I should receive the following expired API
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName    |
      | 1990-01-10  | 2000-01-12         | 2222222        | PASSPORT     | MALE   | GBR         | GBR            | namee namee |
    Examples:
      | channel         |
      | Digital         |
      | PublicApiMobile |
      | PublicApiB2B    |

  Scenario Outline: Do not Return Expired APIs if the channel is not allowed to request
    Given am using channel <channel>
    And using the customer cus00000001
    And no APIs in hybris for the same customer
    And I add the following expired API in hybris
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName    |
      | 1990-01-10  | 2000-01-12         | 2222222        | PASSPORT     | MALE   | GBR         | GBR            | namee namee |
    When I request APIs for that customer
    Then I should return with no apis but an error "SVC_100200_2001"
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |

#  Following is the set of API data we have already in AL and Hybris.

# Documents for cus00000001, in Legacy AL
#      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName               |
#      | 2000-10-22  | 2020-10-22         | 333333         | ID_CARD      | MALE   | ITA         | ITA            | legacy_Alionel Alionel |
#      | 2000-10-22  | 2020-10-22         | 1111111        | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne    |
#      | 2000-10-22  | 2020-10-22         | 1111111X       | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne    |

  # Documents for cus00000001, in Hybris
#      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName            |
#      | 2000-01-12  | 2080-01-12         | 1111111        | PASSPORT     | MALE   | GBR         | GBR            | lname name          |
#      | 1990-01-10  | 2000-01-12         | 2222222        | PASSPORT     | MALE   | GBR         | GBR            | namee namee         |
#      | 2000-01-12  | 2080-01-12         | 3333333        | PASSPORT     | MALE   | GBR         | GBR            | nameeee nameee      |

   # Duplicate Documents for cus00000001, in Hybris and Legacy AL
#           | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName            |
#  In Hybris| 2000-01-12  | 2080-01-12         | 1111111        | PASSPORT     | MALE   | GBR         | GBR            | lname name          |
#  In AL    | 2000-10-22  | 2020-10-22         | 1111111        | PASSPORT     | MALE   | ITA         | ITA            | legacy_Ajohn Awayne |
