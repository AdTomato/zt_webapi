package com.authine.cloudpivot.web.api.bean.fourgoodleadgroup;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author zhengshihao
 * @Date: 2021/01/28 17:28
 */
@Data
public class SubmitFourGoodAssessChild {
    /**
     *考核项目
     */
    @JsonAlias(value = "assess_index")
    private String assessIndex;
    /**
     *考核内容
     */
    @JsonAlias(value = "assess_content")
    private String assessContent;

    /**
     *分数
     */
    private BigDecimal score;
}
