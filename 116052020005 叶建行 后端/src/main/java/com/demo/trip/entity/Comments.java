package com.demo.trip.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @Author YIS
 * @Date 2022/11/29 21:04
 */
@Data
public class Comments {
    private String commentId;
    private JSONObject userInfo;
    private String content;
    private String commentTime;
    private String score;
    private JSONObject tourTypeInfo;
    private JSONArray attachments;
}
