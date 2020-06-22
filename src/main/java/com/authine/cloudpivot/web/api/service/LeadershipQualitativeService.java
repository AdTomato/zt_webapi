package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.LeadershipQualitative;
import com.authine.cloudpivot.web.api.bean.LeadershipQualitativeDetails;
import com.authine.cloudpivot.web.api.bean.SendLeadershipQualitativeDetails;
import com.authine.cloudpivot.web.api.dto.LeadershipQualitatiDto;
import com.authine.cloudpivot.web.api.dto.SendLeadershipQualitativeDto;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/28 13:29
 * @Description: 领导班子定性测评表service
 */
public interface LeadershipQualitativeService {

    void insertLeadershipQualitative(List<LeadershipQualitative> leadershipQualitatives);

    void insertLeadershipQualitativeDetails(List<LeadershipQualitativeDetails> leadershipQualitativeDetails);

    void insertLeadershipQualitativeData(List<LeadershipQualitative> leadershipQualitatives, List<LeadershipQualitativeDetails> leadershipQualitativeDetails);

    void updateQualitative(String unit, List<LeadershipQualitativeDetails> leadershipQualitativeDetails);

    SendLeadershipQualitativeDto getSendLeadershipQualitativeDto(String id);

    List<SendLeadershipQualitativeDetails> getSendLeadershipQualitativeDetails(String id);

    void updateSendLeadershipQualitative(String id, Double votePeoples);

    void updateSendLeadershipQualitativeDetails(List<SendLeadershipQualitativeDetails> sendLeadershipQualitativeDetails);

    List<LeadershipQualitatiDto> getLeadershipQualitatiDto(String id);

    void updateQualitative2(String id);

}
