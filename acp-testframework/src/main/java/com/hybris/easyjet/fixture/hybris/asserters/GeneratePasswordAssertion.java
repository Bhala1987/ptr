package com.hybris.easyjet.fixture.hybris.asserters;

import com.hybris.easyjet.fixture.hybris.invoke.response.customer.operationconfirmation.GeneratePasswordResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by robertadigiorgio on 13/02/2017.
 */
public class GeneratePasswordAssertion extends Assertion<GeneratePasswordAssertion, GeneratePasswordResponse> {

    private String passwordGenerate;

    public GeneratePasswordAssertion(GeneratePasswordResponse generatePasswordResponse) {

        this.response = generatePasswordResponse;
    }

    private static int lenghtPassword(String password) {

        int point = 0;
        int passwordLenght = password.length();

        if (passwordLenght > 7) {
            point += 5;
        }
        if (passwordLenght > 14) {
            point += 5;
        }
        return point;
    }

    private static int occurrenceCharacters(String password) {

        int point = 0;

        int occurenceCharacters = occurenceCounter(password, "[a-zA-Z]");
        int occurenceSpecialCharactersAndNumber = occurenceCounter(password, "[~!\\-$@_%\\/\\^=;:*\"'{}<>?\\[\\]\\(\\).,?0-9]");

        if (occurenceCharacters > 0) {
            point += 5;
        }
        if (occurenceCharacters > 1) {
            point += 5;
        }
        if (occurenceSpecialCharactersAndNumber > 0) {
            point += 10;
        }
        if (occurenceSpecialCharactersAndNumber > 1) {
            point += 10;
        }
        return point;
    }

    private static int occurenceCounter(String password, String pattern) {

        int count = 0;

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(password);

        while (m.find()) {
            count++;
        }

        return count;
    }

    private static int findSequences(String password) {

        int points = 0;

        SequenceType currentSequence = SequenceType.NONE;
        int sequenceLength = 0;

        for (int i = 1; i < password.length(); i++) {
            SequenceType found = findSequence(password.charAt(i - 1), password.charAt(i));

            if (!SequenceType.NONE.equals(found)) {
                if (SequenceType.NONE.equals(currentSequence)) {
                    currentSequence = found;
                    sequenceLength = 2;
                } else if (found.equals(currentSequence)) {
                    sequenceLength++;
                } else {
                    if (sequenceLength >= 3) {
                        points -= 5;
                        currentSequence = SequenceType.NONE;
                        sequenceLength = 0;
                    } else {
                        currentSequence = found;
                        sequenceLength = 2;
                    }
                }
            } else {
                if (sequenceLength >= 3) {
                    points -= 5;
                }
                currentSequence = SequenceType.NONE;
                sequenceLength = 0;
            }
        }
        if (sequenceLength >= 3) {
            points -= 5;
        }
        return points;
    }

    private static SequenceType findSequence(char prev, char curr) {

        if (!isValidCharacter(prev) || !isValidCharacter(curr)) {
            return SequenceType.NONE;
        }
        if (prev == curr) {
            return SequenceType.EQUAL;
        }
        if (curr == prev + 1) {
            return SequenceType.ASCENDING;
        }
        if (curr == prev - 1) {
            return SequenceType.DESCENDING;
        } else {
            return SequenceType.NONE;
        }
    }

    private static boolean isValidCharacter(char c) {

        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    public GeneratePasswordAssertion passwordIsStrongEnough() {

        int point = 0;

        passwordGenerate = response.getGeneratePasswordConfirmation().getPassword();

        point += lenghtPassword(passwordGenerate);
        point += occurrenceCharacters(passwordGenerate);
        point += findSequences(passwordGenerate);

        assertThat(point).isGreaterThan(24);

        return this;
    }

    public GeneratePasswordAssertion fieldIsEmpty(Integer row) {

        assertThat(row).isEqualTo(0);
        return this;
    }

    public GeneratePasswordAssertion fieldIsEmpty(String row) {

        if (row == null) {
            assertThat(row).isEqualTo(null);
        } else {
            assertThat(row).isEqualTo("0");
        }
        return this;
    }

    enum SequenceType {
        ASCENDING,
        DESCENDING,
        EQUAL,
        NONE
    }
}






