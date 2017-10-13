@TeamD
@Sprint31
Feature: Setup and Send TermsAndCondition URL to channel

  @backoffice:FCPH-10205
  Scenario: Create URL for TermsAndConditions in the back office
    Given that I'm on the TermsAndConditions folder
    When I select to create a TermsAndConditions
    Then I must enter a code, URL name, Description
    And I can enter a URL Name in more than one language
    And I can enter Description in more than one language

  @backoffice:FCPH-10205
  Scenario: Update URL for TermsAndConditions in the back office
    Given that I'm on the TermsAndConditions folder
    When I select to update a TermsAndConditions
    Then I can amend a code, URL name, Description
    And code cannot be amended

  @backoffice:FCPH-10205
  Scenario:  Store the creation/modifications date & time for URL
    Given that I'm on the TermsAndConditions folder
    When I create TermsAndConditions
    Then the creation/modification date, time and user ID will be stored

  @FCPH-10205
  Scenario Outline: Return a list of language specific TermsAndConditions URL
    Given one of this channel ADAirport, Digital is used
    And the header contains acceptLanguage = <language>
    When I sent a request to getTermsAndConditions service
    Then I should receive the list of terms and conditions in the requested language
    Examples:
      | language |
      | en       |
      | fr       |

  @FCPH-10205
  Scenario: Return a list of all T&C URL's
    Given one of this channel ADAirport, Digital is used
    When I sent a request to getTermsAndConditions service
    Then I should receive the list of terms and conditions in the requested language
