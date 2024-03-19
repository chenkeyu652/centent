package com.centent.channel;

import com.centent.core.enums.Channel;

public interface IChannel {

    Channel channel();

    default void sendNotify(NotifyContext context) {

    }
}
