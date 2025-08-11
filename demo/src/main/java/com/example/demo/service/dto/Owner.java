package com.example.demo.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Owner {
    @com.fasterxml.jackson.annotation.JsonProperty("display_name")
    private String displayName;
    private String id;
}