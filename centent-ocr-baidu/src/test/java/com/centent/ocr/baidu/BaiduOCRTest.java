package com.centent.ocr.baidu;

import com.centent.core.util.FileUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class BaiduOCRTest {

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    private static final String API_KEY = "QjCxqWRpCTczl04valxbut38";
    private static final String SECRET_KEY = "vq88FQSglCMnryie7pdBDEkoovIrlDnk";
    private static final String ACCESS_TOKEN = "24.2c60257c3303204c358a29a6cb2b1904.2592000.1712989915.282335-56176550";

    @Test
    void idcard() throws IOException {
        File file = new File("D:\\Storage\\e187559f9d7bfd1748f56f1d8ce9933f0fc5436d23c740b285bb169c7894ab5e.png");
        String base64 = FileUtil.getFileAsBase64(file, true);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = RequestBody.create(mediaType, "id_card_side=front&detect_risk=false&detect_quality=false&detect_photo=false&detect_card=false&detect_direction=false&image=" + base64);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token=" + ACCESS_TOKEN)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());
    }
}