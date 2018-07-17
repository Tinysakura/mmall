package com.cfh.mmall.service;

import com.cfh.mmall.common.ServerResponse;
import com.cfh.mmall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

/**
 * Created by geely
 */
public interface ShippingService {

    ServerResponse<Shipping> add(Integer userId, Shipping shipping);
    ServerResponse<String> del(Integer userId,Integer shippingId);
    ServerResponse<Shipping> update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

}
