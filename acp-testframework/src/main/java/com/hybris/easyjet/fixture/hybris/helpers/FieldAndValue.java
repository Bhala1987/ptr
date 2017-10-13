package com.hybris.easyjet.fixture.hybris.helpers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dwebb on 12/12/2016.
 */
@Builder
@Getter
@Setter
public class FieldAndValue {

	String field;
	String value;

	public FieldAndValue (String field, String value) {

		this.setField(field);
		this.setValue(value);
	}

}
