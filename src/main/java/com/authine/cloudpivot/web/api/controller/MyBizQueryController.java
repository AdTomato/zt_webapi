package com.authine.cloudpivot.web.api.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.authine.cloudpivot.engine.api.model.application.AppFunctionModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryActionModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryHeaderModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryModel;
import com.authine.cloudpivot.engine.api.model.permission.AdminModel;
import com.authine.cloudpivot.engine.api.model.runtime.UserFavoritesModel;
import com.authine.cloudpivot.engine.enums.type.*;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.exception.PortalException;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.task.RunTimeThreadTask;
import com.authine.cloudpivot.web.api.utils.Points;
import com.authine.cloudpivot.web.api.utils.SystemDataSetUtils;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.authine.cloudpivot.web.api.exception.ResultEnum.SCHEMA_NOT_FOUNT_ERR;

/**
 * @Author: wangyong
 * @Date: 2020-03-04 10:31
 * @Description:
 */
@RestController
@Validated
@Slf4j
@RequestMapping("/api/app/query")
@CustomizedOrigin(level = 1)
public class MyBizQueryController extends BaseController {

    private static final String BIZLIST_SCHEMACODE_NOT_EMPTY = "模型编码不能为空";
    public static final String SCHEMA_CODE_INVALID_MSG = "业务模型编码不能为空，并且必须以字母开头，只能包含字母、数字和下划线，不能超过28个字符";

    @Autowired
    private RunTimeThreadTask springThreadTask;

    @GetMapping("/get")
    public ResponseResult<BizQueryModel> get(@RequestParam(required = false) String code,
                                             @NotBlank(message = BIZLIST_SCHEMACODE_NOT_EMPTY) @RequestParam String schemaCode,
                                             @RequestParam(required = false) ClientType clientType,
                                             @RequestParam(required = false) Integer source) {
        String userId = getUserId();
        //新增一个最近使用的信息
        if (source == null) {
            source = 0;
        }
        if (SystemDataSetUtils.isCanAllShow(schemaCode)) {
            userId = Points.ADMIN_ID;
            log.debug("当前表单为所有用户可见，将其userId设置成管理员");
        }
        clientType = clientType == null ? ClientType.PC : clientType;

        UserFavoritesModel favorite = new UserFavoritesModel();
        favorite.setUserId(userId);
        favorite.setBizObjectKey(schemaCode);
        favorite.setBizObjectType(BizObjectType.BIZ_MODEL);

        springThreadTask.setFavoriteSchema(favorite);
        log.debug("\n调用引擎接口【getQueryDetail】获取列表配置信息，参数：code=【{}】,schemaCode=【{}】", code, schemaCode);
        if (StringUtils.isEmpty(code)) {
            List<BizQueryHeaderModel> bizQueryHeaders = Lists.newArrayList();
            List<BizQueryHeaderModel> bizQueryHeadersTmp = getAppManagementFacade().getBizQueryHeaders(schemaCode);
            List<BizQueryHeaderModel> bizQueryHeadersForApp = bizQueryHeadersTmp.stream().filter(fun -> Objects.equals(fun.getShowOnMobile(), Boolean.TRUE)).collect(Collectors.toList());
            List<BizQueryHeaderModel> bizQueryHeadersForPC = bizQueryHeadersTmp.stream().filter(fun -> Objects.equals(fun.getShowOnPc(), Boolean.TRUE)).collect(Collectors.toList());
            //如果是APP端返回移动端唯一点亮的那个
            if (Objects.equals(clientType, ClientType.APP)) {
                //处理历史数据
                if (CollectionUtils.isNotEmpty(bizQueryHeadersForApp)) {
                    bizQueryHeaders.addAll(bizQueryHeadersForApp);
                }
            } else {
                bizQueryHeaders.addAll(bizQueryHeadersForPC);
            }
            code = CollectionUtils.isEmpty(bizQueryHeaders) ? bizQueryHeadersTmp.get(0).getCode() : bizQueryHeaders.get(0).getCode();
        }
        //区分PC和移动端属性数据
        BizQueryModel queryModel = getAppManagementFacade().getBizQuery(schemaCode, code, clientType);
        log.debug("列表详细信息:{}", JSONObject.toJSONString(queryModel));
        if (isEmptyBizQuery(queryModel)) {
            return getOkResponseResult(null, "未获取到列表详情信息");
        }
        //处理历史数据、加上二维码打印按钮
        if (isEmptyQueryActionQrCode(queryModel)) {
            BizQueryActionModel bizQueryActionModel = new BizQueryActionModel();
            bizQueryActionModel.setActionCode(QueryActionType.QR_CODE.toString().toLowerCase());
            bizQueryActionModel.setName(QueryActionType.QR_CODE.getName());
            bizQueryActionModel.setQueryActionRelativeType(QueryActionRelativeType.BIZSHEET);
            bizQueryActionModel.setSchemaCode(schemaCode);
            bizQueryActionModel.setSortKey(5);
            bizQueryActionModel.setIcon("plus");
            bizQueryActionModel.setRelativeCode(getAppManagementFacade().getBizForms(schemaCode).get(0).getCode());
            bizQueryActionModel.setQueryActionType(QueryActionType.QR_CODE);
            queryModel.getQueryActions().add(bizQueryActionModel);
        }
        //处理应用管理员和超级管理员信息
        AdminModel managerModel = getAppAdminByUserId(userId);
        if (managerModel != null && (source == 0 || managerModel.getAdminType().equals(AdminType.SYS_MNG))) {
            return getOkResponseResult(queryModel, "获取列表详情成功");
        } else {
            //admin用户managerModel会为空，解决删除按钮丢失问题 2019年8月27日14:30:15
            if (isAdmin()) {
                return getOkResponseResult(queryModel, "获取列表详情成功");
            }
        }
        //如果是数据管理员，按钮不做过滤
        if (!isDataManagerForApp(userId, schemaCode) && CollectionUtils.isNotEmpty(queryModel.getQueryActions())) {
            List<BizQueryActionModel> actionModel = queryModel.getQueryActions();
            List<BizQueryActionModel> customButtons = actionModel.stream().filter(t -> Objects.equals(t.getQueryActionType(), QueryActionType.CUSTOM)).collect(Collectors.toList());
            parseCommonAction(userId, schemaCode, queryModel);
            //添加自定义按钮
            queryModel.getQueryActions().addAll(customButtons);
        }
        return getOkResponseResult(queryModel, "获取列表详情成功");
    }

