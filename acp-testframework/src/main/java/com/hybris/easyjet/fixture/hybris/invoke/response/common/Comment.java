package com.hybris.easyjet.fixture.hybris.invoke.response.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by g.dimartino on 14/05/17.
 */
@Getter
@Setter
public class Comment {
    private String code;
    private String description;
    private String agentId;
    private String dateTime;
    private String updateAgentId;
    private String updateDateTime;
    private String channel;
    private String commentType;
    private String status;
}
