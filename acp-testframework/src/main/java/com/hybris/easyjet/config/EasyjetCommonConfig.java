package com.hybris.easyjet.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by giuseppedimartino on 25/04/17.
 */
@Component
@Getter
public class EasyjetCommonConfig extends EasyjetTestConfig {

    @Autowired
    EasyjetCommonConfig(Environment environment) {
        super(environment);
    }

}