    /**
     * @param schemaCode
     * @return json
     * @Autor LJL
     * @date: 2019-11-5
     */
    @ApiOperation(value = "查询业务模型标题(关联表单标题)", notes = "根据业务模型编码查询业务模型标题(关联表单标题)")
    @ApiImplicitParam(name = "schemaCode", value = "业务模型编码", required = true, dataType = "String", paramType = "query")
    @GetMapping(value = "/getBizModelName")
    public ResponseResult<?> getBizModelName(@RequestParam String schemaCode) {

        validateCode(schemaCode, SCHEMA_CODE_INVALID_MSG);
        AppFunctionModel appFunctionModel = getAppManagementFacade().getAppFunctionByCode(schemaCode);

        if (appFunctionModel == null) {
            throw new PortalException(SCHEMA_NOT_FOUNT_ERR.getErrCode(), SCHEMA_NOT_FOUNT_ERR.getErrMsg());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("name", appFunctionModel.getName());
        map.put("name_i18n", appFunctionModel.getName_i18n());
        return getOkResponseResult(map, "查询业务模型标题成功");
    }


    /**
     * 列表详情是否为空
     *
     * @param queryModel
     * @return
     */
    private static Boolean isEmptyBizQuery(BizQueryModel queryModel) {
        return Objects.nonNull(queryModel)
                && CollectionUtils.isEmpty(queryModel.getQuerySorts())
                && CollectionUtils.isEmpty(queryModel.getQueryConditions())
                && CollectionUtils.isEmpty(queryModel.getQueryActions())
                && CollectionUtils.isEmpty(queryModel.getQueryColumns());
    }

    /**
     * 打印二维码操作是否为空
     * QueryActions有为空情况，queryModel.getQueryActions()会出现空指针异常，历史脏数据问题
     *
     * @param queryModel
     * @return
     */
    private static Boolean isEmptyQueryActionQrCode(BizQueryModel queryModel) {
        return CollectionUtils.isEmpty(queryModel.getQueryActions().stream().filter(t -> Objects.equals(t.getQueryActionType(), QueryActionType.QR_CODE)).collect(Collectors.toList()));
    }

    /**
     * 处理查询列表普通用户按钮权限
     *
     * @param userId     用户id
     * @param schemaCode 模型编码
     * @param queryModel 查询列表
     */
    private void parseCommonAction(String userId, @NotBlank(message = BIZLIST_SCHEMACODE_NOT_EMPTY) String schemaCode, BizQueryModel queryModel) {
        AppFunctionModel appFunctionModel = getAppManagementFacade().getAppFunctionByCode(schemaCode);
        if (appFunctionModel == null) {
            log.debug("模型不存在");
            return;
        }
    }



}
