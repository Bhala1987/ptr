package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.database.hybris.models.CommentModel;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.*;
import com.hybris.easyjet.fixture.hybris.invoke.response.customer.CustomerProfileResponse;
import org.apache.commons.collections.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.EMPLOYEE_ID;
import static com.hybris.easyjet.database.hybris.models.CommentModel.CUSTOMER_COMMENT_TYPE;
import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by dwebb on 12/2/2016.
 * assertion wrapper for customer profile response object, provides reusable assertions to all tests
 */
public class CustomerProfileAssertion extends Assertion<CustomerProfileAssertion, CustomerProfileResponse> {


    public CustomerProfileAssertion(CustomerProfileResponse profileResponse) {

        this.response = profileResponse;
    }

    public CustomerProfileAssertion theProfileContainsBasicData(CustomerModel dbCustomer) {

        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getCustomerId()).matches(dbCustomer.getUid());
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getEmail()
                .toLowerCase()).isEqualTo(dbCustomer.getCustomerid().toLowerCase());
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getFirstName()).matches(dbCustomer.getFirstname());
        return this;
    }

    public CustomerProfileAssertion theProfileIsValid(String customerId) {

        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getCustomerId()).matches(customerId);
        return this;
    }

    public CustomerProfileAssertion theFullProfileHasAllBasicFields(CustomerModel dbCustomer) {

        assertThat(response.getCustomer().getBasicProfile()).isNotNull();
        CustomerProfileResponse.BasicProfile basicProfile = response.getCustomer().getBasicProfile();
        assertThat(basicProfile.getPersonalDetails().getCustomerId()).matches(dbCustomer.getUid());
        assertThat(basicProfile.getPersonalDetails().getFirstName()).matches(dbCustomer.getFirstname());
        assertThat(basicProfile.getPersonalDetails().getGroup() != null);
        assertThat(basicProfile.getPersonalDetails().getType() != null);
        assertThat(basicProfile.getPersonalDetails().getStatus().matches(dbCustomer.getStatus()));
        assertThat(basicProfile.getPersonalDetails().getTitle().matches(dbCustomer.getTitle()));
        assertThat(basicProfile.getPersonalDetails().getLastName()).matches(dbCustomer.getLastname());
        assertThat(basicProfile.getPersonalDetails().getNifNumber().matches(dbCustomer.getNifNumber()));
        assertThat(CollectionUtils.isNotEmpty(basicProfile.getPersonalDetails().getKeyDates()));
        assertThat(basicProfile.getContactAddress() != null);
        assertThat(dbCustomer.getEmployeeid() != null ? basicProfile.getPersonalDetails()
                .getEmployeeId()
                .matches(dbCustomer.getEmployeeid()) : basicProfile.getPersonalDetails().getEmployeeId() == null);

        return this;
    }

    public CustomerProfileAssertion theSignificantOtherIsSuccessfullyAdded() {

        CustomerProfileResponse.SignificantOthers significantOthers = response.getCustomer().getAdvancedProfile().getSignificantOthers();

        assertThat(significantOthers.getPassengers().size()).isEqualTo(1);
        Profile profilePassenger = significantOthers.getPassengers().get(0);

        assertThat(profilePassenger.getTitle()).isNotNull();
        assertThat(profilePassenger.getType()).isNotNull();
        assertThat(profilePassenger.getFirstName()).isNotNull();
        assertThat(profilePassenger.getLastName()).isNotNull();
        assertThat(profilePassenger.getAge()).isNotNull();

        assertThat(profilePassenger.getTitle()).isEqualTo("mr");
        assertThat(profilePassenger.getType()).isEqualTo("adult");
        assertThat(profilePassenger.getFirstName()).isEqualTo("John");
        assertThat(profilePassenger.getLastName()).isEqualTo("Smith");
        assertThat(profilePassenger.getAge()).isEqualTo(34);

        return this;
    }

    public CustomerProfileAssertion theSignificantOtherIsSuccessfullyUpdated() {

        CustomerProfileResponse.SignificantOthers significantOthers = response.getCustomer().getAdvancedProfile().getSignificantOthers();

        assertThat(significantOthers.getPassengers().size()).isEqualTo(1);
        Profile profilePassenger = significantOthers.getPassengers().get(0);

        assertThat(profilePassenger.getTitle()).isNotNull();
        assertThat(profilePassenger.getType()).isNotNull();
        assertThat(profilePassenger.getFirstName()).isNotNull();
        assertThat(profilePassenger.getLastName()).isNotNull();
        assertThat(profilePassenger.getAge()).isNotNull();

        assertThat(profilePassenger.getTitle()).isEqualTo("mr");
        assertThat(profilePassenger.getType()).isEqualTo("adult");
        assertThat(profilePassenger.getFirstName()).isEqualTo("John");
        assertThat(profilePassenger.getLastName()).isEqualTo("Dorian");
        assertThat(profilePassenger.getAge()).isEqualTo(34);

        return this;
    }

    public CustomerProfileAssertion documentIsSuccessfullyRemovedFromTheSignificantOther(String customerId, String passengerId) {

        CustomerProfileResponse.SignificantOthers significantOthers = response.getCustomer().getAdvancedProfile().getSignificantOthers();

        assertThat(significantOthers.getPassengers().size()).isEqualTo(1);
        Profile profilePassenger = significantOthers.getPassengers().get(0);
        assertThat(profilePassenger.getIdentityDocuments().size()).isEqualTo(0);

        return this;
    }

    public CustomerProfileAssertion theCompleteSignificantOtherIsSuccessfullyAdded(String email, String documentId) throws ParseException {

        CustomerProfileResponse.SignificantOthers significantOthers = response.getCustomer().getAdvancedProfile().getSignificantOthers();

        assertThat(significantOthers.getPassengers().size()).isEqualTo(1);
        Profile profilePassenger = significantOthers.getPassengers().get(0);

        assertThat(profilePassenger.getTitle()).isNotNull();
        assertThat(profilePassenger.getType()).isNotNull();
        assertThat(profilePassenger.getFirstName()).isNotNull();
        assertThat(profilePassenger.getLastName()).isNotNull();
        assertThat(profilePassenger.getAge()).isNotNull();
        assertThat(profilePassenger.getEmail()).isNotNull();
        assertThat(profilePassenger.getNifNumber()).isNotNull();
        assertThat(profilePassenger.getPhoneNumber()).isNotNull();
        assertThat(profilePassenger.getIdentityDocuments()).isNotNull();

        assertThat(profilePassenger.getTitle()).isEqualTo("mr");
        assertThat(profilePassenger.getType()).isEqualTo("adult");
        assertThat(profilePassenger.getFirstName()).isEqualTo("John");
        assertThat(profilePassenger.getLastName()).isEqualTo("Smith");
        assertThat(profilePassenger.getAge()).isEqualTo(34);
        assertThat(profilePassenger.getEmail()).isEqualTo(email);
        assertThat(profilePassenger.getNifNumber()).isEqualTo("123456789811");
        assertThat(profilePassenger.getPhoneNumber()).isEqualTo("3453453456");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-mm-yyyy");

        IdentityDocument identityDocument = profilePassenger.getIdentityDocuments().get(0);
        assertThat(identityDocument.getDocumentNumber()).isEqualTo("7487714");
        assertThat(identityDocument.getDateOfBirth()).isEqualTo("1983-01-09");
        assertThat(identityDocument.getDocumentExpiryDate()).isEqualTo("2018-06-06");
        assertThat(identityDocument.getGender()).isEqualTo("MALE");
        assertThat(identityDocument.getNationality()).isEqualTo("GBR");
        assertThat(identityDocument.getCountryOfIssue()).isEqualTo("GBR");
        assertThat(identityDocument.getDocumentType()).isEqualTo("PASSPORT");

        Name name = identityDocument.getName();
        assertThat(name.getFullName()).isEqualTo("Jim Morrison");

//        SpecialRequest savedSSRs = profilePassenger.getSavedSSRs();
//        assertThat(savedSSRs).isNotNull();
//        assertThat(savedSSRs.getSsrs()).isNotNull();
//        assertThat(savedSSRs.getSsrs().size()).isEqualTo(1);
//        assertThat(savedSSRs.getSsrs().get(0).getType()).isEqualTo("BLND");
//        assertThat(savedSSRs.getSsrs().get(0).isIsTandCsAccepted()).isEqualTo("true");

        return this;
    }

    public CustomerProfileAssertion theRemainingChangesIsCorrectlyShown() {

        CustomerProfileResponse.SignificantOthers significantOthers = response.getCustomer().getAdvancedProfile().getSignificantOthers();

        assertThat(significantOthers.getRemainingChanges()).isNotNull();

        return this;
    }

    public CustomerProfileAssertion theFullProfileHasAllAdvancedFields(CustomerModel dbCustomer, String channel, String sections) {

        assertThat(response.getCustomer().getAdvancedProfile()).isNotNull();
        CustomerProfileResponse.AdvancedProfile advancedProfile = response.getCustomer().getAdvancedProfile();
        checkBookings(channel, sections, advancedProfile);
        checkDependents(sections, advancedProfile);
        return this;
    }

    public CustomerProfileAssertion thePartialProfileHasOnlySelectedSections(CustomerModel dbCustomer, String channel, String sections) {

        assertThat(response.getCustomer().getAdvancedProfile()).isNotNull();
        CustomerProfileResponse.AdvancedProfile advancedProfile = response.getCustomer().getAdvancedProfile();
        checkSections(channel, sections, advancedProfile);
        checkDependents(sections, advancedProfile);
        return this;
    }

    private void checkSections(String channel, String sections, CustomerProfileResponse.AdvancedProfile advancedProfile) {

        checkDependents(sections, advancedProfile);
        checkSignificantOthers(channel, sections, advancedProfile);
        checkBookings(channel, sections, advancedProfile);
    }

    private void checkSignificantOthers(String channel, String sections, CustomerProfileResponse.AdvancedProfile advancedProfile) {

        if (sections.isEmpty() || sections.contains("significant")) {
            assertThat(!advancedProfile.getSignificantOthers().getRemainingChanges().isEmpty());
            assertThat("2018-01-10".equals(advancedProfile.getSignificantOthers().getChangesEndDate()));
        }
    }

    private void checkDependents(String sections, CustomerProfileResponse.AdvancedProfile advancedProfile) {

        if (sections.isEmpty() || sections.contains("dependent")) {
            assertThat(!advancedProfile.getDependents().isEmpty());
        }
    }

    private void checkBookings(String channel, String sections, CustomerProfileResponse.AdvancedProfile advancedProfile) {

        if (sections.isEmpty() || sections.contains("bookings")) {
            assertThat(!advancedProfile.getRelatedBookings().isEmpty());
            boolean check = false;
            switch (channel) {
                case "Digital":
                case "PublicApiMobile":
                case "PublicApiB2B":

                    check = advancedProfile.getRelatedBookings().size() == 2;
                    break;
                case "ADAirport":
                case "ADCustomerService":
                    check = advancedProfile.getRelatedBookings().size() == 3;
                    break;
            }
            assertThat(check);
        }
    }

    public void verifyAllReferenceDataForCustomerAreClear() {

        assertThat(Objects.isNull(response.getCustomer().getAdvancedProfile().getIdentityDocuments()));
        //assertThat(Objects.isNull(response.getCustomer().getAdvancedProfile().getSavedPayments().getSavedCards().isEmpty()));
        assertThat(Objects.isNull(response.getCustomer().getAdvancedProfile().getSavedSSRs()));
        assertThat(Objects.isNull(response.getCustomer().getAdvancedProfile().getSavedPassengers()));
    }

    public CustomerProfileAssertion theProfileContainsData() {

        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getTitle()).isEqualTo("mr");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getType()).isEqualTo("adult");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getFirstName()).isEqualTo("Tony");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getLastName()).isEqualTo("Henry");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getAge()).isEqualTo("25");
        //  assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getEjPlusCardNumber()).isEqualTo("00453560");
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getContactAddress()
                .get(0)
                .getAddressLine1()).isEqualTo("35, Main Street");
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getPhoneNumber()).isEqualTo("774012854");
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getAlternativePhoneNumber()).isEqualTo("0200123821");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getNifNumber()).isEqualTo("876765512");
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getFlightClubExpiryDate()).isEqualTo("2017-02-09");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getFlightClubId()).isEqualTo("543443");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getKeyDates()
                .get(0)
                .getType()).isEqualTo("graduation");
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getKeyDates()
                .get(0)
                .getMonth()).isEqualTo("12");
        assertThat(response.getCustomer()
                .getBasicProfile()
                .getPersonalDetails()
                .getKeyDates()
                .get(0)
                .getDay()).isEqualTo("31");
        assertThat(response.getCustomer().getBasicProfile().getPersonalDetails().getEmail()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getIdentityDocuments().get(0)).isNotNull();
        assertThat(response.getCustomer().getAdvancedProfile().getRecentSearches().get(0)).isNotNull();
        assertThat(response.getCustomer().getAdvancedProfile().getRelatedBookings().get(0)).isNotNull();

        return this;
    }

    public CustomerProfileAssertion theProfileContainsPassenger() {

        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getTitle()).isEqualTo("mr");
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getFirstName()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getLastName()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getAge()).isEqualTo(25);
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getType()).isEqualTo("adult");
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getPhoneNumber()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getNifNumber()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getEmail()).isNotEmpty();
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getIdentityDocuments()).isNotEmpty();
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getSavedSSRs()
                .getSsrs()).isNotEmpty();
        // assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getEjPlusCardNumber()).isNotEmpty();

        return this;
    }

    public CustomerProfileAssertion theProfileContainsPassenger(String passengerId) {
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getCode().equalsIgnoreCase(passengerId));
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getTitle()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getFirstName()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getLastName()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getAge()).isNotNull();
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getType()).isNotEmpty();
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getPhoneNumber()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getNifNumber()).isNotEmpty();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getEmail()).isNotEmpty();
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getIdentityDocuments()).isNotEmpty();
        assertThat(response.getCustomer()
                .getAdvancedProfile()
                .getSavedPassengers()
                .get(0)
                .getSavedSSRs()
                .getSsrs()).isNotEmpty();

        return this;
    }


    public CustomerProfileAssertion theSignificantOtherIsInCostumer(String email) throws ParseException {

        CustomerProfileResponse.SignificantOthers significantOthers = response.getCustomer().getAdvancedProfile().getSignificantOthers();

        assertThat(significantOthers.getPassengers().size()).isEqualTo(1);
        Profile profilePassenger = significantOthers.getPassengers().get(0);

        assertThat(profilePassenger.getTitle()).isNotNull();
        assertThat(profilePassenger.getType()).isNotNull();
        assertThat(profilePassenger.getFirstName()).isNotNull();
        assertThat(profilePassenger.getLastName()).isNotNull();
        assertThat(profilePassenger.getAge()).isNotNull();
        assertThat(profilePassenger.getEmail()).isNotNull();

        assertThat(profilePassenger.getTitle()).isEqualTo("mr");
        assertThat(profilePassenger.getType()).isEqualTo("adult");
        assertThat(profilePassenger.getFirstName()).isEqualTo("John");
        assertThat(profilePassenger.getLastName()).isEqualTo("Smith");
        assertThat(profilePassenger.getAge()).isEqualTo(34);
        assertThat(profilePassenger.getEmail()).isEqualTo(email);
        return this;
    }

    public CustomerProfileAssertion numberOfSavedSsrsAre(int expectedNumberOfSsrs) {
        List<SavedSSRs.Ssr> actualSsrs = response.getCustomer().getAdvancedProfile().getSavedSSRs().getSsrs();
        assertThat(actualSsrs.size()).isEqualTo(expectedNumberOfSsrs);
        return this;
    }

    public CustomerProfileAssertion savedSSRsShouldCustmerHave(List<String> expectedSsrs) {
        List<SavedSSRs.Ssr> actualSsrs = response.getCustomer().getAdvancedProfile().getSavedSSRs().getSsrs();
        assertThat(actualSsrs.contains(expectedSsrs));
        return this;
    }

    public CustomerProfileAssertion savePassengerToCustomerProfile(String surname) {
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getFirstName().isEmpty()).isFalse();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getLastName().isEmpty()).isFalse();
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().get(0).getIdentityDocuments().get(0).getDocumentType().contains("PASSPORT"));
        int noOfPassengers = response.getCustomer().getAdvancedProfile().getSavedPassengers().size();
        assertThat(noOfPassengers >= 2);

        assertThat(
                response.getCustomer().getAdvancedProfile().getSavedPassengers().stream()
                        .filter(profile -> profile.getLastName().equals(surname))
                        .findFirst()
                        .orElseThrow(AssertionError::new)
                        .getEjPlusCardNumber().isEmpty()
        ).isFalse();

        return this;
    }

    public CustomerProfileAssertion noCommentsExistInProfile() {
        assertThat(response.getCustomer().getAdvancedProfile().getComments()).isEmpty();
        return this;
    }

    public CustomerProfileAssertion commentsExistInProfile(String expectedComment, String expectedCode) {
        commentsExistInProfile(expectedComment, expectedCode, "ACTIVE");
        return this;
    }


    public CustomerProfileAssertion createdCommentFieldsAreCorrectInDatabase(CommentModel actualCommentWithId) {
        assertThat(actualCommentWithId.getChannel()).isEqualTo(testData.getChannel());
        assertThat(actualCommentWithId.getCommenttype()).isEqualTo(CUSTOMER_COMMENT_TYPE);
        assertThat(actualCommentWithId.getEmployee_uid()).isEqualTo(testData.getData(EMPLOYEE_ID));

        assertThat(actualCommentWithId.getCreatedTS()).isNotEmpty();
        return null;
    }

    public CustomerProfileAssertion updatedCommentFieldsAreCorrectInDatabase(CommentModel actualCommentWithId) {
        assertThat(actualCommentWithId.getChannel()).isEqualTo(testData.getChannel());
        assertThat(actualCommentWithId.getCommenttype()).isEqualTo(CUSTOMER_COMMENT_TYPE);
        assertThat(actualCommentWithId.getEmployee_uid()).isEqualTo(testData.getData(EMPLOYEE_ID));

        assertThat(actualCommentWithId.getModifiedTS()).isNotEmpty();
        return null;
    }


    public CustomerProfileAssertion commentsExistInProfile(String expectedComment, String expectedCode, String status) {

        Comment actualComment = response.getCustomer().getAdvancedProfile().getComments().stream()
                .filter(comment -> (comment.getCode().equals(expectedCode)))
                .findFirst().orElse(null);

        assertThat(actualComment)
                .withFailMessage("Could not find the comment in customer profile")
                .isNotNull();
        assertThat(actualComment.getDescription())
                .withFailMessage("The comment description is wrong")
                .isEqualTo(expectedComment);
        assertThat(actualComment.getStatus())
                .withFailMessage("The comment status is wrong")
                .isEqualTo(status);

        return this;
    }

    public CustomerProfileAssertion commentsAreNotShowed() {
        assertThat(response.getCustomer().getAdvancedProfile().getComments())
                .withFailMessage("The getCustomerProfile returned comments")
                .isEmpty();
        return this;
    }

    public CustomerProfileAssertion paymentMethodIsPopulated(){
        assertThat(response.getCustomer().getAdvancedProfile().getSavedPayments().getDebitCards().size()>1)
                .withFailMessage("no payment method saved to the profile");

        assertThat(response.getCustomer().getAdvancedProfile().getSavedPayments().getDebitCards().get(0).getPaymentMethod().contains("DM"));
        return this;
    }

    public void noFlightInterestsInCustomerProfile(){
            assertThat(response.getCustomer().getAdvancedProfile().getFlightInterests().isEmpty())
                    .withFailMessage("Expected Flight Interest(s) were not removed").isTrue();
    }

    public CustomerProfileAssertion theSavedPassengerHasBeenCreated(int numOfSavedPassenger, boolean isCreatedOrUpdate) {
        if(isCreatedOrUpdate) {
            assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().size())
                    .withFailMessage("No saved passenger has been created against the customer profile").isGreaterThan(numOfSavedPassenger);
        } else {
            assertThat(response.getCustomer().getAdvancedProfile().getSavedPassengers().size())
                    .withFailMessage("No saved passenger has been created against the customer profile").isEqualTo(numOfSavedPassenger);
        }
        return this;
    }

    public CustomerProfileAssertion theSavedPassengerHasBeenUpdateInField(String oldValue, String newValue) {
        assertThat(oldValue)
                .withFailMessage("The value has not been update as expected")
                .isNotEqualTo(newValue);

        return this;
    }
}
