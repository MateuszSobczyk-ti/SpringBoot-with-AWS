package com.sobczyk.testThing.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileData {

    private String name;
    private String description;
    private String imageUrl;
    private double imageSizeInKB;
}
