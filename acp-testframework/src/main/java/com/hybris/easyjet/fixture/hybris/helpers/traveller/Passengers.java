package com.hybris.easyjet.fixture.hybris.helpers.traveller;

import com.hybris.easyjet.fixture.IRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.response.Response;
import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@XmlRootElement
//TODO it is a response or a request body?
public class Passengers extends Response implements IRequestBody {

    private List<Passenger> passengers = new ArrayList<>();

}