package feature.document.steps.data;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.DealDao;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import cucumber.api.java.en.Given;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * DealHelper handle the retrieval of deal informations from db.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class DealHelper {

    private static final String VALID = "valid";
    private static final String INVALID = "invalid";

    @Steps
    private SerenityReporter reporter;

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private DealDao dealDao;

    /**
     * Get a deal from the db and set it in testData
     *
     * @param applicationId it can be valid, invalid or no; define which kind of data need to retrieve from the db
     * @param officeId      it can be valid, invalid or no; define which kind of data need to retrieve from the db
     * @param corporateId   it can be valid, invalid or no; define which kind of data need to retrieve from the db
     */
    @Step("Deal selection")
    @Given("^a deal with (valid|invalid|no) application id, (valid|invalid|no) office id and (valid|invalid|no) corporate id is defined$")
    public void selectDeal(String applicationId, String officeId, String corporateId) {
        List<DealModel> deals = dealDao.getDeals(true, true, true);
        DealModel deal = deals.get(new Random().nextInt(deals.size()));
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);

        switch (applicationId) {
            case VALID:
                reporter.info(deal.getSystemName() + " selected as application id");
                testData.setData(APPLICATION_ID, deal.getSystemName());
                testData.setData(DEALS, Collections.singletonList(deal));
                headers.xApplicationId(deal.getSystemName());
                break;
            case INVALID:
                reporter.info(INVALID + " selected as application id");
                testData.setData(APPLICATION_ID, INVALID);
                break;
            default:
                reporter.info("Application id empty");
                testData.setData(APPLICATION_ID, null);
                break;
        }

        switch (officeId) {
            case VALID:
                reporter.info(deal.getOfficeId() + " selected as office id");
                testData.setData(OFFICE_ID, deal.getOfficeId());
                headers.xOfficeId(deal.getOfficeId());
                break;
            case INVALID:
                reporter.info(INVALID + " selected as office id");
                testData.setData(OFFICE_ID, INVALID);
                break;
            default:
                reporter.info("Office id empty");
                testData.setData(OFFICE_ID, null);
                break;
        }

        switch (corporateId) {
            case VALID:
                reporter.info(deal.getCorporateId() + " selected as corporate id");
                testData.setData(CORPORATE_ID, deal.getCorporateId());
                headers.xCorporateId(deal.getCorporateId());
                break;
            case INVALID:
                reporter.info(INVALID + " selected as corporate id");
                testData.setData(CORPORATE_ID, INVALID);
                break;
            default:
                reporter.info("Corporate id empty");
                testData.setData(CORPORATE_ID, null);
                break;
        }

        testData.setData(HEADERS, headers);
    }
}