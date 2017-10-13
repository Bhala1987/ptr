package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.booking.GetCommentTypesResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 05/07/2017.
 */
public class GetCommentTypesAssertion extends Assertion<GetCommentTypesAssertion, GetCommentTypesResponse> {

    /**
     * @param getCommentTypesResponse the response object from the getCommentTypes service
     */
    public GetCommentTypesAssertion(GetCommentTypesResponse getCommentTypesResponse) {

        this.response = getCommentTypesResponse;
    }


    public void commentContextsWereReturned() {
        assertThat(response.getCommentContexts().size()).withFailMessage("No Comment Contexts were returned.").isGreaterThan(0);
    }

    /**
     * @param commentContext the Comment Context is returned in the response object from the getCommentTypes service
     */
    public void commentContextsWereReturned(String commentContext) {
        assertThat(response.getCommentContexts().stream().filter(commentContext1 -> commentContext1.getContext().equals(commentContext))).withFailMessage("Comment Context is incorrect.");
    }

    /**
     * @param commentContext the Comment Context is returned in the response object from the getCommentTypes service
     * @param commentTypes the list of strings of comment types from getCommentTypes service response object
     */
    public void getCommentTypes(String commentContext, List<String> commentTypes) {

        List<String> getCommentTypes = response.getCommentContexts().stream().filter(commentContext1 -> commentContext1.getContext().equals(commentContext))
                .flatMap(commentType -> commentType.getCommentTypes().stream().map(code -> code.getCode()))
                .collect(Collectors.toList());

        assertThat(getCommentTypes.size() == commentTypes.size()).withFailMessage("Number of Comment Types is incorrect.").isEqualTo(true);

        commentTypes.forEach(commentType -> assertThat(getCommentTypes).contains(commentType).withFailMessage("Comment Type is incorrect."));
    }

    /**
     * @param commentContext particular string of comment type from getCommentTypes service response object when passed only one particular comment type in request as a query
     */
    public void getParticularCommentContext(String commentContext) {

        assertThat(response.getCommentContexts().stream().count()).withFailMessage("No Comment Context found.").isEqualTo(1);
        assertThat(response.getCommentContexts().stream().filter(commentContext1 -> commentContext1.getContext().equals(commentContext))).withFailMessage("No matching Comment Context found in the response.");
    }

}
