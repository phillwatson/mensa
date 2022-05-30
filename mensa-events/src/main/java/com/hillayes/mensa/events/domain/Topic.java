package com.hillayes.mensa.events.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum Topic {
    USER_CREATED,
    USER_ONBOARDED;

    public String topicName() {
        return name().toLowerCase();
    }

    public static Collection<String> allTopicNames() {
        return Arrays.stream(Topic.values()).map(Topic::topicName).collect(Collectors.toList());
    }
}
