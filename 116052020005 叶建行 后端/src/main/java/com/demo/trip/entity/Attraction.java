package com.demo.trip.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @Author YIS
 * @Date 2022/11/9 0:15
 */
@Data
@Document("Attractions")
public class Attraction {
    @Id
    private String id;
    private String name;
    private String img;
    private String number;
    private String price;
    private String city;
    private String commentCount;
    @Field("id")
    private String productId;
    //
    private Integer dataNum;
}
