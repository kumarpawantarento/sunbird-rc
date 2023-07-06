package dev.sunbirdrc.claim.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "course",unique=true)
    private String course;

    @Column(name = "credentialName",unique=true)
    private String credentialName;

    @Column(name = "issueDate")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date issueDate;

    @Column(name = "credentialURL")
    private String credentialURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id")
    @JsonIgnore
    private Learner learner;


}
