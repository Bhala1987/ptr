@FCPH-10057
@TeamA
@Sprint32
Feature: Generate event when a passenger Details has changed

  @regression
  Scenario Outline: Generate event to downstream systems when a passenger Title, Name and Age has changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger <passengerIndex> requests to update their last name to <surname> and first name to <name> and age to <age>
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers | passengerIndex | name         | surname     | age |
      | 1 Adult    | 1              | Alessandro   | Del Piero   | 25  |
      | 2 Adult    | 1              | Alessandro   | Del Piero   | 10  |
      | 2 Adult    | 1              | Alessandrino | Del Pierino | 1   |

  Scenario Outline: Generate event to downstream systems when a passenger Title, Name and Age has changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger <passengerIndex> requests to update their last name to <surname> and first name to <name> and age to <age>
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers           | passengerIndex | name       | surname   | age |
      | 1 Adult, 1 Infant OL | 2              | Alessandro | Del Piero | 25  |

  @regression
  Scenario Outline: Generate event to downstream systems when a passenger Nif number has changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger <passengerIndex> requests to update their nifNumber to <nif>
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers | passengerIndex | nif        |
      | 1 Adult    | 1              | 0987654321 |

  @regression
  Scenario Outline: Generate event to downstream systems when a passenger telephone number has changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger <passengerIndex> requests to update their telephone to <telephone>
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers | passengerIndex | telephone     |
      | 1 Adult    | 1              | 0440987654321 |

  @regression
  Scenario Outline: Generate event to downstream systems when a passenger email has changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger <passengerIndex> requests to update their email to <email>
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers | passengerIndex | email                       |
      | 1 Adult    | 1              | alessandro.delpiero@juve.it |

  @regression
  Scenario Outline: Generate event to downstream systems when a passenger EJ Plus has changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger <passengerIndex> requests to update their Ej plus number
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers | passengerIndex |
      | 1 Adult    | 1              |

  @regression
  Scenario Outline: Generate event to downstream systems when a passenger SSRs has changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger "1" updates SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | MAAS |                  | false                     |
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers |
      | 1 Adult    |

  @regression
  Scenario Outline: Generate event to downstream systems when all passenger details have changed
    Given I am using ADCustomerService channel
    And   I created a successful booking for <passengers>
    And   the booking is amendable
    When  passenger "1" updates SSRs with the following:
      | code | isTandCsAccepted | overrideSectorRestriction |
      | MAAS |                  | false                     |
    And   passenger <passengerIndex> requests to update their <age>, Ej plus number, <email>, <telephone>, <nif>
    And   I do commit booking for given basket
    And   ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger
    Then  I validate the json schema for updated booking event
    Examples:
      | passengers | passengerIndex | age | email                       | telephone     | nif        |
      | 1 Adult    | 1              | 28  | alessandro.delpiero@juve.it | 0440987654321 | 0987654321 |
