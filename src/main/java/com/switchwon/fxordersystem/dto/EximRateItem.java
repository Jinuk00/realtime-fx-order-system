package com.switchwon.fxordersystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EximRateItem(
        @JsonProperty("result") String result,
        @JsonProperty("cur_unit") String curUnit,
        @JsonProperty("ttb") String ttb,
        @JsonProperty("tts") String tts,
        @JsonProperty("deal_bas_r") String dealBaseRate
) {
}
