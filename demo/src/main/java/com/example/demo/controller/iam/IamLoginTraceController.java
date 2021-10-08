package com.example.demo.controller.iam;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.vo.*;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.Operation;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.vo.IamLoginTraceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录日志
 *
 * @author MyName
 * @version 1.0
 * @date 2021-09-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/loginTrace")
@Slf4j
@BindPermission(name = "登录日志")
public class IamLoginTraceController extends BaseCrudRestController<IamLoginTrace> {
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 查询分页数据
     *
     * @return
     * @throws Exception
     */
		@Log(operation = Operation.LABEL_LIST)
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamLoginTrace entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamLoginTraceVO.class);
    }

    /**
     * 加载更多数据
     * @return
     * @throws Exception
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore(ModelMap modelMap) throws Exception {
        // 获取关联数据字典AUTH_TYPE的KV
        List<KeyValue> authTypeKvList = dictionaryService.getKeyValueList(Cons.DICTTYPE.AUTH_TYPE.name());
        modelMap.put("authTypeKvList", authTypeKvList);
        return JsonResult.OK(modelMap);
    }

}