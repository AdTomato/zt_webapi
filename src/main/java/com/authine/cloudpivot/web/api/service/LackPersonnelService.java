package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.web.api.bean.FinalTotalResult;
import com.authine.cloudpivot.web.api.bean.LackPersonnelAssessInfo;
import com.authine.cloudpivot.web.api.bean.LackPersonnelInfo;
import com.authine.cloudpivot.web.api.bean.LackPersonnelapplyinfo;

import java.math.BigDecimal;
import java.util.List;

public interface LackPersonnelService {
    List<LackPersonnelInfo> findLackPersonnelList(LackPersonnelAssessInfo lackPersonnelAssessInfo);

    void savescore(LackPersonnelapplyinfo lackPersonnelapplyInfo);

    void resetmaindutyscore(String applyId);

    void countscore(LackPersonnelapplyinfo lackPersonnelapplyInfo);

    FinalTotalResult countfinalscore(LackPersonnelapplyinfo lackPersonnelapplyInfo);

    void updateFinalscore(BigDecimal result, String applyId);
}
