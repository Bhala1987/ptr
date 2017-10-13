package com.hybris.easyjet.fixture.hybris.helpers.dto.bundletemplate;

import lombok.Data;

import java.util.List;
import java.util.Map;


/**
 * Created by marco on 23/02/17.
 */
@Data
public class BundleTemplateDTO {

    private String id;
    private String gdsFareClass;

    // These maps contain the language as keys and the related localized field (name, description or farecondition) as values
    private Map<String, String> names;
    private Map<String, String> descriptions;
    private Map<String, String> fareconditions;

    private List<ProductDTO> products;

}
