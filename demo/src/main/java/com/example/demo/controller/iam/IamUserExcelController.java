package com.example.demo.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.D;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.example.demo.excel.listener.IamUserImportExcelListener;
import com.example.demo.excel.model.IamUserExportModel;
import com.example.demo.excel.model.IamUserImportModel;
import com.diboot.file.controller.BaseExcelFileController;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.file.util.ExcelHelper;
import com.diboot.iam.annotation.Operation;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.vo.IamUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* 用户Excel上传下载Controller
* @author MyName
* @version 1.0
* @date 2021-09-30
* Copyright © MyCompany
*/
@RestController
@RequestMapping("/iam/user/excel")
@Slf4j
@BindPermission(name = "用户Excel上传下载")
public class IamUserExcelController extends BaseExcelFileController {

    @Autowired
    private IamUserService iamUserService;

    /**
     * 下载示例文件
     * @throws Exception
     */
    @BindPermission(name = "下载示例文件")
    @GetMapping("/downloadExample")
    public void downloadExample() throws Exception{
        String fileName = "用户导入示例.xlsx";
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getResponse();
        ExcelHelper.exportExcel(response, fileName, IamUserImportModel.class, null);
    }

    /***
     * 预览数据
     * @throws Exception
     */
    @Log(operation = "Excel预览", businessObj = "IamUser")
    @BindPermission(name = "excel预览")
    @PostMapping("/preview")
    public JsonResult preview(@RequestParam("file") MultipartFile file) throws Exception {
        return super.excelPreview(file);
    }
    
		/***
     * 预览保存
     * @throws Exception
     */
		@Log(operation = "Excel预览导入用户", businessObj = "IamUser")
    @BindPermission(name = Operation.LABEL_IMPORT, code = Operation.CODE_IMPORT)
    @PostMapping("/previewSave")
    public JsonResult previewSave(@RequestParam(BaseExcelFileController.PREVIEW_FILE_NAME) String previewFileName,
                                  @RequestParam(BaseExcelFileController.ORIGIN_FILE_NAME) String originFileName,
                                  HttpServletRequest request) throws Exception {
        return super.excelPreviewSave(IamUser.class, previewFileName, originFileName);
    }

    /***
     * excel导入用户
     * @throws Exception
     */
		@Log(operation = "Excel导入用户", businessObj = "IamUser")
    @BindPermission(name = Operation.LABEL_IMPORT, code = Operation.CODE_IMPORT)
    @PostMapping("/upload")
    public JsonResult upload(@RequestParam("file") MultipartFile file) throws Exception {
        return super.uploadExcelFile(file, IamUser.class);
    }

    /***
     * 人员列表导出
     * @param iamUser
     * @return
     * @throws Exception
     */
		@Log(operation = "导出用户列表", businessObj = "IamUser")
    @BindPermission(name = Operation.LABEL_EXPORT, code = Operation.CODE_EXPORT)
    @GetMapping("/export")
    public JsonResult export(IamUser iamUser) throws Exception {
        QueryWrapper<IamUser> queryWrapper = super.buildQueryWrapper(iamUser);
        List<IamUserVO> iamUserList = iamUserService.getViewObjectList(queryWrapper, null, IamUserVO.class);
        if (V.isEmpty(iamUserList)) {
            return new JsonResult(Status.FAIL_OPERATION, "用户列表为空，导出失败");
        }
        String fileName = "用户列表导出_" + D.today() + ".xlsx";
        List<IamUserExportModel> dataList = this.entityList2ExcelList(iamUserList);
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getResponse();
        ExcelHelper.exportExcel(response, fileName, IamUserExportModel.class, dataList);
        return null;
    }

    /***
     * 实体列表转excel列表
     * @param userVoList
     * @return
     */
    private List<IamUserExportModel> entityList2ExcelList(List<IamUserVO> userVoList) {
        if (V.isEmpty(userVoList)) {
            return Collections.emptyList();
        }
        List<IamUserExportModel> excelModelList = new ArrayList<>();
        for (IamUserVO vo : userVoList) {
            IamUserExportModel excelModel = new IamUserExportModel();
            BeanUtils.copyProperties(vo, excelModel);
            excelModel.setGender(vo.getGenderLabel());
            excelModelList.add(excelModel);
        }
        return excelModelList;
    }

    @Override
    protected FixedHeadExcelListener getExcelDataListener() {
        return new IamUserImportExcelListener();
    }

}