package com.hybris.easyjet.fixture.alei.invokers.services.factories;

import com.hybris.easyjet.config.EasyjetALConfig;
import com.hybris.easyjet.fixture.alei.invokers.requests.InventoryRequest;
import com.hybris.easyjet.fixture.alei.invokers.services.impl.ALInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * this factory class is under spring control and therefore allows autowiring of configuration and jersey client in the
 * instantiation of new 'service' objects, calling get with a specific code of request as the argument will return the correct
 * service object, ready to be controlled, modified, invoked and queried
 */
@Component
public class ALServiceFactory {

    private final EasyjetALConfig config;

    /**
     * @param config autowired configuration
     */
    @Autowired
    public ALServiceFactory(EasyjetALConfig config) {
        this.config = config;
    }

    public ALInventoryService deallocateInventory(InventoryRequest deallocationRequest) {
        return new ALInventoryService(deallocationRequest, config.getAlInventory());
    }

    public ALInventoryService allocateInventory(InventoryRequest allocationRequest) {
        return new ALInventoryService(allocationRequest, config.getAlInventory());
    }

}
