package com.centent.core.util;

import org.junit.jupiter.api.Test;

import java.io.File;

class FileUtilTest {

    @Test
    void getFileAsBase64() {
        File file = new File("D:\\Storage\\e187559f9d7bfd1748f56f1d8ce9933f0fc5436d23c740b285bb169c7894ab5e.png");
        String base64 = FileUtil.getFileAsBase64(file, true);
        System.out.println(base64);
    }
}