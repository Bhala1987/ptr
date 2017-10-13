package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractSector {
    private String code;
    private Airport departure;
    private Airport arrival;

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = false)
    public static class Airport {
        private String code;
        private String name;
        private String marketGroup;
        private String terminal;
    }

}
