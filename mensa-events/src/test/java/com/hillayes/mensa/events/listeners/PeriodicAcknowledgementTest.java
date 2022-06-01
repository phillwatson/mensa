package com.hillayes.mensa.events.listeners;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PeriodicAcknowledgementTest {
    private PeriodicAcknowledgement fixture;

    @Test
    public void test2() {
        Map<String, List<Integer>> pending = new HashMap<>(Map.of(
            "a", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            "b", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            "c", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            "d", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            "e", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        ));

        Map<String, Set<Integer>> acked = new HashMap<>(Map.of(
            "a", new HashSet<>(Set.of(0, 1, 2, 3, 4, 5, 6, 7, 10)),
            "b", new HashSet<>(Set.of(3, 4, 5, 6, 8, 9, 10)),
            "c", new HashSet<>(Set.of(0, 1, 2, 5, 6, 7, 8, 9, 10)),
            "d", new HashSet<>(Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
            "e", new HashSet<>(Set.of(2))
        ));

        Map<String, Integer> queued = new HashMap<>();
        acked.forEach((partition, offsets) -> {
            AtomicInteger offset = new AtomicInteger(-1);
            List<Integer> outstanding = pending.computeIfPresent(partition, (k, v) -> v.stream()
                .sorted()
                .dropWhile(n -> offsets.contains(n) && offset.updateAndGet(l -> (l < 0 || l + 1 == n) ? n : l) == n)
                .collect(Collectors.toList())
            );

            if ((offset.get() >= 0) && (outstanding != null)) {
                pending.put(partition, outstanding);
                offsets.retainAll(outstanding);
                queued.put(partition, offset.get());
            }
        });

        System.out.println(pending);
        System.out.println(acked);
        System.out.println(queued);
    }
}
