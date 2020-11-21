package com.remast.baralga.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitiesRepresentation {

    private List<ActivityRepresentation> data;

    private List<ProjectRepresentation> projectRefs;

}
