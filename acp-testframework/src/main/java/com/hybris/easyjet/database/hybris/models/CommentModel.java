package com.hybris.easyjet.database.hybris.models;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by jamie on 05/07/2017.
 */
@Builder
@Getter
public class CommentModel {
    public static final String CUSTOMER_COMMENT_TYPE = "customerComment";
    public static final String BOOKING_COMMENT_TYPE = "bookingComment";

    private String channel;
    private String createdTS;
    private String modifiedTS;
    private String employee_uid;
    private String commenttype;

}
