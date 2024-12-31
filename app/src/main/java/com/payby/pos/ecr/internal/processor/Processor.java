package com.payby.pos.ecr.internal.processor;

public class Processor {
    public static final String ACQUIRE_PLACE_ORDER = "/acquire/place";
    public static final String ACQUIRE_GET_ORDER = "/acquire/get";                          // 查询收款订单
    public static final String ACQUIRE_GET_ORDER_LIST = "/acquire/queryPage";
    public static final String ACQUIRE_GET_ORDER_RECEIPT = "/acquire/receipt/get";        // 查询收单订单的票据
    public static final String ACQUIRE_PRINT_RECEIPTS = "/acquire/receipts/print";         // 打印收单订单的票据
    public static final String ACQUIRE_NOTIFICATION = "/acquire/notification";

    public static final String VOID_PLACE_ORDER = "/acquire/void";                        // 撤销

    public static final String REFUND_PLACE_ORDER = "/acquire/refund/place";              // 退款
    public static final String REFUND_GET_ORDER = "/acquire/refund/get";                    // 查询单笔退款
    public static final String REFUND_PRINT_RECEIPTS = "/acquire/refund/receipts/print";    // 打印退款订单的票据

    public static final String SETTLEMENT_CLOSE = "/settlement/closeBatch";
    public static final String DEVICE_GET_THIS = "/device/getThis";

    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SUCCESS = "SUCCESS";
}
