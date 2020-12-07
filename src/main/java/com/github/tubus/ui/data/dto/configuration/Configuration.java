package com.github.tubus.ui.data.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "configuration", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uk_configuration_full_path_group", columnNames = {"path", "\"group\""})
})
public class Configuration implements Serializable {

    private static final long serialVersionUID = 3571529954343865041L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @NotNull
    @Column(name = "name", unique = false, nullable = false)
    private String name;

    @NotNull
    @Column(name = "path", unique = false, nullable = false)
    private String path;

    @Column(name = "description", unique = false, nullable = true)
    private String description;

    @Column(name = "configuration_pattern_id", nullable = true)
    private UUID configurationPatternId;

    @NotNull
    @Column(name = "\"group\"", nullable = false)
    private boolean group;

    @Column(name = "parent_id", nullable = true)
    private UUID parentId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Configuration parent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}