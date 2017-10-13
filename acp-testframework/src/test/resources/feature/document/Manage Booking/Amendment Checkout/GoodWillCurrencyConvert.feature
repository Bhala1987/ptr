@TeamA
@Sprint29
@FCPH-9881
Feature: Calculate currency conversion for good will amount to be paid
  
  Scenario Outline: Convert to the amount into the requested currency with an expense of margin
    Given the channel <channel> is used
    And I want to convert from <FromCurrency> to <ToCurrency> for an amount of <Amount>
    When I send the request to calculateCurrencyConversion service
    Then it should deduct the margin from total converted amount value
    @regression
    Examples:
      | channel           | FromCurrency | ToCurrency | Amount |
      | Digital           | EUR          | USD        | 65.5   |
    Examples:
      | channel           | FromCurrency | ToCurrency | Amount |
      | ADAirport         | USD          | CZK        | 100.00 |
      | ADCustomerService | CHF          | EUR        | 167.67 |
      | PublicApiB2B      | GBP          | DKK        | 50.0   |
      | PublicApiMobile   | DKK          | CHF        | 1      |

  Scenario Outline: Error scenarios for currency conversion service
    Given the channel <channel> is used
    And I want to convert from <FromCurrency> to <ToCurrency> for an amount of <Amount>
    When I send the wrong request to calculateCurrencyConversion service
    Then the channel will receive an error with code <Error>
    Examples:
      | channel           | FromCurrency | ToCurrency | Amount  | Error           |
      | ADAirport         | CHF          | EUR        |         | SVC_100532_2001 |
      | PublicApiB2B      |              | USD        | 100.00  | SVC_100532_2002 |
      | PublicApiMobile   | CHF          |            | 100.00  | SVC_100532_2003 |
      | ADAirport         | USD          | CZK        | -100.00 | SVC_100532_3001 |
      | Digital           | AAA          | USD        | 100.00  | SVC_100532_3002 |
      | ADCustomerService | GBP          | BBB        | 100.00  | SVC_100532_3002 |


