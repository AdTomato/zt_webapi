package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.bean.OrgDepartment;
import com.authine.cloudpivot.web.api.bean.OrgUser;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.mapper.OrgUserMapper;
import com.authine.cloudpivot.web.api.utils.RedisUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import io.lettuce.core.models.role.RedisSentinelInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-01-08 14:19
 * @Description: 用于测试的接口
 */
@RestController
@RequestMapping("/ext/test")
@Slf4j
public class TestController extends BaseController {

    @Resource
    OrgUserMapper orgUserMapper;

    @Autowired
    RedisUtils redisUtils;

    @RequestMapping("/testGetDepart")
    public void test() {
        String userId = getUserId();
        userId = "ff8080816e3e92fb016e3e9628ff00b4";
        List<DepartmentModel> list = getOrganizationFacade().getDepartmentsByUserId(userId);
    }

    @RequestMapping("/getUserInfo")
    public ResponseResult<Object> getUserInfo(String mobile) {
        Map<String, Object> info = new HashMap<>();
        OrgUser userInfo = orgUserMapper.getUserInfo(mobile);
        if (userInfo == null) {
            return this.getErrResponseResult(null, 404L, "没有获取用户信息");
        }
        info.put("userInfo", userInfo);
        if (userInfo.getDepartmentId() == null) {
            return this.getErrResponseResult(info, 404L, "不存在部门");
        }
        OrgDepartment departmentInfo = orgUserMapper.getDepartmentInfo(userInfo.getDepartmentId());
        if (departmentInfo == null) {
            return this.getErrResponseResult(info, 404L, "不存在部门");
        }
        int i = 0;
        info.put("department" + i, departmentInfo);
        while (departmentInfo.getParentId() != null) {
            i++;
            departmentInfo = orgUserMapper.getDepartmentInfo(departmentInfo.getParentId());
            info.put("department" + i, departmentInfo);
        }
        return this.getErrResponseResult(info, 404L, "最终结果");
    }

    @GetMapping("/testRedis")
    public ResponseResult<Object> testRedis(String id) {
        String userId = "ff8080816e3e92fb016e3e9628ff00b4";
        synchronized (TestController.class) {
            if (redisUtils.hasKey(userId + "-" + id)) {
                return this.getErrResponseResult(null, 444L, "禁止重复提交");
            } else {
                redisUtils.set(userId + "-" + id, 1, 30);
            }
        }
        return this.getErrResponseResult(null, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    @GetMapping("/getRedisValue")
    public ResponseResult<Object> getRedisValue(String id) {
        Object value = null;
        String userId = "ff8080816e3e92fb016e3e9628ff00b4";
        if (redisUtils.hasKey(userId + "-" + id)) {
            value = redisUtils.get(userId + "-" + id);
        }
        return this.getErrResponseResult(value, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

}
