package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.eres.EresFlightsDao;
import com.hybris.easyjet.database.hybris.dao.FareClassDao;
import com.hybris.easyjet.database.hybris.models.DeallocateFareModel;
import com.hybris.easyjet.database.seating.SeatingDao;
import com.hybris.easyjet.fixture.alei.invokers.ALHeaders;
import com.hybris.easyjet.fixture.alei.invokers.pathparams.InventoryPathParams;
import com.hybris.easyjet.fixture.alei.invokers.requestbodies.DeallocateInventoryRequestBody;
import com.hybris.easyjet.fixture.alei.invokers.requests.InventoryRequest;
import com.hybris.easyjet.fixture.alei.invokers.services.factories.ALServiceFactory;
import com.hybris.easyjet.fixture.alei.invokers.services.impl.ALInventoryService;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.PassengersAndSeatsNumber;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.purchasedseats.RemovePurchasedSeatRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requests.PurchasedSeatRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.PurchasedSeatService;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.steps.StepEventBus;
import org.assertj.core.util.Lists;
import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;
import static com.hybris.easyjet.config.SerenityFacade.testData.ALLOCATED_SEATS;
import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams.BasketPaths.REMOVE_PURCHASED_SEAT;

/**
 * Created by dwebb on 12/5/2016.
 */
@ContextConfiguration(classes = TestApplication.class)
public class GlobalHooks {
    private static boolean dunit = false;
    private static String tags;
    private static Pattern pattern;
    private static Matcher matcher;
    @Rule
    public SpringIntegrationMethodRule springIntegration = new SpringIntegrationMethodRule();
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private FareClassDao fareClassDao;
    @Autowired
    private ALServiceFactory alFactory;
    @Autowired
    private HybrisServiceFactory hybrisFactory;
    @Autowired
    private EresFlightsDao eresFlightsDao;
    @Autowired
    private SeatingDao seatingDao;
    private String mock;
    @Autowired
    private BasketHelper basketHelper;


    @Before
    public void startUp(Scenario scenario) throws SQLException {
//        releaseData();
        setDefaultProperties(scenario);
        parseTagBefore(scenario);
        outputVmAndThreadInfo("======= TEST EXECUTION STARTED ====== >");
    }

    private void releaseData() throws SQLException {
        if (!System.getProperty("environment").equals("local") && !dunit && !System.getProperty("os.name").contains("Mac")) {
            eresFlightsDao.cleanData();
//            seatingDao.clearSeats();
            dunit = true;
        }
    }

    private void setDefaultProperties(Scenario scenario) {
        Serenity.initializeTestSession();
        testData.setData(SCENARIO, scenario);
        System.setProperty("channel", "Digital");
        if (System.getProperty("eres") == null) {
            System.setProperty("eres", "false");
        }
        if (System.getProperty("mocked") == null) {
            System.setProperty("mocked", "false");
        }
        mock = System.getProperty("mocked");
    }

    /**
     * tag method will analyze the tag of the scenario and add the proper issue link to jira, the story/backoffice tag for listing in serenity report and mark backoffice scenarios as manual
     * <p>
     * if scenario is annotated with one of the specified tag; an AssumptionViolatedException will be thrown and the test steps will not be executed and the outcome of each step of the test will be 'ignored'.
     * <p>
     * if scenario is annotated with @AsXml the system property will be set for an XML response from the API that we get one.
     *
     * @param scenario is the scenario being tested, it's automatically given by cucumber API
     * @see com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders This property is checked for and if is "true" it sets the accept header.
     */
    private void parseTagBefore(Scenario scenario) {
        if (StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            pattern = Pattern.compile("@FCPH-\\d+");
            matcher = pattern.matcher(System.getProperty("cucumber.options"));
            boolean defectRetest = false;
            if (matcher.find()) {
                defectRetest = true;
            }

            tags = String.join(";", scenario.getSourceTagNames());

            pattern = Pattern.compile("@local");
            matcher = pattern.matcher(tags);
            if (matcher.find()) {
                System.setProperty("mocked", "true");
            }

            pattern = Pattern.compile("@AsXml");
            matcher = pattern.matcher(tags);
            if (matcher.find()) {
                System.setProperty("AsXml", "true");
            }

            pattern = Pattern.compile("@FCPH-\\d+");
            matcher = pattern.matcher(tags);
            while (matcher.find()) {
                String story = matcher.group().substring(1);
                StepEventBus.getEventBus().addIssuesToCurrentTest(Collections.singletonList(story));
                StepEventBus.getEventBus().addTagsToCurrentTest(Lists.newArrayList(TestTag.withName(story).andType("story")));
            }

            pattern = Pattern.compile("@Sprint\\d+");
            matcher = pattern.matcher(tags);
            while (matcher.find()) {
                StepEventBus.getEventBus().addTagsToCurrentTest(Lists.newArrayList(TestTag.withName(matcher.group().substring(1)).andType("sprint")));
            }

            pattern = Pattern.compile("@backoffice:FCPH-\\d+");
            matcher = pattern.matcher(tags);
            if (matcher.find()) {
                Arrays.asList(matcher.group().substring(12).split(",")).forEach(
                        story -> {
                            StepEventBus.getEventBus().addIssuesToCurrentTest(Collections.singletonList(story));
                            StepEventBus.getEventBus().addTagsToCurrentTest(Lists.newArrayList(TestTag.withName(story).andType("story")));
                        }
                );
                StepEventBus.getEventBus().testIsManual();
                throw new AssumptionViolatedException("This test is manual");
            }

            pattern = Pattern.compile("@defect:FCPH-\\d+|@defect:FQT-\\d+|@defect:FCP-\\d+");
            matcher = pattern.matcher(tags);
            if (matcher.find() && !defectRetest) {
                Arrays.asList(matcher.group().substring(8).split(",")).forEach(
                        issue -> StepEventBus.getEventBus().addIssuesToCurrentTest(Collections.singletonList(issue))
                );
                StepEventBus.getEventBus().testSkipped();
                throw new AssumptionViolatedException("This test has known defect");
            }

            pattern = Pattern.compile("@Ignore|@Ignored|@ignore|@ignored|@Wip|@wip|@Skip|@skip|@Pending|@pending|@Manual|@manual");
            matcher = pattern.matcher(tags);
            if (matcher.find()) {
                throw new AssumptionViolatedException("This test is " + matcher.group().substring(1));
            }
        }
    }

