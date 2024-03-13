package com.centent.ocr;

import com.centent.core.util.FileUtil;
import com.centent.ocr.bean.Idcard;
import com.centent.ocr.enums.Direction;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public abstract class IOCR {

    public abstract Idcard idcard(String base64, Direction direction);

    public abstract String vehicleLicence(String base64, Direction direction);

    public final Idcard idcard(Direction direction, MultipartFile image) {
        String base64 = FileUtil.getFileAsBase64(image, true);
        return this.idcard(base64, direction);
    }

    public final Idcard idcard(Direction direction, File image) {
        String base64 = FileUtil.getFileAsBase64(image, true);
        return this.idcard(base64, direction);
    }

    public final Idcard idcard(Direction direction, String filePath) {
        String base64 = FileUtil.getFileAsBase64(filePath, true);
        return this.idcard(base64, direction);
    }
}
