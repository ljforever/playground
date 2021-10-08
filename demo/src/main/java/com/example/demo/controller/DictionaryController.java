package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.cache.BindingCacheManager;
import org.springframework.web.bind.annotation.*;

import com.diboot.core.dto.AttachMoreDTO;
import com.diboot.core.util.V;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.entity.ValidList;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.S;
import com.diboot.core.vo.*;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.controller.BaseCrudRestController;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.iam.annotation.Operation;
import com.diboot.iam.annotation.BindPermission;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 数据字典相关Controller
* @author MyName
* @version 1.0
* @date 2021-09-30
* Copyright © MyCompany
*/
@RestController
@BindPermission(name = "数据字典")
@Slf4j
public class DictionaryController extends BaseCrudRestController<Dictionary> {

    /***
    * 查询ViewObject的分页数据
    * <p>
    * url请求参数示例: /list?name=abc&pageSize=20&pageIndex=1&orderBy=name
    * </p>
    * @return
    * @throws Exception
    */
    @BindPermission(name = Operation.LABEL_LIST, code = Operation.CODE_LIST)
    @GetMapping("/dictionary/list")
    public JsonResult getViewObjectListMapping(Dictionary entity, Pagination pagination) throws Exception{
        QueryWrapper<Dictionary> queryWrapper = super.buildQueryWrapperByQueryParams(entity);
        queryWrapper.lambda().orderByAsc(Dictionary::getSortId, Dictionary::getId);
        List<DictionaryVO> voList = dictionaryService.getViewObjectList(queryWrapper, pagination, DictionaryVO.class);
        return JsonResult.OK(voList).bindPagination(pagination);
    }

    /***
    * 根据资源id查询ViewObject
    * @param id ID
    * @return
    * @throws Exception
    */
    @BindPermission(name = Operation.LABEL_DETAIL, code = Operation.CODE_DETAIL)
    @GetMapping("/dictionary/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") Long id) throws Exception{
        return super.getViewObject(id, DictionaryVO.class);
    }

    /**
    * 创建资源对象
    * @param entityVO
    * @return JsonResult
    * @throws Exception
    */
    @BindPermission(name = Operation.LABEL_CREATE, code = Operation.CODE_CREATE)
    @PostMapping("/dictionary/")
    public JsonResult createEntityMapping(@RequestBody @Valid DictionaryVO entityVO) throws Exception {
        boolean success = dictionaryService.createDictAndChildren(entityVO);
        if(!success){
            return JsonResult.FAIL_OPERATION("保存数据字典失败！");
        }
        return JsonResult.OK();
    }

    /***
    * 根据ID更新资源对象
    * @param entityVO
    * @return JsonResult
    * @throws Exception
    */
    @BindPermission(name = Operation.LABEL_UPDATE, code = Operation.CODE_UPDATE)
    @PutMapping("/dictionary/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id")Long id, @Valid @RequestBody DictionaryVO entityVO) throws Exception {
        entityVO.setId(id);
        boolean success = dictionaryService.updateDictAndChildren(entityVO);
        if(!success){
            return JsonResult.FAIL_OPERATION("更新数据字典失败！");
        }
        return JsonResult.OK();
    }

    /***
    * 根据id删除资源对象
    * @param id
    * @return
    * @throws Exception
    */
    @BindPermission(name = Operation.LABEL_DELETE, code = Operation.CODE_DELETE)
    @DeleteMapping("/dictionary/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id")Long id) throws Exception {
        boolean success = dictionaryService.deleteDictAndChildren(id);
        if(!success){
            return JsonResult.FAIL_OPERATION("删除数据字典失败！");
        }
        return JsonResult.OK();
    }

    /***
    * 获取数据字典数据列表
    * @param type
    * @return
    * @throws Exception
    */
    @GetMapping("/dictionary/items/{type}")
    public JsonResult getItems(@PathVariable("type")String type) throws Exception{
        if (V.isEmpty(type)){
            return JsonResult.FAIL_INVALID_PARAM("type参数未指定");
        }
        List<KeyValue> itemsList = dictionaryService.getKeyValueList(type);
        return JsonResult.OK(itemsList);
    }

    /**
    * 校验类型编码是否重复
    * @param id
    * @param type
    * @return
    */
    @GetMapping("/dictionary/checkTypeDuplicate")
    public JsonResult checkTypeDuplicate(@RequestParam(required = false) Long id, @RequestParam String type) {
        if (V.notEmpty(type)) {
            LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper();
            wrapper.select(Dictionary::getId).eq(Dictionary::getType, type).eq(Dictionary::getParentId, 0);
            if (V.notEmpty(id)) {
                wrapper.ne(Dictionary::getId, id);
            }
            boolean alreadyExists = dictionaryService.exists(wrapper);
            if (alreadyExists) {
                return new JsonResult(Status.FAIL_OPERATION, "类型编码已存在");
            }
        }
        return JsonResult.OK();
    }
		
    /**
    * 获取附加属性的通用kvList接口，用于初始化前端下拉框选项。
    * 如数据量过大，请勿调用此通用接口
    * @param attachMoreDTOList
    * @return
    */
    @PostMapping("/common/attachMore")
    public JsonResult attachMore(@Valid @RequestBody ValidList<AttachMoreDTO> attachMoreDTOList) {
        if(V.isEmpty(attachMoreDTOList)){
            return JsonResult.OK(Collections.emptyMap());
        }
        Map<String, Object> result = new HashMap<>(attachMoreDTOList.size());
        for (AttachMoreDTO attachMoreDTO : attachMoreDTOList) {
            AttachMoreDTO.REF_TYPE type = attachMoreDTO.getType();
            String targetKeyPrefix = S.toLowerCaseCamel(attachMoreDTO.getTarget());
            if (type.equals(AttachMoreDTO.REF_TYPE.D)) {
                List<KeyValue> keyValueList = dictionaryService.getKeyValueList(attachMoreDTO.getTarget());
                result.put(targetKeyPrefix + "KvList", keyValueList);
            }
            else if (type.equals(AttachMoreDTO.REF_TYPE.T)) {
                String entityClassName = S.capFirst(targetKeyPrefix);
                Class<?> entityClass = BindingCacheManager.getEntityClassBySimpleName(entityClassName);
                if (V.isEmpty(entityClass)) {
                    log.warn("传递错误的实体类型：{}", attachMoreDTO.getTarget());
                    continue;
                }
                BaseService baseService = ContextHelper.getBaseServiceByEntity(entityClass);
                if(baseService == null){
                    log.warn("未找到实体类型{} 对应的Service定义", attachMoreDTO.getTarget());
                    continue;
                }
                String value = V.isEmpty(attachMoreDTO.getValue()) ? ContextHelper.getIdFieldName(entityClass) : attachMoreDTO.getValue();
                String key = attachMoreDTO.getKey();
                if (V.isEmpty(key)) {
                    for (Field field : entityClass.getDeclaredFields()) {
                        if (V.equals(field.getType().getName(), String.class.getName())) {
                            key = field.getName();
                            break;
                        }
                    }
                }
                // 构建前端下拉框的初始化数据
                List<KeyValue> keyValueList = baseService.getKeyValueList(Wrappers.query().select(key, value));
                result.put(targetKeyPrefix + "KvList", keyValueList);
            }
            else {
                log.error("错误的加载绑定类型：{}", attachMoreDTO.getType());
            }
        }
        return JsonResult.OK(result);
    }
}