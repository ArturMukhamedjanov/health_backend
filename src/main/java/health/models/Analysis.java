package health.models;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "analysis")
@Getter
@Setter
@Builder(toBuilder = true)
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String minValue;

    @Column(nullable = false)
    private String maxValue;

    @Column(nullable = false)
    private String unit;
}
