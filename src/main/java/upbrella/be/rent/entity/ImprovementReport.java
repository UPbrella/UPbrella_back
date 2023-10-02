package upbrella.be.rent.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImprovementReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "history_id")
    private History history;
    private String content;
    private String etc;

    public static ImprovementReport createFromReturn(History history, String content) {

        return ImprovementReport.builder()
                .history(history)
                .content(content)
                .build();
    }
}
