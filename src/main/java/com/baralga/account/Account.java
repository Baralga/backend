package com.baralga.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class Account {

    @Id
    @Column("username")
    private String username;

    @Column("tenant_id")
    private String tenantId;

    @Column("password")
    private String password;

}
