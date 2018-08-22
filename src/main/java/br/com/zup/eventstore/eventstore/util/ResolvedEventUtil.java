package br.com.zup.eventstore.eventstore.util;

import com.github.msemys.esjc.ResolvedEvent;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ResolvedEventUtil {
    public static void printResolvedEvent(ResolvedEvent e) {
        log.info("@{} - eventId:'{}' eventType: '{}'; data: '{}'\n",
                 e.originalEventNumber(),
                 e.originalEvent().eventId,
                 e.originalEvent().eventType,
                 new String(e.originalEvent().data));
    }
}
