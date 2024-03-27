package com.bitdf.txing.oj.model.vo.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class CourseVideoPlayVO extends CourseSearchItemVO {
    List<CourseVideoVO> videoVOS;
}
