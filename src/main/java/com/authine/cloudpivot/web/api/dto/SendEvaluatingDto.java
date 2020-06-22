package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.bean.SendEvaluating;
import com.authine.cloudpivot.web.api.bean.SendEvaluatingCadreList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/27 14:32
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEvaluatingDto extends SendEvaluating {

    private List<SendEvaluatingCadreList> sendEvaluatingCadreLists;

}
