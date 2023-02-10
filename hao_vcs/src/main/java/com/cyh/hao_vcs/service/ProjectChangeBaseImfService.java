package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.entity.ProjectChangeBaseImf;

import java.time.LocalDateTime;

public interface ProjectChangeBaseImfService {

    boolean insertProjectChangeBaseImf(Long userID, Long projectID, LocalDateTime latestUpdateTime, String latestAction);

    ProjectChangeBaseImf getProjectChangeBaseImfByID(Long projectID);

    boolean deleteProjectByID(Long projectID);
}
