package com.centent.ocr;

import com.centent.ocr.enums.CardDirection;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IOCR {

    String idcard(CardDirection direction, MultipartFile image);

    String idcard(CardDirection direction, File image);

    String idcard(CardDirection direction, String filePath);
}
