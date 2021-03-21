package com.baralga;

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
@Table("project")
public class Project {

    @Id
    @Column("project_id")
    private String id;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("active")
    private Boolean active;

}
