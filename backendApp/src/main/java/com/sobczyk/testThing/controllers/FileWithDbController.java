package com.sobczyk.testThing.controllers;

import com.sobczyk.testThing.services.FileAction;
import com.sobczyk.testThing.responses.FileResponse;
import com.sobczyk.testThing.services.FileWithDbAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/file-store")
@CrossOrigin("*")
public class FileWithDbController {
    private final FileWithDbAction fileWithDbAction;

    @Autowired
    public FileWithDbController(FileWithDbAction fileWithDbAction) {
        this.fileWithDbAction = fileWithDbAction;
    }

    @PostMapping( "/uploadDB")
    ResponseEntity<String> uploadImageRDS(@RequestParam("file") MultipartFile file, @RequestParam("name") String name,
                                          @RequestParam("description") String description) {
        return this.fileWithDbAction.uploadImageWithRDS(name, description, file);
    }

    @GetMapping("/getImageUrlsDB")
    public FileResponse downloadImageRDS(@RequestParam(value = "duration",required = false) Long duration) {
        return this.fileWithDbAction.getUrlsRDS(duration);
    }
}
