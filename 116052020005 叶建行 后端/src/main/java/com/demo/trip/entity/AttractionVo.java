package com.demo.trip.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 景点统计结果类
 * @Author YIS
 * @Date 2022/12/4 13:55
 */
@Data
public class AttractionVo implements Serializable {
    /*城市*/
    private String name;
    /*景点总数*/
    private Integer num;
    /*热度总值*/
    private Integer heat;
    /*最大值*/
    private Integer max;
}
