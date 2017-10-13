package feature.document.steps;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import feature.document.steps.constants.StepsRegex;
import feature.document.steps.services.agentservices.LoginSteps;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * CommonSteps contains the step used across multiple tests.
 * It contains methods to set channels and headers, and method to check common part of the responses (i.e. errors and additional informations).
 */
@ContextConfiguration(classes = TestApplication.class)
public class CommonSteps {

    private static final Logger LOG = LogManager.getLogger(CommonSteps.class);

    @Steps
    private SerenityReporter reporter;

    @Autowired
    private SerenityFacade testData;

    @Steps
    private LoginSteps loginSteps;

    private IService service;

    //  ----- channel/header initialization -----
    @Step("{0} channel used")
    @Given("^the channel " + StepsRegex.CHANNELS + " is used$")
    public void channelSelection(String channel) {
        testData.setData(CHANNEL, channel);
        testData.setData(HEADERS, HybrisHeaders.getValid(channel));
    }

    @Given("^one of this channel " + StepsRegex.CHANNEL_LIST + " is used$")
    public void randomChannelSelectionFromList(String channel) {
        String[] channels = channel.split(",\\s*");
        int rnd = new Random().nextInt(channels.length);
        reporter.info(channels[rnd] + " channel selected");
        testData.setData(CHANNEL, channels[rnd]);
        testData.setData(HEADERS, HybrisHeaders.getValid(channels[rnd]));
        if (channels[rnd].startsWith("AD")) {
            loginSteps.succesfulLogin();
        }
    }

    @Given("^a channel is used$")
    public void randomChannelSelection() {
        String[] channels = StepsRegex.CHANNELS.substring(1, StepsRegex.CHANNELS.length() - 1).split("\\|");
        int rnd = new Random().nextInt(channels.length);
        reporter.info(channels[rnd] + " channel selected");
        testData.setData(CHANNEL, channels[rnd]);
        testData.setData(HEADERS, HybrisHeaders.getValid(channels[rnd]));
        if (channels[rnd].startsWith("AD")) {
            loginSteps.succesfulLogin();
        }
    }

    //  ----- other param initialization -----
    @And("^the header contains ([^\\s]+)\\s?=\\s?([^\\s]+)$")
    public void setHeader(String param, String value) throws EasyjetCompromisedException {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        try {
            headers.getClass().getMethod(param, String.class).invoke(headers, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("Invalid header", e);
        }
    }

    @But("^I set transaction id to ([\\d|\\w]+) in order to (?:.*)$")
    public void setTransactionId(String storyNumber) {
        testData.setData(TRANSACTION_ID, UUID.randomUUID().toString().substring(0, 24) + storyNumber);
    }

    //  ----- error response validation -----
    @Then("^the channel will receive an error with code (SVC_\\d+_\\d+(?:, SVC_\\d+_\\d+)*)$")
    public void errorCheck(List<String> error) {
        service = testData.getData(SERVICE);
        service.assertThatErrors().containedTheCorrectErrorMessage(error);
    }

    @Then("^the affected data of the error (SVC_\\d+_\\d+) contains ([^\\s]+)\\s?=\\s?([^\\s]+)$")
    public void errorAffectedDataCheck(String error, String param, String value) throws EasyjetCompromisedException {
        List<String> params = Collections.singletonList(param);
        List<String> values = Collections.singletonList(value);

        service.assertThatErrors().containedTheCorrectErrorAffectedData(error, params, values);
    }

    @Then("^the affected data of the error (SVC_\\d+_\\d+) contains:$")
    public void errorAffectedDataCheck(String error, DataTable affectedDatas) throws EasyjetCompromisedException {
        List<String> params = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : affectedDatas.asMap(String.class, String.class).entrySet()) {
            params.add(entry.getKey());
            values.add(entry.getValue());
        }
        service.assertThatErrors().containedTheCorrectErrorAffectedData(error, params, values);
    }

    @Then("^the affected data of the error (SVC_\\d+_\\d+) does not contains (.+(?:, .+)*)$")
    public void errorAffectedDataValidation(String error, List<String> params) throws EasyjetCompromisedException {
        service.assertThatErrors().notContainedTheErrorAffectedData(error, params);
    }

    //  ----- additional informations validation -----
    @Then("^the channel will receive a warning with code (SVC_\\d+_\\d+(?:, SVC_\\d+_\\d+)*)$")
    public void warningCheck(List<String> warning) {
        service = testData.getData(SERVICE);
        service.assertThat().containedTheCorrectWarningMessage(warning);
    }

}