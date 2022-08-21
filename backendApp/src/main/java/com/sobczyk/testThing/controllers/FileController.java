package com.sobczyk.testThing.controllers;

import com.sobczyk.testThing.services.FileAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/file-store")
@CrossOrigin("*")
public class FileController {
    private final FileAction fileAction;

    @Autowired
    public FileController(FileAction fileAction) {
        this.fileAction = fileAction;
    }

    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadImage(@RequestParam("file") MultipartFile file) {
        this.fileAction.uploadImage(file);
    }

    @GetMapping("/download")
    public List<byte[]> downloadImage() {
        return this.fileAction.downloadImages();
    }
}
