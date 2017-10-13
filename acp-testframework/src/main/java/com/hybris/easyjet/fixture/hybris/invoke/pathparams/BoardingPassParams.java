package com.hybris.easyjet.fixture.hybris.invoke.pathparams;

import com.hybris.easyjet.fixture.IPathParameters;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.hybris.easyjet.fixture.hybris.invoke.pathparams.BoardingPassParams.GenerateBoardingPassPaths.DEFAULT;


/**
 * Created by albertowork on 5/24/17.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BoardingPassParams extends PathParameters implements IPathParameters {
    private String bookingId;
    private GenerateBoardingPassPaths path;


    @Override
    public String get() {
        if (!isPopulated(bookingId)) {
            throw new IllegalArgumentException("You must specify a bookingId for this service.");
        }

        if (path == null) {
            path = DEFAULT;
        }

        return bookingId + "/generate-boarding-pass-request";
    }

    public enum GenerateBoardingPassPaths {
        DEFAULT
    }
}
