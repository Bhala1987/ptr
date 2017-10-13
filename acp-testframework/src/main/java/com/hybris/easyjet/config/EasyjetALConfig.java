package com.hybris.easyjet.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by giuseppedimartino on 21/04/17.
 */
@Component
@Getter
public class EasyjetALConfig extends EasyjetTestConfig {

    private final String alInventory;

    @Autowired
    public EasyjetALConfig(Environment environment) {
        super(environment);
        this.alInventory = environment.getProperty("al.inventory");
    }
}
