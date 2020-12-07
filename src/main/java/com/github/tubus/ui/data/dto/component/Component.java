package com.github.tubus.ui.data.dto.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import com.github.tubus.ui.data.dto.env.Environment;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "component", schema = "public")
public class Component implements Serializable {

    private static final long serialVersionUID = 9030443947401369196L;

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

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "component_environment",
            joinColumns = @JoinColumn(name = "component_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "environment_id", referencedColumnName = "id")
    )
    private Set<Environment> environments;

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
        Component component = (Component) o;
        return id.equals(component.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}