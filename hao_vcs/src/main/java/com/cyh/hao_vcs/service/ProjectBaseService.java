package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.ProjectBaseImf;

import java.util.List;

public interface ProjectBaseService {

    R insertBaseProject(ProjectBaseImf baseImf, Long userID, int status);

    List<Long> getAllProjectID(Long userID);

    List<Long> getSelfProjectID(Long userID);

    List<Long> getJoinProjectID(Long userID);

    List<ProjectBaseImf> getProjects(List<Long> idList);

    String getProjectPath(Long projectId);
}
