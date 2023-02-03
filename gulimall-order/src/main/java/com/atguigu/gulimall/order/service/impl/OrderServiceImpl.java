package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.order.OrderConstant;
import com.atguigu.common.to.MemberEntityVo;
import com.atguigu.common.to.SkuInfoEntityTo;
import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.feign.ProductFeignService;
import com.atguigu.gulimall.order.feign.WareFeignService;
import com.atguigu.gulimall.order.order.OrderCreateTO;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.order.MemberAddressVO;
import com.atguigu.common.vo.order.OrderConfirmVO;
import com.atguigu.common.vo.order.OrderItemVO;
import com.atguigu.common.vo.order.OrderSubmitVO;
import com.atguigu.gulimall.order.config.WebMvcConfig;
import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    WareFeignService wareFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVO confirmOrder() {
        Object o = AopContext.currentProxy();
        System.out.println(o);

        OrderConfirmVO res = new OrderConfirmVO();
        MemberEntityVo currentUser = WebMvcConfig.getCurrentUser();
        // 查用户地址
        res.setMemberAddressVos(new ArrayList<MemberAddressVO>(){{
            add(
                    new MemberAddressVO()
                            .setId(0L)
                            .setMemberId(currentUser.getId())
                            .setName(currentUser.getNickname())
                            .setPhone(currentUser.getMobile())
                            .setPostCode("710021")
                            .setProvince("陕西省")
                            .setRegion("西安市")
                            .setDetailAddress("未央区徐家湾街道XXX")
                            .setDefaultStatus(1)
            );
            add(
                    new MemberAddressVO()
                            .setId(1L)
                            .setMemberId(currentUser.getId())
                            .setName(currentUser.getNickname())
                            .setPhone(currentUser.getMobile())
                            .setPostCode("710021")
                            .setProvince("asada")
                            .setRegion("dsadasdsa")
                            .setDetailAddress("未央区徐dasdadas家湾街道XXX")
                            .setDefaultStatus(0)
            );
        }});
        // 查价格
        R r = cartFeignService.getOrderItem(currentUser.getId());
        if (r.getCode() == 0){
            res.setItems(r.getData(new TypeReference<List<OrderItemVO>>(){}));
        }

        // 生成幂等token
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+currentUser.getId(),token,30, TimeUnit.MINUTES);
        res.setUniqueToken(token);

        return res;
    }

    @Override
    @Transactional
//    @GlobalTransactional(rollbackFor = Exception.class)
    public SubmitOrderResponseVO submit(OrderSubmitVO vo) {
        Long userId = WebMvcConfig.getCurrentUser().getId();
        SubmitOrderResponseVO responseVO = new SubmitOrderResponseVO();
        responseVO.setCode(-1);

        // 1.验证令牌
        String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + userId);
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Long res = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + WebMvcConfig.getCurrentUser().getId()),
                redisToken
        );

        if (new Long(1).equals(res)){
            // 2.令牌校验成功,创建订单
            OrderCreateTO order = createOrder(vo);
            responseVO.setOrder(order.getOrder());
            // 3.验价不做了，保存订单
            saveOrder(order);
            // 4.锁定库存
            wareFeignService.testLock();
            System.out.println("RootContext.getXID() = " + RootContext.getXID());

//            int bb = 1/0;

            responseVO.setCode(0);
        }

        return responseVO;
    }

    @Transactional
    protected void saveOrder(OrderCreateTO order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        baseMapper.insert(orderEntity);
        for (OrderItemEntity orderItem : order.getOrderItems()) {
            orderItemDao.insert(orderItem);
        }
    }

    private OrderCreateTO createOrder(OrderSubmitVO vo){
        OrderCreateTO orderCreateTO = new OrderCreateTO();

        // 1.订单基本信息
        OrderEntity orderEntity = buildOrderEntity();

        // 2.获取订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderEntity);

        // 3.计算价格相关
        computePrice(orderEntity,orderItemEntities);

        orderCreateTO.setOrderItems(orderItemEntities);
        orderCreateTO.setOrder(orderEntity);
        return orderCreateTO;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        BigDecimal total = new BigDecimal("0");
        Integer totalGift = 0,totalGrowth = 0;
        for (OrderItemEntity entity : orderItemEntities) {
            BigDecimal multiply = entity.getSkuPrice().multiply(new BigDecimal(entity.getSkuQuantity().toString()));
            // 优惠不弄了
            total = total.add(multiply);
            totalGift += entity.getGiftIntegration();
            totalGrowth += entity.getGiftGrowth();
        }
        // 商品总额
        orderEntity.setTotalAmount(total);
        // 加上运费
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        // 优惠金额
        orderEntity.setPromotionAmount(BigDecimal.ZERO);

        orderEntity.setIntegration(totalGift);
        orderEntity.setGrowth(totalGrowth);
    }

    private List<OrderItemEntity> buildOrderItems(OrderEntity orderEntity) {

        R r = cartFeignService.getOrderItem(WebMvcConfig.getCurrentUser().getId());
        if (r.getCode() == 0){
            List<OrderItemVO> orderItemVOList = r.getData(new TypeReference<List<OrderItemVO>>() {});
            if (orderItemVOList != null && !orderItemVOList.isEmpty()){
                List<OrderItemEntity> collect = orderItemVOList.stream()
                        .map(i -> buildOrderItem(i,orderEntity.getOrderSn()))
                        .collect(Collectors.toList());
                return collect;
            }
        }
        return null;
    }

    private static OrderEntity buildOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(WebMvcConfig.getCurrentUser().getId());
        // 订单号
        String orderId = IdWorker.getTimeId();
        orderEntity.setOrderSn(orderId);
        // 运费信息
        orderEntity.setFreightAmount(new BigDecimal("1.65"));
        // 收货人信息
        orderEntity.setReceiverCity("西安市")
                   .setReceiverDetailAddress("未央区徐家湾")
                   .setReceiverName("喵喵喵")
                   .setReceiverPhone("1111111111")
                   .setReceiverPostCode("712001")
                   .setReceiverProvince("陕西省")
                   .setReceiverRegion("区域一");

        // 订单状态
        orderEntity.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        return orderEntity;
    }

    private OrderItemEntity buildOrderItem(OrderItemVO item, String orderSn) {
        OrderItemEntity res = new OrderItemEntity();
        // 1.订单号
        res.setOrderSn(orderSn);
        // 2.spu
        R r = productFeignService.info(item.getSkuId());
        SkuInfoEntityTo to = r.getData("skuInfo",new TypeReference<SkuInfoEntityTo>() {
        });
        res.setSpuId(to.getSpuId());
        res.setSpuName("懒得查了");
        res.setSpuPic(item.getImage());
        res.setSpuBrand(to.getBrandId()+"：这是品牌Id");
        res.setCategoryId(to.getCatalogId());

        // 3.sku
        res.setSkuId(item.getSkuId());
        res.setSkuName(item.getTitle());
        res.setSkuPic(item.getImage());
        res.setSkuPrice(item.getPrice());
        StringBuilder sb = new StringBuilder();
        for(String s: item.getSkuAttrValues()){
            sb.append(s).append(";");
        }
        res.setSkuAttrsVals(sb.toString());
        res.setSkuQuantity(item.getCount());
        // 4.积分信息
        res.setGiftGrowth(item.getPrice().intValue() * item.getCount());
        res.setGiftIntegration(item.getPrice().intValue() * item.getCount());
        return res;
    }
}