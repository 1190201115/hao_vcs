package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.ApplyJoinProject;
import com.cyh.hao_vcs.entity.Message;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.entity.User;

import java.util.List;
import java.util.Map;

public interface ProjectBaseService {

    R insertBaseProject(ProjectBaseImf baseImf, Long userID, int status);

    List<Long> getAllProjectID(Long userID);

    List<Long> getSelfProjectID(Long userID);

    List<Long> getJoinProjectID(Long userID);

    List<ProjectBaseImf> getProjects(List<Long> idList);

    String getProjectPath(Long projectId);

    List<ProjectBaseImf> getProjectByKey(String key, long userId);

    User getOwner(Long projectId);

    boolean applyJoin(Long projectId, Long userID, String content);

    int checkApplyNum(long userId);

    int checkReceiveApplyNum(long userId);

    Map<String, List<Message>> checkAllApply(long userId);

    List<Integer> getLikeList(long userId, List<ProjectBaseImf> projectBaseImfList);

    boolean changeLikedStatus(long userId, long projectId, Integer newLikedStatus);

    List<ProjectBaseImf> getLikeProject(long userId);

    boolean setReply(ApplyJoinProject applyJoinProject);

}
