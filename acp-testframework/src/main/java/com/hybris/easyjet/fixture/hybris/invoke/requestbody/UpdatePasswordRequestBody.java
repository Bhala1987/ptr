package com.hybris.easyjet.fixture.hybris.invoke.requestbody;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by marco on 10/02/17.
 */
@Builder
@Getter
@Setter
public class UpdatePasswordRequestBody implements IRequestBody {

    private String newPassword;
    private String currentPassword;
    private String passwordResetToken;

}
