package com.remast.baralga.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFilter {

    private LocalDateTime start;

    private LocalDateTime end;

    private String user;

}
