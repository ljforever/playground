package com.example.demo.entity;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.entity.BaseEntity;

import java.util.List;

/**
 * @author banxian1804@qq.com
 * @date 2021/9/30 11:08
 */
public class Student extends BaseEntity {
    private int id;
    private String name;
    private int age;

    @BindEntityList(entity = Teacher.class, condition="this.t_id=id")
    private List<Teacher> teacherList;

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }
}
