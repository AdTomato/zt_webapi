package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.web.api.bean.DeptName;
import com.authine.cloudpivot.web.api.bean.LeadPerson;
import com.authine.cloudpivot.web.api.bean.deputyassess.Dept;
import com.authine.cloudpivot.web.api.bean.deputyassess.Header;
import com.authine.cloudpivot.web.api.bean.deputyassess.LaunchDeputyAssChild;
import com.authine.cloudpivot.web.api.bean.deputyassess.SubmitDeputyAssChild;
import com.authine.cloudpivot.web.api.mapper.DeputyAssessMapper;
import com.authine.cloudpivot.web.api.service.DeputyAssessService;
import io.reactivex.internal.util.BlockingIgnoringReceiver;
import org.bouncycastle.asn1.x509.V2AttributeCertificateInfoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Deputy assess service.
 *
 * @Author Asuvera
 * @Date 2020 /7/20 17:04
 * @Version 1.0
 */
@Service
public class DeputyAssessServiceImpl implements DeputyAssessService {
    /**
     * The Deputy assess mapper.
     */
    @Autowired
    DeputyAssessMapper deputyAssessMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertDeptDeputyAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables) {
        deputyAssessMapper.insertDeptDeputyAsselement(deptDeputyAssessTables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSubmitDeputyAsselement(SubmitDeputyAssChild submitDeputyAssChild) {
        deputyAssessMapper.insertSubmitDeputyAsselement(submitDeputyAssChild);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateDeputyAssesment(String oldParentId, String assessedPersonId) {
        List<SubmitDeputyAssChild> list = deputyAssessMapper.selectDetails(oldParentId, assessedPersonId);
//        Iterator<Map.Entry<Integer, List<SubmitDeputyAssChild>>> entries = map.entrySet().iterator();
//        while(entries.hasNext()){
//            Map.Entry<Integer, List<SubmitDeputyAssChild>> entry = entries.next();
//            Integer key = entry.getKey();
//            List<SubmitDeputyAssChild> value = entry.getValue();
//            System.out.println(key+":"+value);
//        }
        //假设10项考核,3种权重
        //indexMap是以专业分组
        Map<String, List<SubmitDeputyAssChild>> indexMap = list.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessIndex));
        for (List<SubmitDeputyAssChild> value : indexMap.values()) {
            //上面进来后是第一次循环是第一个专业所有
            //分组,Integer是权重,value是对应权重所有分数明细list
            Map<Integer, List<SubmitDeputyAssChild>> indexWeightMap = value.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getWeight));
            //分子
            BigDecimal up = new BigDecimal("0");
            //分母
            BigDecimal down = new BigDecimal("0");
            for (List<SubmitDeputyAssChild> indexWeightList : indexWeightMap.values()) {
                //上面进来后第一循环是第一个专业第一种权重所有
                BigDecimal firstIndexWeightResult = new BigDecimal("0");
                //第一个专业第一种权重求和
                for (SubmitDeputyAssChild submitDeputyAssChild : indexWeightList) {
                      firstIndexWeightResult = firstIndexWeightResult.add(submitDeputyAssChild.getScore());
                }
                //第一个专业第一种权重加权
                BigDecimal divide = firstIndexWeightResult.divide(new BigDecimal(indexWeightList.size())).multiply(new BigDecimal(indexWeightList.get(0).getWeight()) ).divide(new BigDecimal("100"));
                //分子求和
                up = up.add(divide);
                down = down.add(new BigDecimal(indexWeightList.get(0).getWeight()));
            }
            BigDecimal divideResult = up.divide(down,3,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
            System.out.println(divideResult.toPlainString());
            SubmitDeputyAssChild submitDeputyAssChild = value.get(0);
            submitDeputyAssChild.setScore(divideResult);
            Integer haveAssessresult = deputyAssessMapper.isHaveAssessresult(submitDeputyAssChild);
            if (null != haveAssessresult){
                deputyAssessMapper.updateAssessResult(submitDeputyAssChild);
            }else {
                deputyAssessMapper.insertAssessResult(submitDeputyAssChild);
            }

        }
    }

    @Override
    public List<Dept> selectDeptFromLaunch() {
        return deputyAssessMapper.selectDeptFromLaunch();
    }

    @Override
    public List<LeadPerson> selecAssessedPeopleFromLaunch(String id) {

        List<String> strings = deputyAssessMapper.selectAssessedPeopleFromLaunch(id);
        HashSet<String> set = new HashSet<>();
        for (String string : strings) {
            String substring = string.substring(1, string.length() - 1);
            String[] split = substring.split(",");
            for (String s : split) {

                set.add(s.trim());
            }
        }
        List<LeadPerson> leadPeople = deputyAssessMapper.selectAssessedPeopleByIds(set);
        return leadPeople;
    }

    @Override
    public List<SubmitDeputyAssChild> selectAssessByDeptIdAndAssessedPersonIdAndAnnual(String deptId, String assessedPersonId, String annual) {
        return deputyAssessMapper.selectAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId,assessedPersonId,annual);

    }

    @Override
    public List<String> selectAnnualFromLaunch() {
        return deputyAssessMapper.selectAnnualFromLaunch();
    }

    @Override
    public List<Dept> selectDeptFromResult() {
        return deputyAssessMapper.selectDeptFromResult();
    }

    @Override
    public List<String> selectHeaders(String deptId, String assessedPersonId, String annual) {
        return deputyAssessMapper.selectHeaders(deptId,assessedPersonId,annual);

    }










    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSectionAsselement(List<LaunchDeputyAssChild> deptDeputyAssessTables) {
        deputyAssessMapper.insertSectionAsselement(deptDeputyAssessTables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSubmitSectionAsselement(SubmitDeputyAssChild submitDeputyAssChild) {
        deputyAssessMapper.insertSubmitSectionAsselement(submitDeputyAssChild);
    }

    @Override
    public void insertOrUpdateSectionAssesment(String oldParentId, String id) {
        List<SubmitDeputyAssChild> list = deputyAssessMapper.selectSectionDetails(oldParentId,id);
//        Iterator<Map.Entry<Integer, List<SubmitDeputyAssChild>>> entries = map.entrySet().iterator();
//        while(entries.hasNext()){
//            Map.Entry<Integer, List<SubmitDeputyAssChild>> entry = entries.next();
//            Integer key = entry.getKey();
//            List<SubmitDeputyAssChild> value = entry.getValue();
//            System.out.println(key+":"+value);
//        }
        //假设10项考核,3种权重
        //indexMap是以专业分组
        Map<String, List<SubmitDeputyAssChild>> indexMap = list.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getAssessIndex));
        for (List<SubmitDeputyAssChild> value : indexMap.values()) {
            //上面进来后是第一次循环是第一个专业所有
            //分组,Integer是权重,value是对应权重所有分数明细list
            Map<Integer, List<SubmitDeputyAssChild>> indexWeightMap = value.stream().collect(Collectors.groupingBy(SubmitDeputyAssChild::getWeight));
            //分子
            BigDecimal up = new BigDecimal("0");
            //分母
            BigDecimal down = new BigDecimal("0");
            for (List<SubmitDeputyAssChild> indexWeightList : indexWeightMap.values()) {
                //上面进来后第一循环是第一个专业第一种权重所有
                BigDecimal firstIndexWeightResult = new BigDecimal("0");
                //第一个专业第一种权重求和
                for (SubmitDeputyAssChild submitDeputyAssChild : indexWeightList) {
                    firstIndexWeightResult = firstIndexWeightResult.add(submitDeputyAssChild.getScore());
                }
                //第一个专业第一种权重加权
                BigDecimal divide = firstIndexWeightResult.divide(new BigDecimal(indexWeightList.size())).multiply(new BigDecimal(indexWeightList.get(0).getWeight()) ).divide(new BigDecimal("100"));
                //分子求和
                up = up.add(divide);
                down = down.add(new BigDecimal(indexWeightList.get(0).getWeight()));
            }
            BigDecimal divideResult = up.divide(down,3,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
            System.out.println(divideResult.toPlainString());
            SubmitDeputyAssChild submitDeputyAssChild = value.get(0);
            submitDeputyAssChild.setScore(divideResult);
            Integer haveAssessresult = deputyAssessMapper.isHaveSectionAssessresult(submitDeputyAssChild);
            if (null != haveAssessresult){
                deputyAssessMapper.updateSectionAssessResult(submitDeputyAssChild);
            }else {
                deputyAssessMapper.insertSectionAssessResult(submitDeputyAssChild);
            }

        }
    }

    @Override
    public List<Dept> selectSectionDeptFromLaunch() {
        return deputyAssessMapper.selectSectionDeptFromLaunch();
    }

    @Override
    public List<LeadPerson> selectSectionAssessedPeopleFromLaunch(String id) {
        List<String> strings = deputyAssessMapper.selectSectionAssessedPeopleFromLaunch(id);
        HashSet<String> set = new HashSet<>();
        for (String string : strings) {
            String substring = string.substring(1, string.length() - 1);
            String[] split = substring.split(",");
            for (String s : split) {

                set.add(s.trim());
            }
        }
        List<LeadPerson> leadPeople = deputyAssessMapper.selectAssessedPeopleByIds(set);
        return leadPeople;
    }

    @Override
    public List<SubmitDeputyAssChild> selectSectionAssessByDeptIdAndAssessedPersonIdAndAnnual(String deptId, String assessedPersonId, String annual) {
        return deputyAssessMapper.selectSectionAssessByDeptIdAndAssessedPersonIdAndAnnual(deptId,assessedPersonId,annual);
    }

    @Override
    public List<String> selectSectionHeaders(String deptId, String assessedPersonId, String annual) {
        return deputyAssessMapper.selectSectionHeaders(deptId,assessedPersonId,annual);
    }

    @Override
    public List<Dept> selectSectionDeptFromResult() {
        return deputyAssessMapper.selectSectionDeptFromResult();
    }


}
