package feature.document;

import cucumber.api.java.en.Given;

/**
 * Created by markphipps on 26/04/2017.
 */
public class DeleteSavedCustomerAPISSteps {

    private GetCustomerAPIsSteps savedCustomerApisHelper = new GetCustomerAPIsSteps();

    @Given("^I have a customer with a saved passenger that has APIS$")
    public void iHaveACustomerWithASavedPassenger() throws Throwable {
        savedCustomerApisHelper.iHaveAPIsStoredForAForLessThanXMonths("passenger");

    }


}
