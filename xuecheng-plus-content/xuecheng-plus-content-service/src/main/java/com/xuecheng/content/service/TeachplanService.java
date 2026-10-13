package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程计划管理相关接口
 * @date 2023/2/14 12:10
 */
public interface TeachplanService {
 /**
  * 根据课程id查询课程计划
  * @param courseId 课程计划
  * @return
  */
  public List<TeachplanDto> findTeachplanTree(Long courseId);

 /**
  * 新增/修改/保存课程计划
  * @param saveTeachplanDto
  */
 public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

 /**
  * 根据id删除课程计划
  * @param teachplanId
  */
 public void deleteTeachplan(Long teachplanId);

 /**
  * 课程计划排序（上移/下移）
  * @param moveType 移动类型：moveup/movedown
  * @param teachplanId 课程计划id
  */
 public void moveTeachplan(String moveType, Long teachplanId);
}