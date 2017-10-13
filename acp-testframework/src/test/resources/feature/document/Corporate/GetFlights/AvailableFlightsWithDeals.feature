@FCPH-380 @FCPH-308 @FCPH-2676 @FCPH-200
Feature: Retrieve and Return Available Direct Flights with Deal found

  Scenario Outline: Deal found has at least one bundle with a discount tier associated
    Given I found a deal with <AppId>, <OffId>, <CorpId>
    When I call the find flights service with deal headers <AppId>, <OffId>, <CorpId> via <channel>
    Then flight is returned
    And I should see discounts for flight search
    And I should see total discounts for flight search
  @regression
    Examples:
      | channel      | AppId | OffId | CorpId |
      | PublicApiB2B | Valid | Valid | Valid  |
    Examples:
      | channel           | AppId | OffId   | CorpId |
      | ADCustomerService | Valid | Invalid |        |
      | ADAirport         | Valid | Valid   | Valid  |
      | ADCustomerService | Valid | Valid   |        |

  Scenario Outline: Deal found has at least one bundle with NO discount tier associated
    Given I found a deal with <AppId>, <OffId>, <CorpId>
    When I call the find flights service with deal headers <AppId>, <OffId>, <CorpId> via <channel>
    Then flight is returned
    And I should not see discounts for flight search
    And I should not see total discounts for flight search
    Examples:
      | channel   | AppId   | OffId   | CorpId  |
      | ADAirport | Invalid | Invalid | Invalid |

  Scenario Outline: Hybris returns error if one of the header is missing.
    Given I found a deal with <AppId>, <OffId>, <CorpId>
    When I call the find flights service with deal headers <AppId>, <OffId>, <CorpId> via <channel>
    Then "<ErrorMessage>" error is returned
    Examples:
      | channel           | AppId | OffId | CorpId | ErrorMessage    |
      | ADAirport         | Valid |       |        | SVC_100148_2014 |
      | ADCustomerService |       | Valid |        | SVC_100148_2014 |
      | Digital           |       |       | Valid  | SVC_100148_2014 |
      | ADAirport         | Valid |       | Valid  | SVC_100148_2014 |

  Scenario Outline: Hybris returns additional information if deal is not present
    Given I found a deal with <AppId>, <OffId>, <CorpId>
    When I call the find flights service with deal headers <AppId>, <OffId>, <CorpId> via <channel>
    Then flights deal <WarningMessage> should be returned
    Examples:
      | channel           | AppId   | OffId   | CorpId  | WarningMessage  |
      | ADAirport         | Valid   | Invalid |         | SVC_100148_2015 |
      | ADCustomerService | Invalid | Invalid |         | SVC_100148_2016 |
      | Digital           | Valid   | Valid   | Invalid | SVC_100148_2017 |
      | PublicApiB2B      | Valid   | Invalid | Invalid | SVC_100148_2018 |
      | ADAirport         | Invalid | Invalid | Valid   | SVC_100148_2019 |