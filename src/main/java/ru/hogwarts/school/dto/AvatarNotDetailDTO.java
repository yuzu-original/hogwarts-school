package ru.hogwarts.school.dto;

import java.util.Objects;

public class AvatarNotDetailDTO {
    private String filePath;
    private Long fileSize;
    private String mediaType;

    public AvatarNotDetailDTO(String filePath, Long fileSize, String mediaType) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvatarNotDetailDTO that = (AvatarNotDetailDTO) o;
        return Objects.equals(filePath, that.filePath) && Objects.equals(fileSize, that.fileSize) && Objects.equals(mediaType, that.mediaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, fileSize, mediaType);
    }

    @Override
    public String toString() {
        return "AvatarNotDetailDTO{" +
                "filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                '}';
    }
}
