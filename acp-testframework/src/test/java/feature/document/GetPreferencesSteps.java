package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PreferencesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.PreferencesService;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created by Siva on 09/01/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetPreferencesSteps {

    private PreferencesService preferencesService;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private SerenityFacade testData;

    @When("^I call the get preference service$")
    public void i_call_the_get_preference_service() throws Throwable {
        preferencesService = serviceFactory.getPreferences(new PreferencesRequest(HybrisHeaders.getValid(testData.getChannel()).build()));
        preferencesService.invoke();
    }

    @Then("^It should see the following contact method options$")
    public void it_should_see_the_following_contact_method_options(List<String> contactMethodOptions) throws Throwable {
        preferencesService.assertThat().theseContactMethodOptionsAllReturned(contactMethodOptions);
    }

    @Then("^It should see the following contact type options$")
    public void it_should_see_the_following_contact_type_options(List<String> contactTypeOptions) throws Throwable {
        preferencesService.assertThat().theseContactTypeOptionsAllReturned(contactTypeOptions);
    }

    @Then("^It should see the following frequency options$")
    public void it_should_see_the_following_frequency_options(List<String> frequencyOptions) throws Throwable {
        preferencesService.assertThat().theseFrequencyOptionsAllReturned(frequencyOptions);
    }

    @Then("^It should see the following hold bag weight options$")
    public void it_should_see_the_following_hold_bag_weight_options(List<String> bagWeightOptions) throws Throwable {
        preferencesService.assertThat().theseBagWeightOptionsAllReturned(bagWeightOptions);
    }

    @Then("^It should see the following marketing Communication Options$")
    public void it_should_see_the_following_marketing_Communication_Options(List<String> marketingCommOptions) throws Throwable {
        preferencesService.assertThat().theseMarketingCommunicationOptionsAllReturned(marketingCommOptions);
    }

    @Then("^It should see the following seating Preference Options$")
    public void it_should_see_the_following_seating_Preference_Options(List<String> seatingPreferenceOptions) throws Throwable {
        preferencesService.assertThat().theseSeatingPreferenceOptionsAllReturned(seatingPreferenceOptions);
    }

    @Then("^It should see the following travelling Season Options$")
    public void it_should_see_the_following_travelling_Season_Options(List<String> travellingSeasonOptions) throws Throwable {
        preferencesService.assertThat().theseTravellingSeasonOptionsAllReturned(travellingSeasonOptions);
    }

    @Then("^It should see the following travelling When Options$")
    public void it_should_see_the_following_travelling_When_Options(List<String> travellingWhenOptions) throws Throwable {
        preferencesService.assertThat().theseTravellingWhenOptionsAllReturned(travellingWhenOptions);
    }

    @Then("^It should see the following travelling With Options$")
    public void it_should_see_the_following_travelling_With_Options(List<String> travellingWithOptions) throws Throwable {
        preferencesService.assertThat().theseTravellingWithOptionsAllReturned(travellingWithOptions);
    }

    @Then("^It should see the following trip Type Options$")
    public void it_should_see_the_following_trip_Type_Options(List<String> tripTypeOptions) throws Throwable {
        preferencesService.assertThat().theseTripTypeOptionsAllReturned(tripTypeOptions);
    }

    @When("^the request for preferences is called$")
    public void the_request_for_preferences_is_called() throws Throwable {
        preferencesService = serviceFactory.getPreferences(new PreferencesRequest(HybrisHeaders.getValid("Digital").build()));
        preferencesService.invoke();
    }

}