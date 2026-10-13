package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {

    public List<CourseTeacher> getCourseTeacherList(Long courseId);

    public CourseTeacher saveCourseTeacher(Long companyId, CourseTeacher courseTeacher);

    public CourseTeacher updateCourseTeacher(Long companyId, CourseTeacher courseTeacher);

    public void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId);
}