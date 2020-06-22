package com.authine.cloudpivot.web.api.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class GraduateInfo {
    @ApiModelProperty(value = "姓名")
    private String userName;
    @ApiModelProperty(value = "性别")
    private String gender;
    //专业
    @ApiModelProperty(value = "院专业")
    private String facultyAndMajor;
    @ApiModelProperty(value = "专业")
    private String major;
    //院校
    @ApiModelProperty(value = "院校")
    private String schoolName;
    //民族
    @ApiModelProperty(value = "民族")
    private String nation;
    //籍贯
    @ApiModelProperty(value = "籍贯")
    private String nationPlace;
    //政治面貌
    @ApiModelProperty(value = "政治面貌")
    private String politicsStatus;
    /**出生年月 Wed Jul 01 2020 00:00:00 GMT+0800 (中国标准时间)*/
    @ApiModelProperty(value = "生日")
    private Date dateOfBirth;
    /**是否是学生干部*/
    @ApiModelProperty(value = "是否是学生干部")
    private String studentcadres;
    //意向公司
    @ApiModelProperty(value = "意向公司")
    private String intentioncompanyname;
    //邮箱
    @ApiModelProperty(value = "邮箱")
    private String email;
    //电话
    @ApiModelProperty(value = "电话")
    private String phone;
    //是否是党员
    @ApiModelProperty(value = "是否是党员")
    private String partyMember;
//    //线上简历通过后,面试评估表的处理人
//    private String adminSelector;
//    //处理人type
//    private int adminSelectorType;
    //学历
    @ApiModelProperty(value = "学历")
    private String educationBackground;
    //签约人
    @ApiModelProperty(value = "签约人")
    private String signatory;
    //签约状态
    @ApiModelProperty(value = "签约状态")
    private String status;
    //短信邮件内容
    @ApiModelProperty(value = "发送短信内容")
    private String note;
    //线上简历投递,意向公司管理员,同时也是填写面试评估表的人
    @ApiModelProperty(value = "面试评估表填写人")
    private List<User> adminSelector;
    //签约类型
    @ApiModelProperty(value = "签约类型")
    private String agreementType;
    //签约专业
    @ApiModelProperty(value = "签约专业")
    private GraduateMajor assignmentMajor;
    //签约公司
    @ApiModelProperty(value = "签约公司")
    private String contractingUnit;
}
