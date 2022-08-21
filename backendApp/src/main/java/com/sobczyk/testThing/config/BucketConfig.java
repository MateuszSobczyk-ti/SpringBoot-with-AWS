package com.sobczyk.testThing.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketConfig {
    MY_BUCKET("wuphf"),
    FOLDER_NAME("testThing");
    private final String name;
}
