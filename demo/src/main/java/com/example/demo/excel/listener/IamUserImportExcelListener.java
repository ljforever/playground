package com.example.demo.excel.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.example.demo.excel.model.IamUserImportModel;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamUserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户导入Excel listener
 */
public class IamUserImportExcelListener extends FixedHeadExcelListener<IamUserImportModel> {

    /**
     * 附加校验
     * @param dataList
     * @param requestParams
     */
    @Override
    protected void additionalValidate(List<IamUserImportModel> dataList, Map<String, Object> requestParams) {
        // 对用户编号是否重复进行校验
        List<String> msgList = new ArrayList<>();
        List<String> existUserNumList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            IamUserImportModel userExcelModel = dataList.get(i);
            if (existUserNumList.contains(userExcelModel.getUserNum())) {
                msgList.add("Excel表中第" + (existUserNumList.indexOf(userExcelModel.getUserNum()) + 2) + "行与第" + (i + 2) + "行用户编号重复");
            }
            existUserNumList.add(userExcelModel.getUserNum());
        }
        if (V.notEmpty(msgList)) {
            throw new BusinessException(Status.FAIL_OPERATION, S.join(msgList, "; "));
        }
        // 对用户编号在系统中是否重复进行校验
        List<String> allUserNumList = dataList.stream().map(IamUserImportModel::getUserNum).collect(Collectors.toList());
        List<String> duplicateUserNumList = ContextHelper.getBean(IamUserService.class).filterDuplicateUserNums(allUserNumList);
        if (V.notEmpty(duplicateUserNumList)) {
            throw new BusinessException(Status.FAIL_OPERATION, "用户编号 " + S.join(duplicateUserNumList, "、") + " 在系统中已存在");
        }
    }

    /**
     * 保存数据
     * @param dataList
     * @param requestParams
     */
    @Override
    protected void saveData(List<IamUserImportModel> dataList, Map<String, Object> requestParams) {
        if (V.isEmpty(dataList)) {
            return;
        }
        Long orgId = 0L;
        if (V.notEmpty(requestParams)) {
            Object orgIdObj = requestParams.get(BeanUtils.convertToFieldName(IamUser::getOrgId));
            if (orgIdObj != null) {
                orgId = Long.valueOf(String.valueOf(orgIdObj));
            }
        }
        List<IamUser> iamUserList = new ArrayList<>();
        for (IamUserImportModel data : dataList) {
            IamUser iamUser = new IamUser();
            BeanUtils.copyProperties(data, iamUser);
            iamUser.setOrgId(orgId);
            iamUserList.add(iamUser);
        }
        ContextHelper.getBean(IamUserService.class).createEntities(iamUserList);
    }
}