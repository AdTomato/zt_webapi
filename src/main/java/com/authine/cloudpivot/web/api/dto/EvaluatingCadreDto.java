package com.authine.cloudpivot.web.api.dto;

import com.authine.cloudpivot.web.api.bean.EvaluatingCadre;
import com.authine.cloudpivot.web.api.bean.EvaluatingCadreList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wangyong
 * @Date:2020/3/27 14:34
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EvaluatingCadreDto extends EvaluatingCadre {

    private List<EvaluatingCadreList> evaluatingCadreLists;

}
