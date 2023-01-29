package com.demo.trip.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


/**
 * @Author YIS
 * @Date 2022/11/22 22:51
 */
@Data
@Document("comment")
public class Comment {
    @Id
    private ObjectId Id;
    private String productId;
    private String totalCount;
    private List<Comments> comments;
    private JSONObject commentAggregation;
}
