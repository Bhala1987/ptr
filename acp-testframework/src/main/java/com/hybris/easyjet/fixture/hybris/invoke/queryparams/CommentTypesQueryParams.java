package com.hybris.easyjet.fixture.hybris.invoke.queryparams;

import com.hybris.easyjet.fixture.IQueryParams;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 06/07/2017.
 * this class offers builder or standard getter/setter construction for query parameters for the getCommentTypes service
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CommentTypesQueryParams extends QueryParameters implements IQueryParams {

    private String commentContext;

    /**
     * get a list of parameters set
     *
     * @return a map of parameters which can be used by the jersey client
     */
    public Map<String, String> getParameters() {

        Map<String, String> queryParams = new HashMap<>();
        if (isPopulated(commentContext)) {
            queryParams.put("comment-context", commentContext);
        }
        return queryParams;
    }

}



