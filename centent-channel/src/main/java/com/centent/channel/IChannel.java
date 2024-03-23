package com.centent.channel;

import com.centent.core.enums.Channel;

public interface IChannel {

    Channel channel();

    default boolean available(String owner) {
        return false;
    }

    default Object config(String owner) {
        return null;
    }

    default void sendNotify(NotifyContext context) {

    }
}
