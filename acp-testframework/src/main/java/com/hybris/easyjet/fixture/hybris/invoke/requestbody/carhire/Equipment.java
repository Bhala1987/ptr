package com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire;

        import lombok.Builder;
        import lombok.Getter;
        import lombok.Setter;

@Builder
@Getter
@Setter
public class Equipment {
    private String equipmentCode;
    private int quantity;
}