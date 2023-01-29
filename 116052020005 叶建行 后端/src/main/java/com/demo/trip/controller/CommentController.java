package com.demo.trip.controller;

import com.alibaba.fastjson.JSON;
import com.demo.trip.entity.Comment;
import com.demo.trip.entity.Province;
import com.demo.trip.response.Result;
import com.demo.trip.service.TripService;
import com.demo.trip.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author YIS
 * @Date 2022/11/22 23:11
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private TripService tripService;

    @GetMapping("/getComments")
    public String getComments(String productId, String type,String pageNum, String pageSize) {
        Integer first = StringUtil.changeString(pageNum);
        Integer second = StringUtil.changeString(pageSize);
        List<Comment> comment = tripService.getComments(productId, type,first,second);
        return JSON.toJSONString(new Result().setCode(200).setData(comment).setMessage("获取评论成功"));
    }
}
