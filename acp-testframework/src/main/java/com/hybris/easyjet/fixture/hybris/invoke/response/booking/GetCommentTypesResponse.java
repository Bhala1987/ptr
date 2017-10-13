package com.hybris.easyjet.fixture.hybris.invoke.response.booking;

import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhalasaravananthiruvarangamrajalakshmi on 05/07/2017.
 */
@Setter
@Getter
public class GetCommentTypesResponse extends Response {

    private List<CommentContext> commentContexts = new ArrayList<>();

    @Setter
    @Getter
    public static class CommentContext {
        private String context;
        private List<CommentType> commentTypes = new ArrayList<>();
    }

    @Setter
    @Getter
    public static class CommentType {
        private String code;
        private String name;
    }

}
