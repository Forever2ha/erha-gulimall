package com.atguigu.gulimall.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.elasticsearch.config.ElasticConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;


@SpringBootTest
public class GulimallElasticsearchApplicationTests {

    @Autowired
    RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    @Test
    void testInsert() throws IOException {
        IndexRequest indexRequest = new IndexRequest("user");
        User user = new User();
        user.setAge(13);
        user.setGender("男");
        user.setName("张三");
        indexRequest.id("1")
                .source(JSON.toJSONString(user),XContentType.JSON);
        IndexResponse response = client.index(indexRequest, ElasticConfig.COMMON_OPTIONS);
        System.out.println(response);

    }

    @Test
    void testSearch() throws IOException {
       /*
       * # 搜索address包含[mill]的所有人的[年龄分布]和[平均年龄]
GET bank/_search
{
 "query": {
   "match": {
     "address": "mill"
   }
 },
 "aggs": {
   "ageAgg": {
     "terms": {
       "field": "age",
       "size": 10
     }
   },
   "avgAgg":{
     "avg": {
       "field": "age"
     }
   }

 }
}
       * */
        SearchRequest request = new SearchRequest("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 1000条记录
        searchSourceBuilder.size(1000);
        // 查询条件
        searchSourceBuilder.query(
                QueryBuilders.matchQuery("address","mill")
        );
        // 聚合条件
        searchSourceBuilder.aggregation(
                AggregationBuilders
                        .terms("ageGroup").field("age")
        );
        searchSourceBuilder.aggregation(
                AggregationBuilders.avg("ageAvg")
                        .field("age")
        );

        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request, ElasticConfig.COMMON_OPTIONS);
        System.out.println(response);
        Aggregations aggregations = response.getAggregations();
        for (Aggregation aggregation : aggregations) {
            System.out.println("aggregation.getName() = " + aggregation.getName());
        }
        Terms ageGroup = aggregations.get("ageGroup");
        for (Terms.Bucket bucket : ageGroup.getBuckets()) {
            System.out.println("bucket.getKeyAsString() = " + bucket.getKeyAsString());
            System.out.println("bucket.getDocCount() = " + bucket.getDocCount());
        }
        Avg ageAvg = aggregations.get("ageAvg");
        System.out.println("ageAvg.getValue() = " + ageAvg.getValue());
    }


    @ToString
    @Data
    static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;


    }


    @Test
    void search() throws IOException {
        // 1.create the SearchRequest.(without arguments this run against all indices)
        SearchRequest searchRequest = new SearchRequest("bank");
        // 2.Most search parameters are added to the SearchSourceBuilder. It offers setters for everything that goes into the search request body
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            //2.1 build queries
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            //2.2 set size and from
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
            //2.3 add aggregations
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("ageAgg").field("age");
        AvgAggregationBuilder avgAggregation = AggregationBuilders.avg("avgAgg").field("balance");
        termsAggregation.subAggregation(avgAggregation);
        searchSourceBuilder.aggregation(termsAggregation);


        // 3.Add the SearchSourceBuilder to the SearchRequest.
        searchRequest.source(searchSourceBuilder);
        // 4. Synchronous execution
        SearchResponse searchResponse = client.search(searchRequest, ElasticConfig.COMMON_OPTIONS);
        // 5. Analyse data
        System.out.println("GET BODY:   "+searchRequest.toString());
        System.out.println("Response:   "+searchResponse.toString());

        // _source
        ArrayList<Account> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            list.add(JSON.parseObject(hit.getSourceAsString(),Account.class));
        }
        list.forEach(System.out::println);

        // aggregations
        Terms ageAgg = searchResponse.getAggregations().get("ageAgg");
        System.out.println("bucket:");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {

        }
    }

    @Test
    void index() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");

        User user = new User();
        user.setAge(18);
        user.setGender("男");
        user.setName("喵喵喵");

        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, ElasticConfig.COMMON_OPTIONS);
        System.out.println(indexResponse);

    }


}
@Data
class User{
    private String name;
    private String gender;
    private Integer age;
}
