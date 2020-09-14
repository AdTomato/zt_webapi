package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.mapper.GraduateRecruitMapper;
import com.authine.cloudpivot.web.api.service.GraduateRecruitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class GraduateRecruitServiceImpl implements GraduateRecruitService {
    @Resource
    private GraduateRecruitMapper graduateRecruitMapper;
    @Override
    public List<Map<String,Object>> selectDropdownBox(String year,String companyId) {
        return graduateRecruitMapper.selectDropdownBox(year,companyId);
    }

    @Override
    public List<String> selectYearDropdownBox(String userName) {
        return graduateRecruitMapper.selectYearDropdownBox(userName);
    }

    @Override
    public List<Map<String,Object>> selectComDropdownBox(String year) {
        return graduateRecruitMapper.selectComDropdownBox(year);
    }

    @Override
    public BigDecimal checkremainingNum(String assignmentMajor) {
        return graduateRecruitMapper.checkremainingNum(assignmentMajor);
    }

    @Override
    public void updateremainingNum(BigDecimal bigDecimal, String assignmentMajor) {
        graduateRecruitMapper.updateremainingNum(bigDecimal,assignmentMajor);
    }

    @Override
    public List<String> selectPlanMajorList(String year, String companyId) {
        return graduateRecruitMapper.selectPlanMajorList(year,companyId);
    }

    @Override
    public List<String> getResumeId(String userName, String phone, String toJSONString) {
        return graduateRecruitMapper.getResumeId(userName,phone,toJSONString);
    }

    @Override
    public int checkDelivery(String phone, String toJSONString) {
        return graduateRecruitMapper.checkDelivery(phone,toJSONString);
    }
}
