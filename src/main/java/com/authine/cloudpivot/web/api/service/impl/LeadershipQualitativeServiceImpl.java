package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.LeadershipQualitatiDetails;
import com.authine.cloudpivot.web.api.bean.LeadershipQualitative;
import com.authine.cloudpivot.web.api.bean.LeadershipQualitativeDetails;
import com.authine.cloudpivot.web.api.bean.SendLeadershipQualitativeDetails;
import com.authine.cloudpivot.web.api.dto.LeadershipQualitatiDto;
import com.authine.cloudpivot.web.api.dto.SendLeadershipQualitativeDto;
import com.authine.cloudpivot.web.api.mapper.LeadershipQualitativeMapper;
import com.authine.cloudpivot.web.api.service.LeadershipQualitativeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/3/28 13:30
 * @Description:
 */
@Service
@Slf4j
public class LeadershipQualitativeServiceImpl implements LeadershipQualitativeService {

    @Resource
    LeadershipQualitativeMapper leadershipQualitativeMapper;

    @Override
    public void insertLeadershipQualitative(List<LeadershipQualitative> leadershipQualitatives) {
        leadershipQualitativeMapper.insertLeadershipQualitative(leadershipQualitatives);
    }

    @Override
    public void insertLeadershipQualitativeDetails(List<LeadershipQualitativeDetails> leadershipQualitativeDetails) {
        leadershipQualitativeMapper.insertLeadershipQualitativeDetails(leadershipQualitativeDetails);
    }

    @Override
    @Transactional
    public void insertLeadershipQualitativeData(List<LeadershipQualitative> leadershipQualitatives, List<LeadershipQualitativeDetails> leadershipQualitativeDetails) {
        log.info("新增领导班子定性测评表主表内容");
        insertLeadershipQualitative(leadershipQualitatives);
        log.info("新增领导班子定性测评表子表内容");
        insertLeadershipQualitativeDetails(leadershipQualitativeDetails);
    }

    @Override
    @Transactional
    public void updateQualitative(String unit, List<LeadershipQualitativeDetails> leadershipQualitativeDetails) {
        // 获取发起领导班子定性考核全表信息
        SendLeadershipQualitativeDto sendLeadershipQualitativeDto = getSendLeadershipQualitativeDto(unit);
        List<SendLeadershipQualitativeDetails> qualitativeDetails = sendLeadershipQualitativeDto.getQualitativeDetails();
        Map<String, SendLeadershipQualitativeDetails> data = new HashMap<>();
        for (SendLeadershipQualitativeDetails qualitativeDetail : qualitativeDetails) {
            data.put(qualitativeDetail.getId(), qualitativeDetail);
        }
        for (LeadershipQualitativeDetails leadershipQualitativeDetail : leadershipQualitativeDetails) {
            SendLeadershipQualitativeDetails details = data.get(leadershipQualitativeDetail.getPId());
            if (details != null) {
                if (leadershipQualitativeDetail.getGoodPoint() == 1) {
                    // 好
                    details.setGoodPoint(details.getGoodPoint() + 1);
                } else if (leadershipQualitativeDetail.getPreferablyPoint() == 1) {
                    // 较好
                    details.setPreferablyPoint(details.getPreferablyPoint() + 1);
                } else if (leadershipQualitativeDetail.getOrdinaryPoint() == 1) {
                    // 一般
                    details.setOrdinaryPoint(details.getOrdinaryPoint() + 1);
                } else if (leadershipQualitativeDetail.getPoolPoint() == 1) {
                    // 较差
                    details.setPoolPoint(details.getPoolPoint() + 1);
                }
            }
        }
        sendLeadershipQualitativeDto.setVotePeoples(sendLeadershipQualitativeDto.getVotePeoples() + 1);
        log.info("更新发起邻导班子定性评测表主表内容");
        updateSendLeadershipQualitative(sendLeadershipQualitativeDto.getId(), sendLeadershipQualitativeDto.getVotePeoples());
        log.info("更新发起领导班子定行评测表子表内容");
        updateSendLeadershipQualitativeDetails(qualitativeDetails);
    }

