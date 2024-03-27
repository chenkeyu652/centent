package com.centent.auth.sign;

import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface SignHandler {

    List<String> getSignKeys(Object... args);

    class StringArgsHandler implements SignHandler {
        @Override
        public List<String> getSignKeys(Object... args) {
            return Arrays.stream(args)
                    .filter(Objects::nonNull)
                    .map(Objects::toString)
                    .filter(Strings::isNotEmpty)
                    .toList();
        }
    }
}
