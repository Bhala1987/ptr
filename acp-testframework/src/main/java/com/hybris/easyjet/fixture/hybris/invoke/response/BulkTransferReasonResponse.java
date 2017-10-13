package com.hybris.easyjet.fixture.hybris.invoke.response;

import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class BulkTransferReasonResponse extends Response {

    @Getter
    @Setter
    private List<BulkTransferReasons> bulkTransferReasons = new ArrayList<>();

    @Getter
    @Setter
    public static class BulkTransferReasons {
        private String code;
        private List<LocalizedName> localizedDescriptions = new ArrayList<>();
    }

}



