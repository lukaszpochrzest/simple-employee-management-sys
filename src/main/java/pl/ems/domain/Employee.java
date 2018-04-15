package pl.ems.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employee extends BaseEntity {

    @Column(nullable=false)
    private String forename;

    @Column(nullable=false)
    private String surname;

    @Column(nullable=false)
    private String email;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;
}