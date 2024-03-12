package com.centent.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IStorage {

    String upload(File file);

    String upload(MultipartFile file);

    File get(String fileName);
}
