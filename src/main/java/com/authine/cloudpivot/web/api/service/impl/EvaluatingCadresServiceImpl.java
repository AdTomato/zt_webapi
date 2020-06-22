package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.dto.EvaluatingCadreDto;
import com.authine.cloudpivot.web.api.dto.SendEvaluatingDto;
import com.authine.cloudpivot.web.api.mapper.EvaluatingCadresMapper;
import com.authine.cloudpivot.web.api.service.EvaluatingCadresService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:lfh
 * @Date: 2020/1/7 11:22
 * @Description：新选拔干部民主评议表service层
 */
@Service
@Slf4j
public class EvaluatingCadresServiceImpl implements EvaluatingCadresService {

    @Resource
    private EvaluatingCadresMapper evaluatingCadresMapper;

    /**
     * 根据id获取发起新选拔干部民主评议表的全部信息
     *
     * @param id
     * @return
     */
    @Override
    public EvaluatingCadres getEvaluatingCadresInfo(String id) {
        return evaluatingCadresMapper.getEvaluatingCadresInfo(id);
    }

    /**
     * 更新发起新选拔干部民主评议表主表结果
     *
     * @param info
     */
    @Override
    public void updateEvaluatingCadresInfo(Map<String, Object> info) {
        evaluatingCadresMapper.updateEvaluatingCadresInfo(info);
    }

    /**
     * 根据unit获取从0到最大投票人数的新选拔干部民主评议表的id
     *
     * @param map
     * @return
     */
    @Override
    public List<String> getEvaluatingCadresIdByUnit(Map map) {
        return evaluatingCadresMapper.getEvaluatingCadresIdByUnit(map);
    }

    /**
     * 获取全部的发起新选拔干部民主评议表的 评测干部表信息
     *
     * @param id
     * @return
     */
    @Override
    public List<SEvaluatingCadresList> getAllSEvaluatingCadresListData(String id) {
        return evaluatingCadresMapper.getAllSEvaluatingCadresListData(id);
    }

    /**
     * 获取全部的新选拔干部民主评议表的 评测干部表信息
     *
     * @param id
     * @return
     */
    @Override
    public List<EvaluatingCadresList> getAllEvaluatingCadresListData(String id) {
        return evaluatingCadresMapper.getAllEvaluatingCadresListData(id);
    }

    /**
     * 更新 发起新选拔干部民主评议表的评测干部表结果
     *
     * @param sec
     */
    @Override
    public void updateAllEvaluatingCadres(List<SEvaluatingCadresList> sec) {
        evaluatingCadresMapper.updateAllEvaluatingCadres(sec);
    }

    /**
     * @param id:
     * @Author: wangyong
     * @Date: 2020/3/27 14:55
     * @return: com.authine.cloudpivot.web.api.dto.SendEvaluatingDto
     * @Description:
     */
    @Override
    public SendEvaluatingDto getSendEvaluatingCadreDtoById(String id) {
        return evaluatingCadresMapper.getSendEvaluatingCadreDtoById(id);
    }

    /**
     * @param id:
     * @Author: wangyong
     * @Date: 2020/3/27 14:55
     * @return: com.authine.cloudpivot.web.api.bean.SendEvaluatingCadreList
     * @Description:
     */
    @Override
    public List<SendEvaluatingCadreList> getSendEvaluatingCadreListByParentId(String id) {
        return evaluatingCadresMapper.getSendEvaluatingCadreListByParentId(id);
    }

    @Override
    public void insertEvaluatingCadre(List<EvaluatingCadre> evaluatingCadres) {
        evaluatingCadresMapper.insertEvaluatingCadre(evaluatingCadres);
    }

    @Override
    public void insertEvaluatingCadreList(List<EvaluatingCadreList> evaluatingCadreLists) {
        evaluatingCadresMapper.insertEvaluatingCadreList(evaluatingCadreLists);
    }

