package com.sobczyk.testThing.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.sobczyk.testThing.config.BucketConfig;
import com.sobczyk.testThing.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileAction {

    private final AmazonFileStore amazonFileStore;
    private final AmazonS3 s3;
    private final FileRepository fileRepository;
    private final Validation validation;

    @Autowired
    public FileAction(AmazonFileStore amazonFileStore, AmazonS3 s3, FileRepository fileRepository, Validation validation) {
        this.amazonFileStore = amazonFileStore;
        this.s3 = s3;
        this.fileRepository = fileRepository;
        this.validation = validation;
    }

    public ResponseEntity<String> uploadImage(MultipartFile file, String bucketName) {
        if (!this.validation.isValidImage(file)) {
            return ResponseEntity.badRequest().body("File cannot be empty and must be in jpeg, png or gif format");
        }
        Map<String, String> metadata = this.getFileMetadata(file);
        String path;
        if (bucketName != null && !bucketName.isEmpty()) {
            if (this.s3.doesBucketExistV2(bucketName)) {
                path = String.format("%s", bucketName);
            } else {
                return ResponseEntity.badRequest().body("Bucket " + bucketName + " does not exists.");
            }
        } else {
            path = String.format("%s/%s", BucketConfig.MY_BUCKET.getName(), BucketConfig.FOLDER_NAME.getName());
        }
        String filename = String.format("%s", file.getOriginalFilename());
        try {
            this.amazonFileStore.upload(path, filename, Optional.of(metadata), file.getInputStream());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Problem occured.");
        }
        return ResponseEntity.ok().body("File has been saved in bucket (or folder): " + path);
    }

    private Map<String, String> getFileMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    public ResponseEntity<?> downloadImages(String bucketName) {
        List<byte[]> files = new ArrayList<>();
        ObjectListing objects;
        boolean defaultBucket = false;
        if (bucketName != null && !bucketName.isEmpty()) {
            if (this.s3.doesBucketExistV2(bucketName)) {
                objects = this.s3.listObjects(bucketName);
            } else {
                return ResponseEntity.badRequest().body("Bucket " + bucketName + " does not exists.");
            }
        } else {
            objects = this.s3.listObjects(BucketConfig.MY_BUCKET.getName(), BucketConfig.FOLDER_NAME.getName());
            defaultBucket = true;
        }
        while (true) {
            List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
            if (objectSummaries.size() < 1) {
                break;
            }
            for (S3ObjectSummary item : objectSummaries) {
                if (!item.getKey().endsWith("/")) {
                    if (defaultBucket) {
                        String path = String.format("%s/%s",
                                BucketConfig.MY_BUCKET.getName(),
                                BucketConfig.FOLDER_NAME.getName());
                        files.add(this.amazonFileStore.download(path, item.getKey().replace(BucketConfig.FOLDER_NAME.getName() + "/", "")));
                    } else {
                        String path = String.format("%s",bucketName);
                        files.add(this.amazonFileStore.download(path, item.getKey()));
                    }
                }
            }
            objects = this.s3.listNextBatchOfObjects(objects);
        }
        return ResponseEntity.ok().body(files);
    }

    public ResponseEntity<String> createBucket(String bucketName) {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName.toLowerCase())
                .withCannedAcl(CannedAccessControlList.Private);
        if (this.s3.doesBucketExistV2(bucketName.toLowerCase())) {
            return ResponseEntity.badRequest().body("Bucket name already exists.");
        }
        try {
            this.s3.createBucket(createBucketRequest);
        } catch (AmazonS3Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.ok().body("Bucket " + bucketName.toLowerCase() + " has been created.");
    }

    public ResponseEntity<?> getUrls(String bucketName, Long duration) {
        List<String> urls = new ArrayList<>();
        ObjectListing objects;
        boolean defaultBucket = false;
        if (bucketName != null && !bucketName.isEmpty()) {
            if (this.s3.doesBucketExistV2(bucketName)) {
                objects = this.s3.listObjects(bucketName);
            } else {
                return ResponseEntity.badRequest().body("Bucket " + bucketName + " does not exists.");
            }
        } else {
            objects = this.s3.listObjects(BucketConfig.MY_BUCKET.getName(), BucketConfig.FOLDER_NAME.getName());
            defaultBucket = true;
        }
        while (true) {
            List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
            if (objectSummaries.size() < 1) {
                break;
            }
            for (S3ObjectSummary item : objectSummaries) {
                if (!item.getKey().endsWith("/")) {
                    if (defaultBucket) {
                        String path = String.format("%s/%s",
                                BucketConfig.MY_BUCKET.getName(),
                                BucketConfig.FOLDER_NAME.getName());
                        urls.add(this.amazonFileStore.getUrl(path, item.getKey().replace(BucketConfig.FOLDER_NAME.getName() + "/", ""), duration));
                    } else {
                        String path = String.format("%s",bucketName);
                        urls.add(this.amazonFileStore.getUrl(path, item.getKey(), duration));
                    }
                }
            }
            objects = this.s3.listNextBatchOfObjects(objects);
        }
        return ResponseEntity.ok().body(urls);
    }
}
