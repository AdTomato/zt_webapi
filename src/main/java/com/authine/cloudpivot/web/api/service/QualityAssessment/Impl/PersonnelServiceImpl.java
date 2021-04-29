package com.authine.cloudpivot.web.api.service.QualityAssessment.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.authine.cloudpivot.web.api.bean.QualityAssessment.InspectionPersonnel;
import com.authine.cloudpivot.web.api.mapper.QualityAssessment.PersonnelMapper;
import com.authine.cloudpivot.web.api.service.IOrgUserService;
import com.authine.cloudpivot.web.api.service.QualityAssessment.PersonnelService;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 中铁考核人员维护
 * @Author Ke LongHai
 * @Date 2021/4/14 13:11
 * @Version 1.0
 */
@Service
public class PersonnelServiceImpl implements PersonnelService {

    @Resource
    PersonnelMapper personnelMapper;

    @Resource
    IOrgUserService iOrgUserService;


    @Override
    public synchronized  List<InspectionPersonnel> returnPersonnel() {

        List<InspectionPersonnel> list = new ArrayList<InspectionPersonnel>();
        List<InspectionPersonnel> personnelList = personnelMapper.getPersonnelS();

        for (InspectionPersonnel personnel : personnelList) {
            if (personnel.getLogic() == 0){
                //"peopleName": "[{\"id\":\"02937e48d1d141888e76f0c4e0e98442\",\"type\":3}]",
                String peopleName = personnel.getPeopleName();
//            String s = peopleName.substring(8,40);
//            String name = iOrgUserService.getOrgUserById(s).getName();
                personnel.setPeopleName(peopleName);
                list.add(personnel);
            }

        }
        Collections.shuffle(list);
        return list;
    }

}
