package com.hybris.easyjet.fixture.hybris.asserters;

import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.hybris.easyjet.fixture.hybris.helpers.EventMessageCreationHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.EventMessage.EventMessageResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.assertj.core.api.Assertions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tejaldudhale on 08/08/2017.
 */

public class GenerateMessageAssertion extends Assertion<GenerateMessageAssertion, EventMessageResponse> {
    private static final Logger LOG = LogManager.getLogger(EventMessageCreationHelper.class);

    public GenerateMessageAssertion(EventMessageResponse eventMessageResponse) {

        this.response = eventMessageResponse;
    }

    public void isValid(boolean isSuccess) {
        assertThat(isSuccess).isTrue();
    }

    public void isValid(ProcessingReport report) {
        if (!report.isSuccess()) {
            report.forEach(processingMessage -> {
                if (processingMessage.getLogLevel() == LogLevel.ERROR || processingMessage.getLogLevel() == LogLevel.FATAL) {
                    LOG.error("Error in Json Schema Validation" + processingMessage);
                }
            });
        }
        assertThat(report.isSuccess()).isTrue().withFailMessage("Message Generated for Event missing mandatory fields as ");
    }

    public void checkThatMessageContainsCorrectName(String message, String name) {
        Assertions.assertThat(message.contains(name)).isTrue().withFailMessage("The event does not contain the name ");
    }

    public void checkThatMessageContainsField(String field) {
        Assertions.assertThat(response.getValue().contains(field)).isTrue()
                .withFailMessage("The event does not contain the field " + field);
    }

    public void checkThatMessageContainsFieldWithValue(String field, String fieldRegex, String value) {
        checkThatMessageContainsField(field);

        boolean isValuePresent = false;

        Pattern pattern = Pattern.compile(fieldRegex);
        Matcher matcher = pattern.matcher(response.getValue());
        if (matcher.find())
        {
            isValuePresent = matcher.group(0).contains(value);
        }
        Assertions.assertThat(isValuePresent).isTrue()
                .withFailMessage("The event does not contain the field " + field + " with value: " + value);
    }

}

