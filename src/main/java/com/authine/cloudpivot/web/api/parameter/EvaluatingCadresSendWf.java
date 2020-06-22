package com.authine.cloudpivot.web.api.parameter;

import com.authine.cloudpivot.web.api.bean.SendEvaluatingCadreList;
import com.authine.cloudpivot.web.api.bean.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/27 14:51
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatingCadresSendWf {

    private String id;
    private Date date;
    private List<User> users;
    private List<SendEvaluatingCadreList> evaluatingCadresLists;

}
