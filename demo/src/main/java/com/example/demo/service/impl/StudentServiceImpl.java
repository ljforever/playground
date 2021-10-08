package com.example.demo.service.impl;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.example.demo.entity.Student;
import com.example.demo.service.BaseCustomService;
import com.example.demo.service.StudentService;

/**
 * @author banxian1804@qq.com
 * @date 2021/9/30 16:58
 */
public class StudentServiceImpl<M extends BaseCrudMapper<T>, T> extends BaseServiceImpl<M, T> implements BaseCustomService<T> {
}