    @After
    public void tearDown() {
        initSystemProperties();
        parseTagAfter();
//        Disabled because it slows execution on Jenkins
        deallocateFlights();
        deallocateSeatsViaService();
        if(Objects.isNull(testData.getData(SerenityFacade.DataKeys.CHANNEL)))
            testData.setData(SerenityFacade.DataKeys.CHANNEL,"Digital");
//        basketHelper.clearBasket(null,testData.getData(SerenityFacade.DataKeys.CHANNEL));
        outputVmAndThreadInfo("======= TEST EXECUTION COMPLETE !!!!! ======\n");
        testData.cleanStoredData();
        clearCookiesInClient();
    }

    /**
     * clear system properties, please set here values that need to be reset in system properties
     */
    private void initSystemProperties() {
        System.setProperty("mocked", mock);
    }

    public static void clearCookiesInClient() {
        HybrisService.theJSessionCookie.remove();
    }

    /**
     * defectedTest method will be executed after each test of the feature files annotated with @defect:FCPH-\\d+.
     * The outcome of each step of the test will be overwritten to be 'skipped'.
     * if scenario is annotated with one of the specified tag, the outcome of each step of the test will be overwritten to be 'skipped'.
     * if scenario is annotated with one of the specified tag, the outcome of each step of the test will be overwritten to be 'pending'.
     * After a scenario that is tagged with @AsXml we want to reset the system property back.
     */
    private void parseTagAfter() {
        if (StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            pattern = Pattern.compile("@AsXml");
            matcher = pattern.matcher(tags);
            if (matcher.find()) {
                System.setProperty("AsXml", "false");
            }

            pattern = Pattern.compile("@Skip|@skip|@Wip|@wip");
            matcher = pattern.matcher(tags);
            if (matcher.find()) {
                StepEventBus.getEventBus().setAllStepsTo(TestResult.SKIPPED);
            }

            pattern = Pattern.compile("@Pending|@pending");
            matcher = pattern.matcher(tags);
            if (matcher.find()) {
                StepEventBus.getEventBus().setAllStepsTo(TestResult.PENDING);
            }
        }
    }

    private void outputVmAndThreadInfo(String message) {
        long threadId = Thread.currentThread().getId();
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("\n" + message + " IN THREAD : " + threadId + ", IN JVM: " + processName + "\n");
    }

    private void deallocateSeatsViaService() {
        if (System.getProperty("mocked").equals("false") && Serenity.hasASessionVariableCalled(ALLOCATED_SEATS)) {
            List<Map<String, String>> allocatedSeats = Serenity.sessionVariableCalled(ALLOCATED_SEATS);

            allocatedSeats.forEach(
                    seat -> {
                        PurchasedSeatService removeSeat = buildRequestDeallocateSeat(seat, null);

                        removeSeat.invoke();
                        if(Objects.nonNull(removeSeat.getErrors())) {
                            List<String> errorCodeDynamicRule = removeSeat.getErrors().getErrors().stream().map(e -> e.getCode()).collect(Collectors.toList());
                            if(errorCodeDynamicRule.contains("SVC_100013_1001")) {
                                removeSeat = buildRequestDeallocateSeat(seat, testData.getData(BASKET_ID));
                                removeSeat.invoke();
                            }
                        }
                    }
            );
        }
    }

