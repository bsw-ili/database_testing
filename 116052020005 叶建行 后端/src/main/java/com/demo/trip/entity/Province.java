package com.demo.trip.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @Author YIS
 * @Date 2022/11/8 19:09
 */
@Data
@Document("provinces")
public class Province {
    @Id
    private ObjectId id;
    @Field("heat")
    private String value;
    @Field("province")
    private String name;
    private List<String> cities;

}
