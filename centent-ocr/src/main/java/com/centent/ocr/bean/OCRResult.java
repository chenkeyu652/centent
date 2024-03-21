package com.centent.ocr.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface OCRResult {

    String getId();

    default void setId(String id) {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class EmptyOCRResult implements OCRResult {
        private String id;
    }
}
