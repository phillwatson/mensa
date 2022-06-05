package com.hillayes.mensa.email.templates;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

import java.util.Map;

@ConfigMapping(prefix = "mensa.emails")
public interface Config {
    @WithParentName
    Map<String,EmailConfig> emails();

    interface EmailConfig {
        String template();
        String sender();
        String subject();
    }
}
