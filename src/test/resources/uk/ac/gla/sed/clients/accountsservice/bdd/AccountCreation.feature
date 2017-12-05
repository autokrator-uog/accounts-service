
Feature: Account Creation
  The Accounts service is responsible for the creation of new (bank) accounts. These are requested from the User Service.

  Scenario: The user service requests a new account to be created
    When an AccountCreationRequest event is received with RequestID 001
    Then an AccountCreated event was created in response to AccountCreationRequest RequestID 001 that defines AccountID 1
    And there is now an account with accountId 1

  Scenario: The user service requests a new account to be created (and it is not the first account to have existed)
    Given there is a user named Alice with accountId 1
    When an AccountCreationRequest event is received with RequestID 001
    Then an AccountCreated event was created in response to AccountCreationRequest RequestID 001 that defines AccountID 2
    And there is now an account with accountId 2
