Feature: Cucumber JGiven integration

  @TestTag
  Scenario: Just a failing scenario
    Given some step
    When I run a failing step
    Then an exception will be thrown
