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
@Table("organizations")
public class Organization {

    @Id
    @Column("org_id")
    private String id;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

}
