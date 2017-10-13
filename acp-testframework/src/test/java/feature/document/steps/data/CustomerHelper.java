package feature.document.steps.data;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.models.DbCustomerModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import cucumber.api.java.en.And;
import net.thucydides.core.annotations.Steps;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * CustomerHelper handle the retrieval of customers from the DB.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class CustomerHelper {

    @Steps
    private SerenityReporter reporter;

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private CustomerDao customerDao;

    /**
     * Retrieve a random customer from the db, based on constrain, and store his information in testData;
     * if constrain is null it will find a totally random customer
     *
     * @param constrain can be significant others
     * @throws EasyjetCompromisedException if no customer is found in the database
     */
    @And("^a( staff)? customer(?: with (significant others|dependents|saved passenger|APIs))? exist in the database$")
    public void findActiveCustomer(String staff, String constrain) throws EasyjetCompromisedException {
        if (Objects.isNull(constrain)) {
            constrain = "";
        }
        DbCustomerModel customer;
        switch (constrain) {
            case "significant others":
                customer = customerDao.getActiveCustomerWithSignificantOthers(StringUtils.isNotBlank(staff));
                testData.setData(SIGNIFICANT_OTHER_ID, customer.getSignificantOther().getId());
                break;
            case "dependents":
                customer = customerDao.getActiveCustomerWithDependents();
                testData.setData(DEPENDENT_ID, customer.getDependent().getId());
                break;
//            case "saved passenger":
            case "APIs":
                customer = customerDao.getActiveCustomerWithAPIs(StringUtils.isNotBlank(staff));
                break;
            default:
                customer = customerDao.getActiveCustomer(StringUtils.isNotBlank(staff));
        }

        reporter.info("Customer " + customer.getId() + " selected");

        testData.setData(CUSTOMER_ID, customer.getId());
    }

}