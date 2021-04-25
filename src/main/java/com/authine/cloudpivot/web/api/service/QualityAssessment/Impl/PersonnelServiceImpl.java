package com.authine.cloudpivot.web.api.service.QualityAssessment.Impl;

import com.authine.cloudpivot.web.api.bean.QualityAssessment.InspectionPersonnel;
import com.authine.cloudpivot.web.api.mapper.QualityAssessment.PersonnelMapper;
import com.authine.cloudpivot.web.api.service.QualityAssessment.PersonnelService;
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


    @Override
    public synchronized  List<InspectionPersonnel> returnPersonnel() {

        List<InspectionPersonnel> list = new ArrayList<InspectionPersonnel>();
        List<InspectionPersonnel> personnelList = personnelMapper.getPersonnelS();

        for (InspectionPersonnel personnel : personnelList) {
            if (personnel.getLogic() == 0){
                list.add(personnel);
            }
        }
        Collections.shuffle(list);
        return list;
    }



}
