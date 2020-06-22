package com.authine.cloudpivot.web.api.parameter;

import com.authine.cloudpivot.web.api.bean.EvaluatingCadresList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:wangyong
 * @Date:2020/3/27 23:48
 * @Description: 执行新选拔干部民主评议表更新方法参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluatingCadresUpdate {

    private String unit;
    private List<EvaluatingCadresList> evaluatingCadresLists;

}
