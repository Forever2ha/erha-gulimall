package com.atguigu.gulimall.elasticsearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.atguigu.gulimall.elasticsearch.service.MallSearchService;
import com.atguigu.gulimall.elasticsearch.vo.SearchParam;
import com.atguigu.gulimall.elasticsearch.vo.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {

        param.set_queryString(request.getQueryString());
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        System.out.println("1111111111111");
        return "list";
    }

    @GetMapping("/test")
    @ResponseBody
    public String a(){
        return "666";
    }

}
