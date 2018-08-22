package br.com.zup.eventstore.eventstore.util;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.ResolvedEvent;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class EventUtil {
    public static void printResolvedEvent(ResolvedEvent e) {
        log.info("@{} - eventId:'{}' eventType: '{}'; data: '{}'\n",
                 e.originalEventNumber(),
                 e.originalEvent().eventId,
                 e.originalEvent().eventType,
                 new String(e.originalEvent().data));
    }

    public static String eventsToString(List<EventData> events) {
        return events.stream()
                .map(e -> new String(e.data))
                .collect(Collectors.toList())
                .toString();
    }
}
