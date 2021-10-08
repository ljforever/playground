package com.example.demo.entity;

import com.diboot.core.binding.Binder;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.entity.BaseEntity;

import java.util.List;

/**
 * @author banxian1804@qq.com
 * @date 2021/9/30 11:09
 */
public class Teacher extends BaseEntity {

    private int id;
    private String name;
    private int age;

    @BindEntityList(entity = Student.class, condition="this.stu_id=id")
    private List<Student> studentList;

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

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }
}
