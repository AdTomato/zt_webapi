package com.authine.cloudpivot.web.api.bean.QualityAssessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 被打分人次数记录表
 * @Author Ke LongHai
 * @Date 2021/4/16 14:35
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordChart {

   private String id;

   //被打分人姓名
   private String gradedName;

   //被打分次数
   private int gradedNumber;
}
