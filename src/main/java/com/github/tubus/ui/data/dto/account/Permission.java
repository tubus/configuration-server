package com.github.tubus.ui.data.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "permission", schema = "public")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1752086153935481294L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "account_id", nullable = false, insertable = true, updatable = false)
    private UUID accountId;

    @NotNull
    @ManyToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", insertable = false, updatable = false, nullable = false)
    private UserAccount account;

    @NotNull
    @Column(name = "source_name", nullable = false, updatable = true)
    private String sourceName;

    @NotNull
    @Column(name = "source_id", nullable = false, updatable = true)
    private String sourceId;

    @NotNull
    @Column(name = "read_access", nullable = false, updatable = true)
    private boolean read;

    @NotNull
    @Column(name = "edit_access", nullable = false, updatable = true)
    private boolean edit;

    @NotNull
    @Column(name = "create_access", nullable = false, updatable = true)
    private boolean create;

    @NotNull
    @Column(name = "delete_access", nullable = false, updatable = true)
    private boolean delete;

    @UpdateTimestamp
    @Column(name = "utc", nullable = false)
    private Instant utc;
}
