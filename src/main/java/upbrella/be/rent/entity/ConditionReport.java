package upbrella.be.rent.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConditionReport {

    @Id
    @GeneratedValue
    private long id;
    @OneToOne
    @JoinColumn(name = "history_id")
    private History history;
    private String content;
    private String etc;
}
