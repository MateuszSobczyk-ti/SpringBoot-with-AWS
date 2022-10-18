package com.sobczyk.testThing.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.waiters.AmazonS3Waiters;
import com.amazonaws.util.IOUtils;
import com.amazonaws.waiters.Waiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class AmazonFileStore {
    private final AmazonS3 s3;

    @Autowired
    public AmazonFileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    public void upload(String path,
                       String fileName,
                       Optional<Map<String, String>> optionalMetaData,
                       InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            s3.putObject(path, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file to Amazon s3", e);
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    public String getUrl(String path, String key, Long duration) {
        Long dur = duration == null ? 30 : duration;
        var date = new Date(new Date().getTime() + dur * 1000);
        try {
            this.s3.setObjectAcl(path,key,CannedAccessControlList.PublicRead);
            return String.valueOf(this.s3.generatePresignedUrl(path, key, date));
        } catch (AmazonS3Exception e) {
            throw new IllegalStateException("Failed to get file url.", e);
        }
    }

}
