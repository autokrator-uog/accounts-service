Feature: Statements
  The Accounts Service should provide an API to retrieve the account statements for a particular account.

  Background:
    Given there is a user named Alice with accountId 1
    And accountId 1 has a balance of £0 in it

  Scenario: A ConfirmedCredit event is received
    When a ConfirmedCredit event is received for accountId 1 with amount £30
    Then the statement for accountId 1 contains an entry with amount £30

  Scenario: A ConfirmedDebit event is received
    When a ConfirmedDebit event is received for accountId 1 with amount £30
    Then the statement for accountId 1 contains an entry with amount £-30

  Scenario: Request statement - empty
    When the statement for accountId 1 is requested
    Then the response is an empty list

  Scenario:
    Given there is a statement item for accountId 1 with amount £25
    When the statement for accountId 1 is requested
    Then the response contains a statement with amount £25
