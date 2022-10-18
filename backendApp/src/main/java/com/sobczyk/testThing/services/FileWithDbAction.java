package com.sobczyk.testThing.services;

import com.sobczyk.testThing.config.BucketConfig;
import com.sobczyk.testThing.entity.File;
import com.sobczyk.testThing.repository.FileRepository;
import com.sobczyk.testThing.responses.FileData;
import com.sobczyk.testThing.responses.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileWithDbAction {
    private final AmazonFileStore amazonFileStore;
    private FileRepository fileRepository;
    private final Validation validation;
    private int counter = 0;
    private double imagesSize = 0;

    @Autowired
    public FileWithDbAction(AmazonFileStore amazonFileStore, FileRepository fileRepository, Validation validation) {
        this.amazonFileStore = amazonFileStore;
        this.fileRepository = fileRepository;
        this.validation = validation;
    }

    public ResponseEntity<String> uploadImageWithRDS(String name, String description, MultipartFile file) {
        if (name == null || name.isEmpty() || !this.validation.isUniqueName(name)) {
            return ResponseEntity.badRequest().body("File name cannot be empty and must be unique.");
        }
        if (!this.validation.isValidImage(file)) {
            return ResponseEntity.badRequest().body("File cannot be empty and must be in jpeg, png or gif format");
        }
        Map<String, String> metadata = this.getFileMetadata(file);

        String path = String.format("%s/%s", BucketConfig.MY_BUCKET.getName(), BucketConfig.FOLDER_NAME.getName());
        String filename = String.format("%s", file.getOriginalFilename());
        try {
            this.amazonFileStore.upload(path, filename, Optional.of(metadata), file.getInputStream());
            this.fileRepository.save(new File(name, description, filename, Math.round(file.getSize()/1024.0 * 10.0)/10.0));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return ResponseEntity.ok().body("File '" + name + "' has been saved.");
    }

    private Map<String, String> getFileMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    public FileResponse getUrlsRDS(Long duration) {
        List<File> files = (List<File>) this.fileRepository.findAll();
        String path = String.format("%s/%s",
                BucketConfig.MY_BUCKET.getName(),
                BucketConfig.FOLDER_NAME.getName());
        List<FileData> filesData = files.stream()
                .map(file -> new FileData(file.getName(), file.getDescription(),
                        this.amazonFileStore.getUrl(path, file.getImageLink(), duration), file.getImageSize()))
                .collect(Collectors.toList());
        FileResponse response = new FileResponse.FileResponseBuilder(files.size(), this.getImagesSize(files), filesData)
                .setDownloadCounter(this.getCounter(files))
                .build();
        this.incrementAndSaveConsumer.accept(files);
        return response;
    }

    Function<Integer, Integer> incrementFunction = i -> i + 1;

    private Integer getCounter(List<File> files) {
        this.counter = 0;
        Function<Integer, Integer> counterFunction = i -> this.counter += i;
        Function<Integer, Integer> incrementAndCountFunction = incrementFunction.andThen(counterFunction);
        List<Integer> counterList = files
                .stream()
                .map(file -> file.getDownloadCounter())
                .collect(Collectors.toList());
        counterList.stream()
                .map(incrementAndCountFunction)
                .collect(Collectors.toList());
        return this.counter;
    }

    private double getImagesSize(List<File> files) {
        this.imagesSize = 0;
        files.forEach(file -> this.imagesSize += file.getImageSize());
        return this.imagesSize;
    }

    private final Consumer<List<File>> incrementAndSaveConsumer = files -> {
        files.forEach(file -> file.setDownloadCounter(file.getDownloadCounter() + 1));
        this.fileRepository.saveAll(files);
    };
}
