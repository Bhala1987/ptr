package com.hybris.easyjet.database.hybris.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by marco on 23/02/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BundleTemplateModel {

    private String id;
    private String name;
    private String description;
    private String fareconditions;
    private String gdsFareClass;
    private String language;

}
