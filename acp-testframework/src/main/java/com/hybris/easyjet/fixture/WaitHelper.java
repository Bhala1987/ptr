package com.hybris.easyjet.fixture;

import com.hybris.easyjet.TenantBeanFactoryPostProcessor;
import com.hybris.easyjet.config.EasyjetHybrisConfig;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.awaitility.core.ConditionFactory;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;

/**
 * Created by jamie on 03/05/2017.
 */

public class WaitHelper {

    private static EasyjetHybrisConfig easyjetHybrisConfig = null;
    protected static final Logger LOG = LogManager.getLogger(HybrisService.class);

    private WaitHelper() {
    }


    /**
     * Grabs the config from spring if it has not already been initialised
     * <p>
     * Usage: Call if easyjetHybrisConfig is needed
     */
    private static void getEasyJetConfigFromSpring() {
        if (!(ofNullable(easyjetHybrisConfig).isPresent())) {
            WaitHelper.easyjetHybrisConfig = TenantBeanFactoryPostProcessor.getFactory().getBean(EasyjetHybrisConfig.class);
        }
    }

    /**
     * Retrieves a ConditionFactory configured with default delay and interval. Exits when supplied assertion passes/returns true
     * <p>
     * Usage: pollingLoop().untilAsserted(() -> {assertThat(i).isTrue()}
     */
    public static ConditionFactory pollingLoop() {
        getEasyJetConfigFromSpring();
        return await()
                .pollInSameThread()
                .timeout(easyjetHybrisConfig.getPollingLimit(), MILLISECONDS)
                .pollDelay(easyjetHybrisConfig.getPollingDelay(), MILLISECONDS)
                .pollInterval(easyjetHybrisConfig.getPollingInterval(), MILLISECONDS);
    }

    /**
     * Pauses execution using default values. Returns fluent ConditionFactory for customising
     * <p>
     * Usage: pause(); waits for pauseDuration in milliseconds
     */
    public static void pause() {
        getEasyJetConfigFromSpring();
        pause(easyjetHybrisConfig.getPauseDuration());
    }

    public static void pause(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a ConditionFactory configured with default delay and interval. Exits when supplied assertion passes/returns true
     * <p>
     * Usage: pollingLoop().untilAsserted(() -> {assertThat(i).isTrue()}
     */
    public static ConditionFactory pollingLoopForSearchBooking() {
        getEasyJetConfigFromSpring();
        return with().pollInSameThread()
                .timeout(easyjetHybrisConfig.getSearchBookingLimit(), MILLISECONDS)
                .pollInterval(easyjetHybrisConfig.getSearchBookingInterval(), MILLISECONDS)
                .pollDelay(easyjetHybrisConfig.getSearchBookingDelay(), MILLISECONDS)
                ;
    }

}
