package dev.sunbirdrc.claim.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column (name="credentials")
    private String credentials;

    @Column (name="template_URL")
    private String templateURL;
}
