package br.com.zup.eventstore.eventstore.event;

import lombok.Value;

import java.io.Serializable;

@Value
public class MoneyDepositedEvent implements Serializable {
    private String id;
    private Long value;
}
