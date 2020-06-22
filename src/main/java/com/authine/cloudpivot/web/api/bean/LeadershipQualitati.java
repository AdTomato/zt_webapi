package com.authine.cloudpivot.web.api.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dom4j.tree.BaseElement;

import java.util.Date;

/**
 * @author wangyong
 * @time 2020/6/4 21:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadershipQualitati extends BaseBean {

    private String unit;

    private Date date;

    private String commentPerson;

}
