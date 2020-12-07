package com.github.tubus.ui.data.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Configuration values for specific Environment
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "configuration_value", schema = "public")
public class ConfigurationValue implements Serializable {

    private static final long serialVersionUID = 8128888952746937007L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @NotNull @NotEmpty
    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "component_configuration_id", referencedColumnName = "id", nullable = true, updatable = false)
    private ComponentConfiguration componentConfiguration;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "environment_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Environment environment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "environment_profile_id", referencedColumnName = "id", nullable = false, updatable = false)
    private EnvironmentProfile environmentProfile;
}
