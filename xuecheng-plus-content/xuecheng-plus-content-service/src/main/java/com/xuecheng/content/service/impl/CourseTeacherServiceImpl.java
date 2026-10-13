package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    private void checkCompany(Long companyId, Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能操作本机构的课程");
        }
    }

    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher saveCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        checkCompany(companyId, courseTeacher.getCourseId());
        courseTeacherMapper.insert(courseTeacher);
        return courseTeacher;
    }

    @Override
    public CourseTeacher updateCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        checkCompany(companyId, courseTeacher.getCourseId());
        courseTeacherMapper.updateById(courseTeacher);
        return courseTeacher;
    }

    @Override
    public void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId) {
        checkCompany(companyId, courseId);
        courseTeacherMapper.deleteById(teacherId);
    }
}