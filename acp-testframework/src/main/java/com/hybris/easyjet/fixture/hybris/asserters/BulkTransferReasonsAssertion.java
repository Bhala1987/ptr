package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.BulkTransferReasonDao;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.response.BulkTransferReasonResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.LocalizedName;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.HEADERS;
import static org.assertj.core.api.Assertions.assertThat;

public class BulkTransferReasonsAssertion extends Assertion<BulkTransferReasonsAssertion, BulkTransferReasonResponse> {

    private BulkTransferReasonDao bulkTransferReasonDao = BulkTransferReasonDao.getBulkTransferReasonDaoFromSpring();

    public BulkTransferReasonsAssertion(BulkTransferReasonResponse bulkTransferReasonResponse) {
        this.response = bulkTransferReasonResponse;
    }

    public BulkTransferReasonsAssertion theReturnedListIsRight() {
        HybrisHeaders.HybrisHeadersBuilder headersBuilder = testData.getData(HEADERS);
        HybrisHeaders headers = headersBuilder.build();
        if (StringUtils.isNotBlank(headers.getAcceptLanguage())) {
            assertThat(response.getBulkTransferReasons().stream()
                    .map(BulkTransferReasonResponse.BulkTransferReasons::getLocalizedDescriptions).flatMap(Collection::stream)
                    .map(LocalizedName::getLocale))
                    .withFailMessage("The language found does not match the language expected")
                    .allMatch(value -> value.equals(headers.getAcceptLanguage()));
        } else {
            int languages = bulkTransferReasonDao.getCountOfAllLanguages();
            for (BulkTransferReasonResponse.BulkTransferReasons reason : response.getBulkTransferReasons()) {
                assertThat(reason.getLocalizedDescriptions().size())
                        .withFailMessage("The count of all languages is incorrect")
                        .isEqualTo(languages);
            }
        }

        return this;
    }

}