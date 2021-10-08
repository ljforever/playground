package com.example.demo.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.example.demo.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author banxian1804@qq.com
 * @date 2021/9/30 16:56
 */
@Mapper
public interface StudentMapper extends BaseCrudMapper<Student> {
}
