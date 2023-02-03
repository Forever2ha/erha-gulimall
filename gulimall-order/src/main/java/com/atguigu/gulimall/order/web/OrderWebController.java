package com.atguigu.gulimall.order.web;


import com.atguigu.common.vo.order.OrderConfirmVO;
import com.atguigu.common.vo.order.OrderSubmitVO;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVO;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("toTrade")
    public String toTrade(Model model){
        OrderConfirmVO orderConfirmVO = orderService.confirmOrder();


        model.addAttribute("confirmOrderData",orderConfirmVO);
        return "confirm";
    }


    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVO vo,Model model){
        SubmitOrderResponseVO res = orderService.submit(vo);

        model.addAttribute("submitOrderResp",res);
        if (res.getCode() == 0){
            return "pay";
        }else {
            // 错误
            return "redirect:http://order.gulimall.com/toTrade";
        }

    }
}
