package com.github.tubus.ui.data.dto.env;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.tubus.ui.data.dto.component.ComponentEnvironment;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "environment", schema = "public")
public class Environment implements Serializable {

    private static final long serialVersionUID = -1770722487038586567L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", unique = false, nullable = true)
    private String description;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE},
            targetEntity = EnvironmentProfile.class, mappedBy = "environment")
    private List<EnvironmentProfile> profiles;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE}, targetEntity = ComponentEnvironment.class, mappedBy = "environment")
    @JsonIgnore
    @ToString.Exclude
    private List<ComponentEnvironment> components;

    @CreationTimestamp
    @Column(name = "creation_utc", nullable = false, updatable = false)
    private Instant creationUtc;

    @UpdateTimestamp
    @Column(name = "utc", nullable = false)
    private Instant utc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Environment that = (Environment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}