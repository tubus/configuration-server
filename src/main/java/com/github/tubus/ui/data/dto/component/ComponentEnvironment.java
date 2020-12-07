package com.github.tubus.ui.data.dto.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import com.github.tubus.ui.data.dto.env.Environment;
import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "component_environment", schema = "public")
public class ComponentEnvironment implements Serializable  {

    private static final long serialVersionUID = -4852505979149473160L;

    @EmbeddedId
    private ComponentEnvironmentId id;

    @ManyToOne
    @MapsId("componentId")
    @JoinColumn(name = "component_id", updatable = false, insertable = false)
    private Component component;

    @ManyToOne
    @MapsId("environmentId")
    @JoinColumn(name = "environment_id", updatable = false, insertable = false)
    private Environment environment;

    @CreationTimestamp
    @Column(name = "creation_utc", nullable = false, updatable = false)
    private Instant creationUtc;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComponentEnvironmentId implements Serializable {

        private static final long serialVersionUID = 2925804211626533686L;

        @Column(name = "environment_id", updatable = false, nullable = false, unique = false)
        private UUID environmentId;

        @Column(name = "component_id", updatable = false, nullable = false, unique = false)
        private UUID componentId;
    }
}