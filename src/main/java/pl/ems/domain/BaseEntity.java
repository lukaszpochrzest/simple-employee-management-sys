package pl.ems.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private UUID uuid = UUID.randomUUID();

    @Override
    public boolean equals(final Object o) {
        return this == o || o instanceof BaseEntity && Objects.equals(uuid, ((BaseEntity) o).uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
