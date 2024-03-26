package com.centent.auth.sign;

import java.util.List;

public interface SignBO {

    List<String> getSignKeys(String time, String random);
}
