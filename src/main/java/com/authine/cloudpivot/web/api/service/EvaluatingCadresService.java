package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.*;
import com.authine.cloudpivot.web.api.dto.SendEvaluatingDto;

import java.util.List;
import java.util.Map;

/**
 * @Author:lfh
 * @Date: 2020/1/7 11:12
 * @Description： 新选拔干部民主评议表service层
 */

public interface EvaluatingCadresService {

    /**
     * 根据id获取发起新选拔干部民主评议表的全部信息
     *
     * @param id
     * @return
     */
    EvaluatingCadres getEvaluatingCadresInfo(String id);

    /**
     * 更新发起新选拔干部民主评议表主表结果
     *
     * @param info
     */
    void updateEvaluatingCadresInfo(Map<String, Object> info);

    /**
     * 根据unit获取从0到最大投票人数的新选拔干部民主评议表的id
     *
     * @param map
     * @return
     */
    List<String> getEvaluatingCadresIdByUnit(Map map);

    /**
     * 获取全部的发起新选拔干部民主评议表的 评测干部表信息
     *
     * @param id
     * @return
     */
    List<SEvaluatingCadresList> getAllSEvaluatingCadresListData(String id);

    /**
     * 获取全部的新选拔干部民主评议表的 评测干部表信息
     *
     * @param id
     * @return
     */
    List<EvaluatingCadresList> getAllEvaluatingCadresListData(String id);

    /**
     * 更新 发起新选拔干部民主评议表的评测干部表结果
     *
     * @param sec
     */
    void updateAllEvaluatingCadres(List<SEvaluatingCadresList> sec);

    /**
     * 获取发起新选拔干部民主评议表的全部内容，包含子表
     *
     * @param id
     * @return
     */
    SendEvaluatingDto getSendEvaluatingCadreDtoById(String id);


    /**
     * 获取发起新选拔干部民主评议表的子表内容
     *
     * @param id
     * @return
     */
    List<SendEvaluatingCadreList> getSendEvaluatingCadreListByParentId(String id);


    /**
     * 创建新选拔干部民主评议表主表数据
     *
     * @param evaluatingCadres
     */
    void insertEvaluatingCadre(List<EvaluatingCadre> evaluatingCadres);


    /**
     * 新选拔干部民主评议表子表数据
     *
     * @param evaluatingCadreLists
     */
    void insertEvaluatingCadreList(List<EvaluatingCadreList> evaluatingCadreLists);

    /**
     * 创建爱你新选干部民主评议表数据
     * @param evaluatingCadres
     * @param evaluatingCadreLists
     */
    void insertEvaluatingCadreData(List<EvaluatingCadre> evaluatingCadres, List<EvaluatingCadreList> evaluatingCadreLists);

    void updateEvaluatingCadre(String unit, List<EvaluatingCadresList> evaluatingCadresLists);

    void updateSendEvaluatingCadreVotePeople(String id, Double votePeople);

    void updateSendEvaluatingCadreList(List<SendEvaluatingCadreList> evaluatingCadreList);

    void updateEvaluatingCadre2(String id);
}
