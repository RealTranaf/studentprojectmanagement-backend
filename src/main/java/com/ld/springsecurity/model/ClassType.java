package com.ld.springsecurity.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum ClassType {
    CLASS_1("Đồ án 1"),
    CLASS_2("Đồ án 2"),
    CLASS_3("Đồ án 3"),
    CLASS_F("Đồ án tốt nghiệp");
    private final String displayName;

    ClassType(String displayName) {
        this.displayName = displayName;
    }

    @Override public String toString() { return displayName; }
}
