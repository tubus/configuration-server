package com.github.tubus.ui.data.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account", schema = "public")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = -8882050448974908762L;

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

    @NotNull
    @Column(name = "encrypted_password", nullable = false, updatable = true)
    private String password;

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
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}