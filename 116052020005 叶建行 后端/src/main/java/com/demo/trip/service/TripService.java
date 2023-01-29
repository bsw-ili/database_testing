package com.demo.trip.service;

import com.demo.trip.entity.Attraction;
import com.demo.trip.entity.AttractionVo;
import com.demo.trip.entity.Comment;
import com.demo.trip.entity.Province;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author YIS
 * @Date 2022/11/8 19:06
 */
@Service
public class TripService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Province> getAllProvince() {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("heat")));
        return mongoTemplate.find(query, Province.class);
    }

    public List<String> getCitiesByProvince(String provinceName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("province").regex(provinceName));
        Province province = mongoTemplate.findOne(query, Province.class);
        return province.getCities();
    }

    @SuppressWarnings("deprecation")
    public List<Comment> getComments(String productId, String type, Integer pageNum, Integer pageSize) {
        Criteria criteria;
//        Map<String, Object> map = new HashMap<>();
        if (type != "")
            criteria = Criteria.where("productId").is(productId).and("comments.tourTypeInfo.tourTypeName").is(type);
        else
            criteria = Criteria.where("productId").is(productId);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("comments"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.ASC,"_id"),
                Aggregation.skip(pageNum>1?(pageNum-1)*pageSize:0),
                Aggregation.limit(pageSize),
                Aggregation.group("_id")
                        .push("comments").as("comments")
                        .first("commentAggregation").as("commentAggregation")
                        .first("productId").as("productId")
                        .first("totalCount").as("totalCount")
        );
        // 设置起始页和每页查询条数

        AggregationResults<Comment> aggregationResults = mongoTemplate.aggregate(aggregation, "comment", Comment.class);
//        System.out.println(aggregationResults.getMappedResults());
        return aggregationResults.getMappedResults();
    }

    public Map<String, Object> getAttractionData() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("city")
                        .first("city").as("name")
                        .sum("number").as("heat")
                        .count().as("num"),
                Aggregation.sort(Sort.Direction.DESC, "heat"),
                Aggregation.limit(6)
        );
        AggregationResults<AttractionVo> aggregationResults = mongoTemplate.aggregate(aggregation, "Attractions", AttractionVo.class);
        List<AttractionVo> list = aggregationResults.getMappedResults();

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> heatMap = new HashMap<>();
        List<Integer> heatData = new ArrayList<>();

        for (AttractionVo attractionVo : list) {
            if (attractionVo.getHeat() > 300000)
                attractionVo.setMax(((attractionVo.getHeat() / 100000) + 1) * 100000);
            else
                attractionVo.setMax(300000);
            heatData.add(attractionVo.getHeat());
        }

        heatMap.put("indicator", list);
        heatMap.put("data", heatData);
        map.put("heat", heatMap);

        List<AttractionVo> list1 = new ArrayList<>();

        Map<String, Object> numMap = new HashMap<>();
        List<Integer> numData = new ArrayList<>();
        for (AttractionVo attractionVo : list) {
            AttractionVo attractionVo1 = new AttractionVo();
            //指向的是同一对象，故需要复制一个相同的bean
            BeanUtils.copyProperties(attractionVo, attractionVo1);
            attractionVo1.setMax(700);
            list1.add(attractionVo1);
            numData.add(attractionVo1.getNum());
        }
        numMap.put("indicator", list1);
        numMap.put("data", numData);

        map.put("num", numMap);

        return map;
    }

    public Map<String, Object> query(String nameValue, String cityValue, Integer low, Integer high, Integer pageNum, Integer pageSize, String sort, String sortBy) {
        Map<String, Object> map = new HashMap<>();
        Query query = new Query();

        Pattern pattern_name = Pattern.compile("^.*" + nameValue + ".*$");
        query.addCriteria(Criteria.where("name").regex(pattern_name));
        //是否为省级
        if (cityValue.contains("省") || cityValue.contains("自治区")) {
            if (cityValue.equals("内蒙古自治区")) {
                cityValue = cityValue.substring(0, 3);
            } else {
                cityValue = cityValue.substring(0, 2);
            }
            List<String> cities = getCitiesByProvince(cityValue);
            for (int i = 0; i < cities.size(); i++) {
                cities.set(i, cities.get(i).replace("市", ""));
            }
//            System.out.println(cities);
            query.addCriteria(Criteria.where("city").in(cities));
        } else {
            cityValue = cityValue.replace("市", "");
            cityValue = cityValue.replace("州", "");
            Pattern pattern_city = Pattern.compile("^.*" + cityValue + ".*$");
            query.addCriteria(Criteria.where("city").regex(pattern_city));
        }

        if (low != null && high != null)
            query.addCriteria(Criteria.where("price").gte(low).lte(high));

        if (!sort.equals("null")) {
            query.with(Sort.by(sortBy.equals("0") ? Sort.Order.desc(sort) : Sort.Order.asc(sort)));
        } else
            query.with(Sort.by(Sort.Order.desc("number")));

        Long count = mongoTemplate.count(query, Attraction.class);
        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        List<Attraction> list = mongoTemplate.find(query.with(pageable), Attraction.class);
        map.put("list", list);
        // 计算总页数
        Long total = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
//        System.out.println(total);

        map.put("total", total);
        return map;
    }

}
