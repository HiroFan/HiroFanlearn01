package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程教师管理接口", tags = "课程教师管理接口")
@RestController
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("查询课程教师列表")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getList(@PathVariable Long courseId){
        return courseTeacherService.getCourseTeacherList(courseId);
    }

    @ApiOperation("添加教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher save(@RequestBody CourseTeacher courseTeacher){
        Long companyId = 1232141425L;
        return courseTeacherService.saveCourseTeacher(companyId, courseTeacher);
    }

    @ApiOperation("修改教师")
    @PutMapping("/courseTeacher")
    public CourseTeacher update(@RequestBody CourseTeacher courseTeacher){
        Long companyId = 1232141425L;
        return courseTeacherService.updateCourseTeacher(companyId, courseTeacher);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void delete(@PathVariable Long courseId, @PathVariable Long teacherId){
        Long companyId = 1232141425L;
        courseTeacherService.deleteCourseTeacher(companyId, courseId, teacherId);
    }
}