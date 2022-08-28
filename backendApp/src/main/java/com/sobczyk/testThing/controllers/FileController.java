package com.sobczyk.testThing.controllers;

import com.sobczyk.testThing.services.FileAction;
import com.sobczyk.testThing.services.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping( "/uploadRDS")
    ResponseEntity<String> uploadImageRDS(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
        @RequestParam("description") String description) {
        if (name == null || name.isEmpty() || !this.fileAction.isUniqueName(name)) {
            return ResponseEntity.badRequest().body("File name cannot be empty and must be unique.");
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be empty.");
        }
        try {
            this.fileAction.uploadImageWithRDS(name, description, file);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error on the backend side.");
        }
        return ResponseEntity.ok("It's okey.");
    }

    @GetMapping("/downloadRDS")
    public List<FileResponse> downloadImageRDS() {
        return this.fileAction.downloadImagesWithRDS();
    }
}
