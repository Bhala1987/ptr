package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedValue;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.OfferPrice;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppedimartino on 15/03/17.
 */
@Getter
@Setter
public class HoldItemsResponse extends Response {
    private List<HoldItems> holdItems = new ArrayList<>();

    @Getter
    @Setter
    public static class HoldItems {
        private String productCode;
        private List<LocalizedValue> LocalizedNames = new ArrayList<>();
        private List<LocalizedValue> localizedDescriptions = new ArrayList<>();
        private String productType;
        private String ancillaryType;
        private String currency;
        private String units;
        private String unitType;
        private List<Price> prices = new ArrayList<>();
        private String weight;
        private ProductValidity productValidity;
        private Dimensions dimensions;
        private List<Rules> rules;
    }

    @Getter
    @Setter
    public static class Price {
        private Integer qtyFrom;
        private Double basePrice;
        private OfferPrice offerPrices;
    }

    @Getter
    @Setter
    public static class ProductValidity {
        private String startDate;
        private String endDate;
    }

    @Getter
    @Setter
    public static class Dimensions {
        private String measurement;
        private Integer length;
        private Integer height;
        private Integer width;
        private Integer depth;
    }

    @Getter
    @Setter
    public static class Rules {
        private Integer maxallowed;
        private String applicablePaxType;
    }

}
