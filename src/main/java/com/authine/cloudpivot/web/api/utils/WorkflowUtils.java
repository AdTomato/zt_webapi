package com.authine.cloudpivot.web.api.utils;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/27 10:48
 * @Description: 流程工具类
 */
public class WorkflowUtils {

    /**
     * @param workflowInstanceFacade: 流程实例
     * @param ids:                    用于开启流程的id
     * @param departmentId:           部门id
     * @param userId:                 用户id
     * @param workflowCode:           流程code
     * @param finishStart:            是否提交
     * @Author: wangyong
     * @Date: 2020/3/27 10:50
     * @return: void
     * @Description: 用于开始流程
     */
    public static void startWorkflow(WorkflowInstanceFacade workflowInstanceFacade, List<String> ids, String departmentId, String userId, String workflowCode, boolean finishStart) {
        for (String id : ids) {
            workflowInstanceFacade.startWorkflowInstance(departmentId, userId, workflowCode, id, finishStart);
        }
    }

}
