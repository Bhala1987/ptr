package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.dao.MessageDao;
import com.hybris.easyjet.fixture.hybris.invoke.response.AgentLoginResponse;
import lombok.NoArgsConstructor;
import net.thucydides.core.annotations.Step;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rajakm on 08/05/2017.
 */
@NoArgsConstructor
public class AgentLoginAssertion extends Assertion<AgentLoginAssertion, AgentLoginResponse> {
    private List<String> dbCode;

    public AgentLoginAssertion(AgentLoginResponse agentLoginResponse) {

        this.response = agentLoginResponse;
    }


    public void setResponse(AgentLoginResponse agentLoginResponse) {

        this.response = agentLoginResponse;
    }


    private MessageDao messageDao = MessageDao.getMessageDaoFromSpring();

    public AgentLoginAssertion theLoginWasSuccesful() {

        assertThat(response.getAgentAuthenticationConfirmation().getAgent().getAgentId()).isNotEmpty();
        assertThat(response.getAgentAuthenticationConfirmation().getAuthentication()).isNotNull();
        assertThat(response.getAgentAuthenticationConfirmation().getAuthentication().getTokenType()).isEqualTo("bearer");
        return this;
    }

    @Step("All today messages are returned")
    public AgentLoginAssertion theMessageIsReturned() {

        assertThat(response.getAgentAuthenticationConfirmation().getMessages()).isNotNull();
        List<String> respondCode =response.getAgentAuthenticationConfirmation().getMessages().stream().map(AgentLoginResponse.Message::getCode).collect(Collectors.toList());
        dbCode = messageDao.getMessageOfTheday();
        assertThat(respondCode).withFailMessage("The latest message is not included in the response").containsExactlyInAnyOrder(dbCode.toArray((new String[dbCode.size()])));
        return this;
    }

    @Step("No more than five old messages are returned")
    public AgentLoginAssertion noMorethenFiveMessage() {
        List<String> respondCode =response.getAgentAuthenticationConfirmation().getMessages().stream().map(AgentLoginResponse.Message::getCode).collect(Collectors.toList());
        assertThat(respondCode.size()).withFailMessage("More than five old messages are returned").isLessThanOrEqualTo(dbCode.size()+5);

        return this;
    }

}
