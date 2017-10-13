package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by giuseppedimartino on 04/07/17.
 */
@Getter
@Setter
public class GetRefundReasonsResponse extends Response {
    private List<PrimaryRefundReasons> primaryRefundReasons;

    @Getter
    @Setter
    public static class PrimaryRefundReasons {
        private String code;
        private List<LocalizedName> localizedDescriptions;
        private List<SecondaryRefundReasons> secondaryRefundReasons;
    }

    @Getter
    @Setter
    public static class SecondaryRefundReasons {
        private String code;
        private List<LocalizedName> localizedDescriptions;
        private List<BookingType> bookingType;
    }

    @Getter
    @Setter
    public static class BookingType {
        private String code;
        private List<LocalizedName> localizedDescriptions;
    }
}