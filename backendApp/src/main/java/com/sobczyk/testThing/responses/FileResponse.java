package com.sobczyk.testThing.responses;

import lombok.Data;

import java.util.List;

@Data
public class FileResponse {

    private Integer files;
    private Integer downloadCounter;
    private double totalImagesSizeInKB;
    private List<FileData> fileData;

    private FileResponse(FileResponseBuilder builder) {
        this.files = builder.files;
        this.downloadCounter = builder.downloadCounter;
        this.totalImagesSizeInKB = builder.totalImagesSizeInKB;
        this.fileData = builder.fileData;
    }

    public static class FileResponseBuilder {

        //required parameters
        private Integer files;
        private double totalImagesSizeInKB;
        private List<FileData> fileData;

        //optional parameter
        private Integer downloadCounter;

        public FileResponseBuilder(Integer files, double totalImagesSizeInKB, List<FileData> fileData) {
            this.files = files;
            this.totalImagesSizeInKB = totalImagesSizeInKB;
            this.fileData = fileData;
        }

        public FileResponseBuilder setDownloadCounter(Integer downloadCounter) {
            this.downloadCounter = downloadCounter;
            return this;
        }

        public FileResponse build() {
            return new FileResponse(this);
        }
    }
}
