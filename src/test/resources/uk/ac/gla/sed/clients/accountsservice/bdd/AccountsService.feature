Feature: Accounts Service
    The Accounts Service is responsible for determining the balance of each account in the system.
    The Accounts Service is also responsible for validating PendingTransaction events
    and executing the necessary state changes for the transaction to occur.

    Background:
      Given there is a user named Alice with accountId 0
      And there is a user named Bob with accountId 1

    Scenario: A transaction is requested from Alice to Bob, and Alice has enough funds to complete it.
      Given accountId 0 has a balance of £50 in it
      And accountId 1 has a balance of £0 in it
      When a PendingTransaction event is received for moving £30 from account 0 to account 1 with ID 001
      Then accountId 0 now has a balance of £20 in it
      And accountId 1 now has a balance of £30 in it
      And a AcceptedTransaction event was created in response to PendingTransaction ID 001
      And a ConfirmedCredit event was created for account 1 with amount £30
      And a ConfirmedDebit event was created for account 0 with amount £30

    Scenario: A transaction is requested from Alice to Bob, but Alice does not have enough funds to complete it.
      Given accountId 0 has a balance of £50 in it
      And accountId 1 has a balance of £0 in it
      When a PendingTransaction event is received for moving £51 from account 0 to account 1 with ID 002
      Then accountId 0 now has a balance of £50 in it
      And accountId 1 now has a balance of £0 in it
      And a RejectedTransaction event was created in response to PendingTransaction ID 002

#  TO BE IMPLEMENTED...
#    Scenario: A user wants to view their balance
#      Given accountId 0 has a balance of £50 in it
#      And Alice is logged in with a valid session token
#      When Alice requests to view her balance
#      Then the Accounts Service returns £50
#
#    Scenario: A user wants to view their balance, but has a bad session token.
#      Given accountId 0 has a balance of £50 in it
#      And Alice has an invalid session token
#      When Alice requests to view her balance
#      Then the Accounts Service returns a 401 Unauthorized

# TODO add (and decide on) scenarios for account creation