    @Override
    public SendLeadershipQualitativeDto getSendLeadershipQualitativeDto(String id) {
        return leadershipQualitativeMapper.getSendLeadershipQualitativeDto(id);
    }

    @Override
    public List<SendLeadershipQualitativeDetails> getSendLeadershipQualitativeDetails(String id) {
        return leadershipQualitativeMapper.getSendLeadershipQualitativeDetails(id);
    }

    @Override
    public void updateSendLeadershipQualitative(String id, Double votePeoples) {
        leadershipQualitativeMapper.updateSendLeadershipQualitative(id, votePeoples);
    }

    @Override
    public void updateSendLeadershipQualitativeDetails(List<SendLeadershipQualitativeDetails> sendLeadershipQualitativeDetails) {
        leadershipQualitativeMapper.updateSendLeadershipQualitativeDetails(sendLeadershipQualitativeDetails);
    }

    @Override
    public List<LeadershipQualitatiDto> getLeadershipQualitatiDto(String id) {
        return leadershipQualitativeMapper.getLeadershipQualitatiDto(id);
    }

    @Override
    public void updateQualitative2(String id) {
        SendLeadershipQualitativeDto sendLeadershipQualitativeDto = getSendLeadershipQualitativeDto(id);

        List<SendLeadershipQualitativeDetails> qualitativeDetails = sendLeadershipQualitativeDto.getQualitativeDetails();

        List<LeadershipQualitatiDto> leadershipQualitatiDtos = getLeadershipQualitatiDto(id);
        Double num = 0D;
        Map<String, SendLeadershipQualitativeDetails> data = new HashMap<>();
        for (SendLeadershipQualitativeDetails qualitativeDetail : qualitativeDetails) {
            qualitativeDetail.setGoodPoint(0D);
            qualitativeDetail.setOrdinaryPoint(0D);
            qualitativeDetail.setPoolPoint(0D);
            qualitativeDetail.setPreferablyPoint(0D);
            data.put(qualitativeDetail.getEvaluationItems(), qualitativeDetail);
        }

        for (LeadershipQualitatiDto leadershipQualitatiDto : leadershipQualitatiDtos) {
            List<LeadershipQualitatiDetails> leadershipQualitatiDetails = leadershipQualitatiDto.getLeadershipQualitatiDetails();
            for (LeadershipQualitatiDetails leadershipQualitatiDetail : leadershipQualitatiDetails) {
                String item = leadershipQualitatiDetail.getEvaluationItems();
                SendLeadershipQualitativeDetails qualitativeDetail = data.get(item);
                if (qualitativeDetail != null) {
                    if (leadershipQualitatiDetail.getGoodPoint() == 1) {
                        qualitativeDetail.setGoodPoint(qualitativeDetail.getGoodPoint() + 1);

                    } else if (leadershipQualitatiDetail.getOrdinaryPoint() == 1) {
                        qualitativeDetail.setOrdinaryPoint(qualitativeDetail.getOrdinaryPoint() + 1);

                    } else if (leadershipQualitatiDetail.getPoolPoint() == 1) {
                        qualitativeDetail.setPoolPoint(qualitativeDetail.getPoolPoint() + 1);

                    } else if (leadershipQualitatiDetail.getPreferablyPoint() == 1) {
                        qualitativeDetail.setPreferablyPoint(qualitativeDetail.getPreferablyPoint() + 1);
                    }
                }
            }
            num++;
        }
        sendLeadershipQualitativeDto.setVotePeoples(num);
        log.info("更新发起邻导班子定性评测表主表内容");
        updateSendLeadershipQualitative(sendLeadershipQualitativeDto.getId(), sendLeadershipQualitativeDto.getVotePeoples());
        log.info("更新发起领导班子定行评测表子表内容");
        updateSendLeadershipQualitativeDetails(qualitativeDetails);
    }
}
