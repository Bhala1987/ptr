@Sprint32
@TeamD
@FCPH-10811

Feature: change to customer address validation

  Scenario Outline: Generate error if Post code, address lines and city are not the correct length while create customer
    Given one of this channel ADAirport, Digital is used
    And I want to create a customer profile with field <field> as <invalidValue>
    When I created a customer
    Then the channel will receive an error with code <error>

    Examples:
      | field          | invalidValue                                        | error           |
      | postcode       | BLANK                                               | SVC_100047_2010 |
      | postcode       | A123123123121212                                    | SVC_100047_2023 |
      | address line 1 | BLANK                                               | SVC_100047_2007 |
      | address line 1 | A1231231231212 12A1231231231212 12A123123123121212Q | SVC_100047_2020 |
      | address line 2 | A1231231231212 12A1231231231212 12A123123123121212Q | SVC_100047_2021 |
      | city           | BLANK                                               | SVC_100047_2009 |
      | city           | A1231231231212 12A123123123121A                     | SVC_100047_2044 |

  Scenario Outline: Generate error if Post code, address lines and city are not the correct length while update customer
    Given one of this channel ADAirport, Digital is used
    And I created a customer
    And I want to update a customer profile with field <field> as <invalidValue>
    When I send the request to updateCustomerProfile service
    Then the channel will receive an error with code <error>

    Examples:
      | field          | invalidValue                                        | error           |
      | postcode       | BLANK                                               | SVC_100060_2001 |
      | postcode       | A123123123121212                                    | SVC_100047_2023 |
      | address line 1 | BLANK                                               | SVC_100060_2001 |
      | address line 1 | A1231231231212 12A1231231231212 12A123123123121212Q | SVC_100047_2020 |
      | address line 2 | A1231231231212 12A1231231231212 12A123123123121212Q | SVC_100047_2021 |
      | city           | BLANK                                               | SVC_100060_2001 |
      | city           | A1231231231212 12A123123123121A                     | SVC_100047_2044 |


