package br.com.zup.eventstore.eventstore.listener;

import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.Subscription;
import com.github.msemys.esjc.SubscriptionDropReason;
import com.github.msemys.esjc.VolatileSubscriptionListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AccountVolatileListener implements VolatileSubscriptionListener {

    @Override
    public void onEvent(Subscription subscription, ResolvedEvent event) {
        log.info("\n\n\tAccountVolatileListener: {}", event.originalEvent().eventType);
    }

    @Override
    public void onClose(Subscription subscription, SubscriptionDropReason reason, Exception exception) {
        log.info("AccountVolatileListener Subscription closed {}", reason, exception);
    }
}
