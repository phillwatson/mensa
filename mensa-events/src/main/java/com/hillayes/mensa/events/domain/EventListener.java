package com.hillayes.mensa.events.domain;

import java.util.function.Consumer;

public interface EventListener extends Consumer<EventPacket> {
}
