package com.centent.data;

import java.io.IOException;

public interface DataService {

    String name();

    void load() throws IOException;
}
