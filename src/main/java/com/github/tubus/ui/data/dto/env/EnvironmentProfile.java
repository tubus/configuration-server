package com.github.tubus.ui.data.dto.env;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "environment_profile", schema = "public",
        uniqueConstraints = {
        @UniqueConstraint(name = "environment_name", columnNames = {"environment_id", "name"})
        })
public class EnvironmentProfile implements Serializable {

    private static final long serialVersionUID = -9118546419500259921L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", unique = true, updatable = false)
    private UUID id;
/*
    @NotNull
    @Pattern(regexp = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")
    @Column(name = "ip", nullable = false)
    private String ip;*/

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", unique = false, nullable = true)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE}, targetEntity = Environment.class)
    @JoinColumn(columnDefinition = "environment_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Environment environment;

    @CreationTimestamp
    @Column(name = "creation_utc", nullable = false, updatable = false)
    private Instant creationUtc;

    @UpdateTimestamp
    @Column(name = "utc", nullable = false)
    private Instant utc;
}
