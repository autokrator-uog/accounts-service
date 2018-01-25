package uk.ac.gla.sed.clients.accountsservice.bdd;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        format = {"pretty", "html:target/cucumber"}
)
public class BddTestRunnerTest {
    public static DbTestFixture db = new DbTestFixture();
}
