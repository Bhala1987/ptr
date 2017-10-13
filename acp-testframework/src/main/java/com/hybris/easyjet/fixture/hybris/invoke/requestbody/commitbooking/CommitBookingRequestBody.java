package com.hybris.easyjet.fixture.hybris.invoke.requestbody.commitbooking;

import com.hybris.easyjet.fixture.IRequestBody;
import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
@Builder
public class CommitBookingRequestBody implements IRequestBody {

	private String         		  basketCode;
	private String                bookingType;
	private String                bookingReason;
	private BasketContent         basketContent;
	private boolean               overrideWarning;
	private CustomerDeviceContext customerDeviceContext;
	private List<PaymentMethod>   paymentMethods;
	private List<RefundOrFee>   refundsAndFees;

}
