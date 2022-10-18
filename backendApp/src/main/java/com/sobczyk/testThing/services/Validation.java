package com.sobczyk.testThing.services;

import com.sobczyk.testThing.repository.FileRepository;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.function.Predicate;

@Service
public class Validation {

    private FileRepository fileRepository;

    @Autowired
    public Validation(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public boolean isValidImage(MultipartFile file) {
        Predicate<MultipartFile> isNotEmpty = filePr -> !filePr.isEmpty();
        Predicate<MultipartFile> isValidFormat = filePr2 -> Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(),
                ContentType.IMAGE_PNG.getMimeType(), ContentType.IMAGE_GIF.getMimeType()).contains(filePr2.getContentType());
        return isValidFormat.and(isNotEmpty).test(file);
    }
    public boolean isUniqueName(String name) {
        return !this.fileRepository.existsByName(name);
    }

}
