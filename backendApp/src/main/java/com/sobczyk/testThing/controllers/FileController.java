package com.sobczyk.testThing.controllers;

import com.sobczyk.testThing.services.FileAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/file-store")
@CrossOrigin("*")
public class FileController {
    private final FileAction fileAction;

    @Autowired
    public FileController(FileAction fileAction) {
        this.fileAction = fileAction;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("Helllllo");
    }

    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam(value = "bucket_name",required = false) String bucketName) {
        return this.fileAction.uploadImage(file, bucketName);
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadImage(@RequestParam(value = "bucket_name",required = false) String bucketName) {
        return this.fileAction.downloadImages(bucketName);
    }

    @GetMapping("/getImageUrls")
    public ResponseEntity<?> downloadImageUrl(@RequestParam(value = "bucket_name",required = false) String bucketName,
                                              @RequestParam(value = "duration",required = false) Long duration) {
        return this.fileAction.getUrls(bucketName,duration);
    }

    @PostMapping("/createBucket")
    ResponseEntity<String> createBucket(@RequestParam("bucket_name") String bucketName) {
        return this.fileAction.createBucket(bucketName);
    }

}
