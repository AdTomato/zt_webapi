package com.authine.cloudpivot.web.api.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPropertyModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizSchemaModel;
import com.authine.cloudpivot.engine.api.model.runtime.BizObjectModel;
import com.authine.cloudpivot.engine.api.model.runtime.UserFavoritesModel;
import com.authine.cloudpivot.engine.component.query.api.Page;
import com.authine.cloudpivot.engine.enums.type.BizObjectType;
import com.authine.cloudpivot.web.api.controller.base.BaseQueryRuntimeController;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.utils.Points;
import com.authine.cloudpivot.web.api.utils.SystemDataSetUtils;
import com.authine.cloudpivot.web.api.view.PageVO;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.authine.cloudpivot.web.api.view.runtime.FilterVO;
import com.authine.cloudpivot.web.api.view.runtime.QueryDataVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: wangyong
 * @Date: 2020-03-04 10:21
 * @Description:
 */
@RestController
@RequestMapping("/api/runtime/query")
@Slf4j
@CustomizedOrigin(level = 20)
public class MyQueryRuntimeController extends BaseQueryRuntimeController {

    @ApiOperation(value = "查询数据接口")
    @PostMapping("/list")
    public ResponseResult<PageVO<BizObjectModel>> list(@RequestBody QueryDataVO queryData) {
        validateNotEmpty(queryData.getSchemaCode(), "模型编码不能为空");
        BizSchemaModel bizSchema = getAppManagementFacade().getBizSchemaBySchemaCode(queryData.getSchemaCode());
        parseFilterVo(queryData,bizSchema);
        if (log.isDebugEnabled()) {
            log.debug("用于查询的条件为：【{}】", (Object) (queryData == null ? null : (queryData.getFilters() == null ? null : JSON.toJSONString(queryData.getFilters()))));
        }
        String userId = getUserId();
        userId = Points.ADMIN_ID;
        log.debug("用户id为={}",userId);

        if (SystemDataSetUtils.isCanAllShow(bizSchema.getCode())) {
            userId = Points.ADMIN_ID;
            log.debug("当前表单为所有用户可见，将其userId设置成管理员");
        }

        Page<BizObjectModel> data = queryBizObject(userId, queryData,false,true);
        if (data==null|| CollectionUtils.isEmpty(data.getContent())) {
            this.getOkResponseResult(new PageVO<>(org.springframework.data.domain.Page.empty()), "获取数据成功");
        }
        if (Objects.nonNull(queryData.getMobile()) && queryData.getMobile()) {
            UserFavoritesModel favorite = new UserFavoritesModel();
            favorite.setUserId(userId);
            favorite.setBizObjectKey(queryData.getSchemaCode());
            favorite.setBizObjectType(BizObjectType.BIZ_MODEL);
            getUserSettingFacade().addUserFavoriteBizModel(favorite);
        }
        return this.getOkResponseResult(new PageVO<>(data), "获取数据成功");
    }

    /**
     * 处理数据项类型
     * @param queryData
     * @param bizSchema
     */
    private void parseFilterVo(QueryDataVO queryData, BizSchemaModel bizSchema) {
        if (CollectionUtils.isNotEmpty(bizSchema.getProperties())) {
            Map<String, List<BizPropertyModel>> listMap = bizSchema.getProperties().stream().collect(Collectors.groupingBy(BizPropertyModel:: getCode));
            if (CollectionUtils.isNotEmpty(queryData.getFilters())) {
                for (FilterVO filterVO : queryData.getFilters()) {
                    if (filterVO.getPropertyType() == null) {
                        filterVO.setPropertyType(listMap.get(filterVO.getPropertyCode()).get(0).getPropertyType());
                    }
                }
            }
        }
    }


}
