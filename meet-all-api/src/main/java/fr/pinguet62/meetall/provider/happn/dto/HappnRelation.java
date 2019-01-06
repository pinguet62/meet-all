package fr.pinguet62.meetall.provider.happn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum HappnRelation {

    // new relation
    @JsonProperty("0")
    NEW_RELATION,

    // accepted by me but not (yet) by target
    @JsonProperty("1")
    ACCEPTED,

    // matched
    @JsonProperty("4")
    MATCHED;

}
