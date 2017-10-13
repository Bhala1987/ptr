@FCPH-9841
@Sprint31
@TeamA
@manual
Feature: Transfer unused funds of a voucher

  Scenario: Transfer the unused funds to a credit file with GBP currency
    Given I create one "GBP" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "GBP" currency

  Scenario: Do not transfer the unused funds to a credit file with GBP currency
    Given I create one "GBP" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with EUR currency
    Given I create one "EUR" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "EUR" currency

  Scenario: Do not transfer the unused funds to a credit file with EUR currency
    Given I create one "EUR" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with CFH currency
    Given I create one "CFH" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "CFH" currency

  Scenario: Do not transfer the unused funds to a credit file with CFH currency
    Given I create one "CFH" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with HUF currency
    Given I create one "HUF" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "HUF" currency

  Scenario: Do not transfer the unused funds to a credit file with HUF currency
    Given I create one "HUF" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with MAD currency
    Given I create one "MAD" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "MAD" currency

  Scenario: Do not transfer the unused funds to a credit file with MAD currency
    Given I create one "MAD" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with PLN currency
    Given I create one "PLN" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "PLN" currency

  Scenario: Do not transfer the unused funds to a credit file with PLN currency
    Given I create one "PLN" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with SEK currency
    Given I create one "SEK" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "SEK" currency

  Scenario: Do not transfer the unused funds to a credit file with SEK currency
    Given I create one "SEK" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with USD currency
    Given I create one "USD" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "USD" currency

  Scenario: Do not transfer the unused funds to a credit file with USD currency
    Given I create one "USD" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with DKK currency
    Given I create one "DKK" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "DKK" currency

  Scenario: Do not transfer the unused funds to a credit file with DKK currency
    Given I create one "DKK" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with CSK currency
    Given I create one "CSK" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "CSK" currency

  Scenario: Do not transfer the unused funds to a credit file with CSK currency
    Given I create one "CSK" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File

  Scenario: Transfer the unused funds to a credit file with CZK currency
    Given I create one "CZK" voucher
    And   I set the active date in the past
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will automatically set the Voucher Remaining Balance to zero
    And   It will transfer the money to an Unused Voucher Credit File Fund with "CZK" currency

  Scenario: Do not transfer the unused funds to a credit file with CZK currency
    Given I create one "CZK" voucher
    And   I set the active date in the future
    And   I set the remaining voucher value greater than zero
    When  I run the transferUnUsedExpiredVocherFundsJob
    Then  It will not change neither the voucher or the Credit File
