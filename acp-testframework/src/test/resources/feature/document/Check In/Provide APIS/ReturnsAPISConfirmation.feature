Feature: Returns APIS Confirmation

  @manual
  @TeamD
  @Sprint30
  @FCPH-10400
  Scenario: Generate event on providing APIs
    Given that I have commit booking with BOOKED status
    And the channel has initiated manage APIs
    And I have added new APIs as follows
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName      |
      | 1986-12-12  | 2050-12-12         | IN000001       | PASSPORT     | MALE   | GBR         | GBR            | Adult Adult   |
      | 2005-12-12  | 2050-12-12         | IN000002       | PASSPORT     | MALE   | GBR         | GBR            | Child Child   |
      | 2016-12-12  | 2050-12-12         | IN000003       | PASSPORT     | MALE   | GBR         | GBR            | Infant Infant |
    When I call save APIs for that customer
    Then I must receive the same APIs provided
    And the commit booking status is BOOKED
    And an event should be generate to inform the submission

  @manual
  @TeamD
  @Sprint30
  @FCPH-10400
  Scenario: Generate event on updating APIs
    Given that I have commit booking with BOOKED status
    And the channel has initiated manage APIs
    And I have updated APIs
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName      |
      | 1986-12-12  | 2050-12-12         | IN0000011      | PASSPORT     | MALE   | GBR         | GBR            | Adult Adult   |
      | 2005-12-12  | 2050-12-12         | IN0000012      | PASSPORT     | MALE   | GBR         | GBR            | Child Child   |
      | 2016-12-12  | 2050-12-12         | IN0000013      | PASSPORT     | MALE   | GBR         | GBR            | Infant Infant |
    When I call save APIs for that customer
    Then I must receive the same APIs provided
    And the commit booking status is BOOKED
    And an event should be generate to inform the submission

  @manual
  @TeamD
  @Sprint30
  @FCPH-10400
  Scenario: Generate event on providing APIs
    Given that I have commit booking with Checked In status
    And the channel has initiated manage APIS
    And I have updated APIs
      | dateOfBirth | documentExpiryDate | documentNumber | documentType | gender | nationality | countryOfIssue | fullName      |
      | 1986-12-12  | 2050-12-12         | IN000001       | PASSPORT     | MALE   | GBR         | GBR            | Adult Adult   |
      | 2005-12-12  | 2050-12-12         | IN000002       | PASSPORT     | MALE   | GBR         | GBR            | Child Child   |
      | 2016-12-12  | 2050-12-12         | IN000003       | PASSPORT     | MALE   | GBR         | GBR            | Infant Infant |
    When I call save APIs for that customer
    Then I must receive the same APIs provided
    And the commit booking status should changed to BOOKED
    And an event should be generate to inform the submission