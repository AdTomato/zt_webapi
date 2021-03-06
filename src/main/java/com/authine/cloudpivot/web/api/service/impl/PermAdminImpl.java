package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.PermAdminMapper;
import com.authine.cloudpivot.web.api.service.IPermAdmin;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: wangyong
 * @Date: 2019-12-24 14:35
 * @Description:
 */
@Service
public class PermAdminImpl implements IPermAdmin {

    @Resource
    PermAdminMapper permAdminMapper;

    /**
     * @param userId :
     * @return : java.lang.String
     * @Author: wangyong
     * @Date: 2019/12/24 14:39
     * @Description: 根据用户id返回管理员的id值
     */
    @Override
    public String getPermAdminIdByUserId(String userId) {
        return permAdminMapper.getPermAdminByUserId(userId);
    }
}
