package com.centent.auth.sign;

import java.util.List;

public interface SignHandler {

    List<String> getSignKeys(String appId, String time, Object... args);
}
