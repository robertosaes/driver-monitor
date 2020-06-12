package com.bytesw.tyu.drivers.comcel.config;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@NoArgsConstructor
@Validated
public class Properties {

	private Integer vendorCode;
	private String distributorCode;
	private long timeout;
    
	private As400System as400System;

    @Data
    @Validated
    public static class As400System {
        @NotNull
        @NotEmpty
        private String                    systemName;
        @NotNull
        @NotEmpty
        private String                    username;
        private String                    password;
        private String                    jdbcLibraries;
        @NotNull
        private Map<String, As400Program> programs;
    }

    @Data
    @Validated
    public static class As400Program {
        @NotNull
        @NotEmpty
        private String pgmLibrary;
        @NotNull
        @NotEmpty
        private String pgmName;
    }
}