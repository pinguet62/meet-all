package fr.pinguet62.meetall.provider.happn.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static java.util.Arrays.stream;

public enum HappnRelation {

    NEW_RELATION(0),
    ACCEPTED_BY_ME(1), // but not (yet) by target
    MATCHED(2),
    DISCUSSING(4);

    private final int value;

    HappnRelation(int value) {
        this.value = value;
    }

    @JsonCreator
    public static HappnRelation fromValue(int input) {
        return stream(values())
                .filter(it -> it.value == input)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown relation value " + input));
    }

    @JsonValue
    public int toValue() {
        return value;
    }

}
