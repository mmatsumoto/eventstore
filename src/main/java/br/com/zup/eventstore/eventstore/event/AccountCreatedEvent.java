package br.com.zup.eventstore.eventstore.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.io.Serializable;

@Value
public class AccountCreatedEvent implements Serializable {
    private String id;
    private String name;
    private Long balance;
}
