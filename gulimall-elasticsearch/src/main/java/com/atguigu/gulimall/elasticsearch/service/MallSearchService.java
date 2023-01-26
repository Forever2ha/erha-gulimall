package com.atguigu.gulimall.elasticsearch.service;

import com.atguigu.gulimall.elasticsearch.vo.SearchParam;
import com.atguigu.gulimall.elasticsearch.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam param);
}
