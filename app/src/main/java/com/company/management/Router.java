package com.company.management;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public final class Router {
    /**
     * 列表相关路由
     */
    public static final String MATERIAL_OUT_WAREHOUSE_LIST = "/warehouse/out"; // 获取出库单的列表路由
    public static final String MATERIAL_PURCHASE_APPLY_LIST = "/material/apply"; // 获取采购单的列表路由
    public static final String MATERIAL_IN_WAREHOUSE_LIST = "/warehouse/in"; // 获取入库单的列表路由
    public static final String MATERIAL_PICKING_LIST = "/material/receive/"; // 获取领料单的列表路由
    public static final String MATERIAL_RETURN_LIST = MATERIAL_PICKING_LIST; // 获取待还料单的列表路由
    /**
     * 与列表状态更改相关路由
     */
    public static final String MATERIAL_PURCHASE_CHECK = "/material/apply/verify"; // 物料采购单审核
    public static final String MATERIAL_PICKING_CHECK = "/material/receive/verify"; //  领料单审核
    /**
     * 与列表创建相关路由
     */
    public static final String MATERIAL_OUT_WAREHOUSE_CREATE = "/warehouse/out"; // 创建出库单的路由
    public static final String MATERIAL_PURCHASE_APPLY_CREATE = "/material/apply/write_table"; // 创建采购单的相关路由
    public static final String MATERIAL_IN_WAREHOUSE_CREATE = "/warehouse/in"; // 创建入库单的路由
    public static final String MATERIAL_PICKING_CREATE = "/material/receive/write_table"; // 创建领料单的路由
    public static final String MATERIAL_RETURN_CREATE = "/material/back/write_table"; // 创建还料单的路由
    /**
     *
     */
    public static final String MATERIAL_INFORMATION_LIST = "/"; // 获取材料总况的路由

    /**
     * 用户相关的路由
     */
    public static final String LOGIN= "/app/login"; // 登录状态检测的路由
    public static final String REGESTER= "/app/register"; // 注册的路由
    public static final String USER_INFO = "/user/info"; // 获取用户信息的路由
    public static final String UPDATE_USER_PASSWORD = "/user/modify/password"; // 修改用户密码的路由

    /**
     * 获取信息的路由
     */
    public static final String GET_MESSAGE = "/message/"; // 获取信息的路由

    /**
     * 获取表单详细内容的路由
     */
    public static final String MATERIAL_PURCHASE_APPLY_DETAIL = "/material/apply/detail"; // 查看采购申请表的路由
    public static final String MATERIAL_OUT_WAREHOUSE_DETAIL = "/warehouse/out/detail"; // 查看出库单详情的路由
    public static final String MATERIAL_IN_WAREHOUSE_DETAIL = "/warehouse/in/detail"; // 查看入库单详情的路由
    public static final String MATERIAL_RETURN_DETAIL = "/"; // 查看还料单详情的路由
    public static final String MATERIAL_PICKING_DETAIL = "/material/receive/detail"; // 查看领料单详情的路由
    /**
     * TODO: 完成路由表的配置
     */
    public static final String MATERIAL_LIST = "/material/"; // 获取材料列表的路由
    /**
     * 获取权限列表的路由
     */
    public static final String ACL_LIST = "/app/permission";
}
