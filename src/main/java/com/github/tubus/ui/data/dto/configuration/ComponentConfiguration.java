package com.github.tubus.ui.data.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.github.tubus.ui.data.dto.component.Component;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "component_configuration", schema = "public", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_component_id_configuration_id",
                columnNames = {"component_id", "configuration_id"})
})
public class ComponentConfiguration {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "component_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Component component;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "configuration_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Configuration configuration;

    public String createFullConfigurationName() {
        if (getConfiguration() == null || getConfiguration().getName() == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getConfiguration().getName());
        if (getConfiguration().getDescription() != null) {
            builder.append(": ").append(getConfiguration().getDescription());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentConfiguration that = (ComponentConfiguration) o;
        return Objects.equals(component, that.component) &&
                Objects.equals(configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(component, configuration);
    }
}