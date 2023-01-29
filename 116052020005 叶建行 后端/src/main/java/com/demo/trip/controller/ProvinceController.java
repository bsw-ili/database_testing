package com.demo.trip.controller;

import com.alibaba.fastjson.JSON;
import com.demo.trip.entity.Province;
import com.demo.trip.response.Result;
import com.demo.trip.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author YIS
 * @Date 2022/11/8 19:03
 */
@RestController
@RequestMapping("/province")
public class ProvinceController {
    @Autowired
    private TripService tripService;

    @GetMapping("/getAll")
    public String getAll() {
        List<Province> list = tripService.getAllProvince();
        return JSON.toJSONString(new Result().setCode(200).setData(list).setMessage("获取成功"));
    }

    @GetMapping("/getCities")
    public String getCitiesByName(String name) {
        List<String> list = tripService.getCitiesByProvince(name);
        return JSON.toJSONString(new Result().setCode(200).setData(list).setMessage("获取成功"));
    }
}
