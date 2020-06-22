package com.authine.cloudpivot.web.api.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.authine.cloudpivot.engine.api.model.application.AppFunctionModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPropertyModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizSchemaModel;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.engine.enums.type.BizPropertyType;
import com.authine.cloudpivot.web.api.controller.app.BizSchemaController;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.exception.PortalException;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.authine.cloudpivot.web.api.view.app.BizPropertyVO;
import com.authine.cloudpivot.web.api.view.app.SubSchemaVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangyong
 * @Date: 2020-03-04 09:55
 * @Description: 重写数据项控制类
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/api/app/bizproperty")
@CustomizedOrigin(level = 1)
public class MyBizPropertyController extends BaseController {

    @ApiImplicitParams({
            @ApiImplicitParam(name = "schemaCode", value = "业务模型编码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isPublish", value = "是否已发布：null 查询所有，true 已发布，false 未发布",
                    dataType = "Boolean", defaultValue = "null", allowableValues = "{null,true,false}", paramType = "query")
    })
    @GetMapping("/list")
    public ResponseResult<List<BizPropertyVO>> list(@RequestParam String schemaCode,
                                                    @RequestParam(required = false) Boolean isPublish) {
        if (log.isDebugEnabled()) {
            log.debug("\n调用引擎接口【getBizSchemaBySchemaCode()】,参数：schemaCode=【{}】,isPublish=【{}】", schemaCode, isPublish);
        }
        validateCode(schemaCode, BizSchemaController.SCHEMA_CODE_INVALID_MSG);

        final AppFunctionModel functionModel = getAppManagementFacade().getAppFunctionByCode(schemaCode);
        if (functionModel == null) {
            throw new PortalException(ErrCode.APP_FUNCTION_MODEL_NOTEXIST.getErrCode(), ErrCode.APP_FUNCTION_MODEL_NOTEXIST.getErrMsg());
        }

        BizSchemaModel bizSchemaModel = getAppManagementFacade().getBizSchemaBySchemaCode(schemaCode, isPublish);
        if (bizSchemaModel == null) {
            throw new PortalException(ErrCode.BIZ_SCHEMA_MODEL_NOT_EXIST.getErrCode(), ErrCode.BIZ_SCHEMA_MODEL_NOT_EXIST.getErrMsg());
        }

        List<BizPropertyModel> properties = bizSchemaModel.getProperties();
        List<BizPropertyVO> propertyVOList = assemblePropertyModel(properties);

        return getOkResponseResult(propertyVOList, "查询数据项列表成功");
    }

    /**
     * 递归获取关联表单的信息
     *
     * @param properties
     * @return
     */
    private List<BizPropertyVO> assemblePropertyModel(List<BizPropertyModel> properties) {
        List<BizPropertyVO> propertyVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(properties)) {
            return propertyVOList;
        }
        for (BizPropertyModel property : properties) {
            BizPropertyVO bizPropertyVO = new BizPropertyVO();
            BeanUtils.copyProperties(property, bizPropertyVO);

            if (BizPropertyType.WORK_SHEET.equals(property.getPropertyType()) &&
                    StringUtils.isNotBlank(property.getRelativeCode())) {
                Map<String, String> map = disposeRelativeWorksheet(property);
                String relativeName = map.get("relativeName");
                String appPackageCode = map.get("appPackageCode");
                String appFunctionCode = map.get("appFunctionCode");

                if (StringUtils.isNotBlank(relativeName)) {
                    bizPropertyVO.setRelativeName(relativeName);
                }

                if (StringUtils.isNotBlank(appPackageCode)) {
                    bizPropertyVO.setAppPackageCode(appPackageCode);
                }
                if (StringUtils.isNotBlank(appFunctionCode)) {
                    bizPropertyVO.setAppFunctionCode(appFunctionCode);
                }
            }

            if (BizPropertyType.CHILD_TABLE.equals(property.getPropertyType())) {
                BizSchemaModel bizSchemaModel = property.getSubSchema();
                if (bizSchemaModel != null) {
                    SubSchemaVO subSchemaVO = new SubSchemaVO();
                    subSchemaVO.setVoproperties(assemblePropertyModel(bizSchemaModel.getProperties()));
                    bizPropertyVO.setSubSchemaVO(subSchemaVO);
                }
            }
            propertyVOList.add(bizPropertyVO);
        }
        return propertyVOList;
    }

    /**
     * 处理关联子表的返回信息
     *
     * @param property
     * @return
     */
    private Map<String, String> disposeRelativeWorksheet(BizPropertyModel property) {
        Map<String, String> map = new HashMap<>();
        BizSchemaModel schemaModel = getAppManagementFacade().getBizSchemaBySchemaCode(property.getRelativeCode());
        if (schemaModel != null) {
            map.put("relativeName", schemaModel.getName());
        }
        AppFunctionModel functionModel = getAppManagementFacade().getAppFunctionByCode(property.getRelativeCode());
        if (functionModel != null) {
            map.put("appPackageCode", functionModel.getAppCode());
            AppFunctionModel function = getAppManagementFacade().getAppFunction(functionModel.getParentId());
            if (function != null) {
                map.put("appFunctionCode", function.getCode());
            }
        }
        return map;
    }
}
