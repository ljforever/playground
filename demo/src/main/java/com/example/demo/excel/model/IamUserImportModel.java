package com.example.demo.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.annotation.ExcelBindDict;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
* 用户导入model定义
* @author MyName
* @version 1.0
* @date 2021-09-30
 * Copyright © MyCompany
*/
@Getter @Setter
public class IamUserImportModel extends BaseExcelModel {

    @NotNull(message = "姓名不能为空")
    @ExcelProperty(value = "姓名", index = 0)
    private String realname;

    @NotNull(message = "用户编号不能为空")
    @ExcelProperty(value = "用户编号", index = 1)
    private String userNum;

    @ExcelBindDict(type = "GENDER")
    @NotNull(message = "性别不能为空")
    @ExcelProperty(value = "性别", index = 2)
    private String gender;

    @ExcelProperty(value = "电话", index = 3)
    private String mobilePhone;

    @ExcelProperty(value = "邮箱", index = 4)
    private String email;
}