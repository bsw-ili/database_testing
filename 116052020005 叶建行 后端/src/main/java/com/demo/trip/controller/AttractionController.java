package com.demo.trip.controller;

import com.alibaba.fastjson.JSON;
import com.demo.trip.entity.Province;
import com.demo.trip.response.Result;
import com.demo.trip.service.TripService;
import com.demo.trip.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author YIS
 * @Date 2022/11/9 12:17
 */
@RestController
@RequestMapping("/attraction")
public class AttractionController {
    @Autowired
    private TripService tripService;

    @GetMapping("/query")
    public String query(String nameValue, String cityValue, String priceValue, String pageNum, String pageSize,
                        String sort, String sortBy) {
        Integer low = null, high = null;
        if (!Objects.equals(priceValue, "")) {
            String[] strs = priceValue.split("-");
            low = Integer.valueOf(strs[0]);
            high = Integer.valueOf(strs[1]);
        }
        Integer first = StringUtil.changeString(pageNum);
        Integer second = StringUtil.changeString(pageSize);
        Map<String, Object> data = tripService.query(nameValue, cityValue, low, high, first, second, sort, sortBy);
        return JSON.toJSONString(new Result().setCode(200).setData(data).setMessage("查询成功"));
    }

    @GetMapping("/analyse")
    public String analyse() {
        Map<String, Object> map = tripService.getAttractionData();
        return JSON.toJSONString(new Result().setCode(200).setData(map).setMessage("统计成功"));
    }


}