    private PurchasedSeatService buildRequestDeallocateSeat(Map<String, String> seat, String basketCode) {
        PurchasedSeatService removeSeat = hybrisFactory.managePurchasedSeat(new PurchasedSeatRequest(
                HybrisHeaders.getValid(testData.getChannel()).build(),
                BasketPathParams.builder()
                        .basketId(Objects.isNull(basketCode) ? seat.get("basketId") : basketCode)
                        .flightKey(seat.get("flightKey"))
                        .path(REMOVE_PURCHASED_SEAT)
                        .build(),
                RemovePurchasedSeatRequestBody.builder()
                        .passengersAndSeatsNumbers(
                                new ArrayList<PassengersAndSeatsNumber>() {{
                                    add(PassengersAndSeatsNumber.builder()
                                            .passengerId(seat.get("passengerId"))
                                            .seats(
                                                    new ArrayList<String>() {{
                                                        add(seat.get("seatNumber"));
                                                    }}
                                            )
                                            .build());
                                }}
                        )
                        .build(),
                "DELETE")
        );
        return removeSeat;
    }

    private void deallocateFlights() {

        if (System.getProperty("mocked").equals("false")) {
            List<DeallocateInventoryRequestBody.DeallocateFare> deallocateFares;

            List<HashMap<String, String>> basketFlights = testData.getData(BASKET_FLIGHTS) != null ? testData.getData(BASKET_FLIGHTS) : new ArrayList<>();
            for (HashMap<String, String> allocatedFlight : basketFlights) {
                List<DeallocateFareModel> allocatedFares = fareClassDao.getFareClassForCart(allocatedFlight.get("basketId"), allocatedFlight.get("flightKey"));
                deallocateFares = getAllocatedFares(allocatedFares);
                if (deallocateFares.size() > 0)
                    sendRequestToAL(allocatedFlight.get("flightKey"), allocatedFlight.get("fareType"), deallocateFares);
            }

            List<HashMap<String, String>> commitFlights = testData.getData(COMMIT_FLIGHTS) != null ? testData.getData(COMMIT_FLIGHTS) : new ArrayList<>();
            for (HashMap<String, String> allocatedFlight : commitFlights) {
                List<DeallocateFareModel> allocatedFares = fareClassDao.getFareClassForOrder(allocatedFlight.get("bookingReference"), allocatedFlight.get("flightKey"));
                deallocateFares = getAllocatedFares(allocatedFares);
                if (deallocateFares.size() > 0)
                    sendRequestToAL(allocatedFlight.get("flightKey"), allocatedFlight.get("fareType"), deallocateFares);
            }

            HashMap<String, Map<String, Integer>> allocatedFlights = testData.keyExist(ALLOCATED_FLIGHTS) ? testData.getData(ALLOCATED_FLIGHTS) : new HashMap<>();
            for (Map.Entry<String, Map<String, Integer>> allocatedFlight : allocatedFlights.entrySet()) {
                String flightKey = allocatedFlight.getKey().split("-")[0];
                String fareType = allocatedFlight.getKey().split("-")[1];
                deallocateFares = new ArrayList<>();
                for (Map.Entry<String, Integer> allocatedFare : allocatedFlight.getValue().entrySet()) {
                    deallocateFares.add(
                            DeallocateInventoryRequestBody.DeallocateFare.builder()
                                    .fareClass(allocatedFare.getKey().toUpperCase())
                                    .numberRequired(allocatedFare.getValue())
                                    .build()
                    );
                }
                if (deallocateFares.size() > 0)
                    sendRequestToAL(flightKey, fareType, deallocateFares);
            }
        }

    }

    private List<DeallocateInventoryRequestBody.DeallocateFare> getAllocatedFares(List<DeallocateFareModel> allocatedFares) {
        List<DeallocateInventoryRequestBody.DeallocateFare> deallocateFares = new ArrayList<>();

        for (DeallocateFareModel allocatedFare : allocatedFares) {
            deallocateFares.add(
                    DeallocateInventoryRequestBody.DeallocateFare.builder()
                            .fareClass(allocatedFare.getFareClass())
                            .numberRequired(allocatedFare.getNumberRequired())
                            .build()
            );
        }

        return deallocateFares;
    }

    private void sendRequestToAL(String flightKey, String fareType, List<DeallocateInventoryRequestBody.DeallocateFare> deallocateFares) {
        InventoryPathParams pathParams = InventoryPathParams.builder().operation("deallocations").build();

        String identifier = UUID.randomUUID().toString();
        DeallocateInventoryRequestBody bodyRequest = DeallocateInventoryRequestBody.builder()
                .uniqueIdentifier(identifier)
                .fares(Lists.newArrayList(
                        DeallocateInventoryRequestBody.Fare.builder()
                                .flightKey(flightKey)
                                .fareType(fareType)
                                .DeallocateFares(deallocateFares)
                                .build()
                ))
                .build();

        ALInventoryService deallocateRequest = alFactory.allocateInventory(new InventoryRequest(ALHeaders.getValid(identifier).build(), pathParams, bodyRequest));
        deallocateRequest.invoke();
    }

}
