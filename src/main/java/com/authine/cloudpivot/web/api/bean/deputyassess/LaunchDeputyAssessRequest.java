package com.authine.cloudpivot.web.api.bean.deputyassess;

import com.authine.cloudpivot.web.api.bean.DeptName;
import com.authine.cloudpivot.web.api.bean.User;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

/**
 * @Author Asuvera
 * @Date 2020/7/20 11:32
 * @Version 1.0
 */
@Data
public class LaunchDeputyAssessRequest {
        private String id ;
        private String annual;
        private DeptName dept;
        private List<User> people;
        @JsonAlias(value ="launch_deputy_asselement")
        private List<LaunchDeputyAssChild>  deputy_assesselement;
        @JsonAlias(value = "launch_deputy_judges")
        private List<LaunchJudges>  deputy_judges;
}
