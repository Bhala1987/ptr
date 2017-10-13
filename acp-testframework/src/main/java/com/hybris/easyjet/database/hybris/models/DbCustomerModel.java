package com.hybris.easyjet.database.hybris.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Getter
@Setter
@Builder
public class DbCustomerModel {
    private String id;
    private String employeeId;
    private List<SignificantOther> significantOthers;
    private List<Dependent> dependents;

    public SignificantOther getSignificantOther() {
        if (Objects.isNull(this.significantOthers)) {
            return null;
        } else {
            return this.significantOthers.get(new Random().nextInt(this.significantOthers.size()));
        }
    }

    public SignificantOther getSignificantOther(String id) {
        if (Objects.isNull(this.significantOthers)) {
            return null;
        } else {
            return this.significantOthers.stream()
                    .filter(significantOther -> significantOther.getId().equals(id))
                    .findFirst().orElse(null);
        }
    }

    public Dependent getDependent() {
        if (Objects.isNull(this.dependents)) {
            return null;
        } else {
            return this.dependents.get(new Random().nextInt(this.dependents.size()));
        }
    }

    public Dependent getDependent(String id) {
        if (Objects.isNull(this.dependents)) {
            return null;
        } else {
            return this.dependents.stream()
                    .filter(dependent -> dependent.getId().equals(id))
                    .findFirst().orElse(null);
        }
    }

    @Getter
    @Setter
    @Builder
    public static class SignificantOther {
        private String id;
    }

    @Getter
    @Setter
    @Builder
    public static class Dependent {
        private String id;
    }

}