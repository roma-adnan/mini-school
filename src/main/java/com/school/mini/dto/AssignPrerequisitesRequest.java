package com.school.mini.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssignPrerequisitesRequest {
    private Long courseId;
    private List<Long> prerequisiteIds;
}
