package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 自定义BaseEntity，对diboot-core的BaseEntity做差异化定义
* @author MyName
* @version 1.0
* @date 2021-09-30
* Copyright © MyCompany
*/
@Getter @Setter @Accessors(chain = true)
public abstract class BaseCustomEntity extends BaseEntity {
    private static final long serialVersionUID = 8532761134410344844L;


}
