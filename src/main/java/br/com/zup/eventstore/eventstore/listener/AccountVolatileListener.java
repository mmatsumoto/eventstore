package br.com.zup.eventstore.eventstore.listener;

import br.com.zup.eventstore.eventstore.util.ResolvedEventUtil;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.Subscription;
import com.github.msemys.esjc.SubscriptionDropReason;
import com.github.msemys.esjc.VolatileSubscriptionListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static br.com.zup.eventstore.eventstore.util.ResolvedEventUtil.printResolvedEvent;

@Log4j2
@Component
@Order(100)
public class AccountVolatileListener implements VolatileSubscriptionListener {

    @Override
    public void onEvent(Subscription subscription, ResolvedEvent event) {
        log.info("\n\n\tAccountVolatileListener: {}", event.originalEvent().eventType);
        printResolvedEvent(event);
    }

    @Override
    public void onClose(Subscription subscription, SubscriptionDropReason reason, Exception exception) {
        log.info("AccountVolatileListener Subscription closed {} \n", reason, exception);
    }
}