    @Override
    @Transactional
    public void insertEvaluatingCadreData(List<EvaluatingCadre> evaluatingCadres, List<EvaluatingCadreList> evaluatingCadreLists) {
        log.info("插入新选拔干部民主评议表主表数据");
        insertEvaluatingCadre(evaluatingCadres);
        log.info("插入新选拔干部民主评议表子表数据");
        insertEvaluatingCadreList(evaluatingCadreLists);
    }

    @Override
    @Transactional
    public void updateEvaluatingCadre(String unit, List<EvaluatingCadresList> evaluatingCadresLists) {
        SendEvaluatingDto sendEvaluatingDto = getSendEvaluatingCadreDtoById(unit);
        if (sendEvaluatingDto != null) {
            List<SendEvaluatingCadreList> sendEvaluatingCadreLists = sendEvaluatingDto.getSendEvaluatingCadreLists();
            Map<String, SendEvaluatingCadreList> data = new HashMap<>();
            for (SendEvaluatingCadreList sendEvaluatingCadreList : sendEvaluatingCadreLists) {
                data.put(sendEvaluatingCadreList.getId(), sendEvaluatingCadreList);
            }
            for (EvaluatingCadresList evaluatingCadresList : evaluatingCadresLists) {
                SendEvaluatingCadreList send = data.get(evaluatingCadresList.getPId());
                if (send != null) {
                    updatePerspective(send, evaluatingCadresList);
                    updateIfThereIsA(send, evaluatingCadresList);
                }
            }
            sendEvaluatingDto.setVotePeoples(sendEvaluatingDto.getVotePeoples() + 1);
            log.info("更新发起新选拔干部民主评议表主表内容");
            updateSendEvaluatingCadreVotePeople(sendEvaluatingDto.getId(), sendEvaluatingDto.getVotePeoples());
            log.info("更新发起新选拔干部民主评议表子表内容");
            updateSendEvaluatingCadreList(sendEvaluatingCadreLists);
            log.info("数据更新完毕");
        } else {
            throw new RuntimeException("根据" + unit + "没有查询到数据");
        }
    }

    @Override
    public void updateSendEvaluatingCadreVotePeople(String id, Double votePeople) {
        evaluatingCadresMapper.updateSendEvaluatingCadreVotePeople(id, votePeople);
    }

    @Override
    public void updateSendEvaluatingCadreList(List<SendEvaluatingCadreList> evaluatingCadreList) {
        evaluatingCadresMapper.updateSendEvaluatingCadreList(evaluatingCadreList);
    }

