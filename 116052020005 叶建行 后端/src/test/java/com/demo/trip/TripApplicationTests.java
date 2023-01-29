package com.demo.trip;

import com.demo.trip.entity.Attraction;
import com.demo.trip.entity.AttractionVo;
import com.demo.trip.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
class TripApplicationTests {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TripService tripService;

    @Test
    void save() {
//        System.out.println(tripService.query("故宫","北京",1000,5000,0,4));
        Map<String, Object> attractionData = tripService.getAttractionData();
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println(list.get(i));
//        }
        System.out.println(attractionData);
    }
    @Test
    void findAll(){
        Integer i = 2345;
        int j = i/10000;
        System.out.println((j+1)*10000);
    }

    @Test
    void contextLoads() {
    }

}