    @Override
    public void updateEvaluatingCadre2(String id) {
        List<EvaluatingCadreDto> allEvaluatingCadreDatas = evaluatingCadresMapper.getAllEvaluatingCadreDatas(id);
        List<SendEvaluatingCadreList> sendEvaluatingCadreListByParentId = evaluatingCadresMapper.getSendEvaluatingCadreListByParentId(id);
        Map<String, SendEvaluatingCadreList> data = new HashMap<>();
        for (SendEvaluatingCadreList sendEvaluatingCadreList : sendEvaluatingCadreListByParentId) {
            sendEvaluatingCadreList.setSatisfiedPoll(0D);
            sendEvaluatingCadreList.setBasicSatisfiedPoll(0D);
            sendEvaluatingCadreList.setNoSatisfiedPoll(0D);
            sendEvaluatingCadreList.setNoUnderstandPoll(0D);
            sendEvaluatingCadreList.setExistencePoll(0D);
            sendEvaluatingCadreList.setNoExistencePoll(0D);
            sendEvaluatingCadreList.setINoUnderstandPoll(0D);
            data.put(sendEvaluatingCadreList.getId(), sendEvaluatingCadreList);
        }
        for (EvaluatingCadreDto allEvaluatingCadreData : allEvaluatingCadreDatas) {
            for (EvaluatingCadreList evaluatingCadreList : allEvaluatingCadreData.getEvaluatingCadreLists()) {
                SendEvaluatingCadreList sendEvaluatingCadreList = data.get(evaluatingCadreList.getPId());
                if (sendEvaluatingCadreList != null) {
                    if (evaluatingCadreList.getSatisfiedPoll() == 1) {
                        sendEvaluatingCadreList.setSatisfiedPoll(sendEvaluatingCadreList.getSatisfiedPoll() + 1);
                    } else if (evaluatingCadreList.getBasicSatisfiedPoll() == 1) {
                        sendEvaluatingCadreList.setBasicSatisfiedPoll(sendEvaluatingCadreList.getBasicSatisfiedPoll() + 1);
                    } else if (evaluatingCadreList.getNoSatisfiedPoll() == 1) {
                        sendEvaluatingCadreList.setNoSatisfiedPoll(sendEvaluatingCadreList.getNoSatisfiedPoll() + 1);
                    } else if (evaluatingCadreList.getNoUnderstandPoll() == 1) {
                        sendEvaluatingCadreList.setNoUnderstandPoll(sendEvaluatingCadreList.getNoUnderstandPoll() + 1);
                    }
                    if (evaluatingCadreList.getNoExistencePoll() == 1) {
                        sendEvaluatingCadreList.setNoExistencePoll(sendEvaluatingCadreList.getNoExistencePoll() + 1);
                    } else if (evaluatingCadreList.getExistencePoll() == 1) {
                        sendEvaluatingCadreList.setExistencePoll(sendEvaluatingCadreList.getExistencePoll() + 1);
                    } else if (evaluatingCadreList.getINoUnderstandPoll() == 1) {
                        sendEvaluatingCadreList.setINoUnderstandPoll(sendEvaluatingCadreList.getINoUnderstandPoll() + 1);
                    }
                }
            }
        }
        log.info("更新发起新选拔干部民主评议表主表内容");
        updateSendEvaluatingCadreVotePeople(id, Double.parseDouble(allEvaluatingCadreDatas.size() + ""));
        log.info("更新发起新选拔干部民主评议表子表内容");
        updateSendEvaluatingCadreList(sendEvaluatingCadreListByParentId);
        log.info("数据更新完毕");
    }

    /**
     * @param send:
     * @param evaluatingCadresList:
     * @Author: wangyong
     * @Date: 2020/3/28 0:03
     * @return: void
     * @Description: 更新对任用该干部看法
     */
    private void updatePerspective(SendEvaluatingCadreList send, EvaluatingCadresList evaluatingCadresList) {
        log.info("更新对任用该干部看法");
        switch (evaluatingCadresList.getPerspective()) {
            case "满意":
                send.setSatisfiedPoll(send.getSatisfiedPoll() + 1);
                break;
            case "基本满意":
                send.setBasicSatisfiedPoll(send.getBasicSatisfiedPoll() + 1);
                break;
            case "不满意":
                send.setNoSatisfiedPoll(send.getNoSatisfiedPoll() + 1);
                break;
            case "不了解":
                send.setNoUnderstandPoll(send.getNoUnderstandPoll() + 1);
                break;
        }
    }

    /**
     * @Author: wangyong
     * @Date: 2020/3/28 0:11
     * @param send:
     * @param evaluatingCadresList:
     * @return: void
     * @Description: 更新是否存在拉票、跑官、要官行为
     */
    private void updateIfThereIsA(SendEvaluatingCadreList send, EvaluatingCadresList evaluatingCadresList) {
        log.info("更新是否存在拉票、跑官、要官行为");
        switch (evaluatingCadresList.getIfThereIsA()) {
            case "不存在":
                send.setNoExistencePoll(send.getNoExistencePoll() + 1);
                break;
            case "存在":
                send.setExistencePoll(send.getExistencePoll() + 1);
                break;
            case "不了解":
                send.setINoUnderstandPoll(send.getINoUnderstandPoll() + 1);
                break;
        }
    }
}
