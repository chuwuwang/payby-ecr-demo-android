# Version History

| Version        | Time        | Modification                                                 |
| -------------- | ----------- | ------------------------------------------------------------ |
| 0.9-SNAPSHOT   |             | 增加multiMedia                                               |
| 0.8.1-SNAPSHOT |             | 修改deviceInfo的内容                                         |
| 0.5-SNAPSHOT   |             | 重构文档结构，用proto文件区分章节<br />修改了下单请求和返回<br />修改了分页查询的请求 |
| 0.4-SNAPSHOT   |             | 增加了reserved字段<br />修改了Receipt 枚举值<br />增加了查询收款订单分页 |
| 0.3-SNAPSHOT   | 2024-aug-26 |                                                              |



# 业务消息格式

## common.proto

### 一般返回码

信封的Response对象的responseCode的部分码值清单。业务结果会给出除了该表格以外其他的错误码。

| **错误码** | **说明** |
| :--------- | :------- |
| SUCCESS    | 成功     |
| FAILURE    | 失败     |

### Money

| 字段名       | 类型   | 多样性 | 说明                                                         |
| ------------ | ------ | ------ | ------------------------------------------------------------ |
| currencyCode | string |        | 遵守 ISO 4217                                                |
| amount       | string |        | 由于protobuf没有提供任意精度十进制树。所以用字符串，例子"1.00" |

样例

```protobuf
currencyCode: "AED"
amount: "1.23"
```

### TimeScope

| 字段名 | 类型      | 多样性   | 说明     |
| ------ | --------- | -------- | -------- |
| from   | timestamp | optional | 起始时间 |
| to     | timestamp | optional |          |

样例

```protobuf
from {
  seconds: 1726737639777
}
to {
  seconds: 1726737639777
}
```

### PageParam

| 字段名 | 类型  | 多样性 | 说明              |
| ------ | ----- | ------ | ----------------- |
| number | int32 |        | 分页顺序，从0开始 |
| size   | int32 |        | 分页大小，最小1   |

### PaginationParam

| 字段名        | 类型      | 多样性 | 说明           |
| ------------- | --------- | ------ | -------------- |
| totalElements | int32     |        | 总的元素的数量 |
| totalPages    | int32     |        | 总的页面的数量 |
| pageParam     | PageParam |        | 页面参数       |

## multi_media.proto

![image-20241021171555080](ecr_acquire_resources/image-20241021171555080.png)

### 获得图片

serviceName: /multiMedia/getImage

request: ResourceUri

response: Image

#### ResourceUri

| 字段名 | 类型   | 多样性   | 说明 |
| ------ | ------ | -------- | ---- |
| url    | string | optional |      |
| urn    | string | optional |      |



#### Image

| 字段名 | 类型      | 多样性 | 说明 |
| ------ | --------- | ------ | ---- |
| type   | ImageType |        |      |
| data   | bytes     |        |      |




## device.proto

![image-20241021171237505](ecr_acquire_resources/image-20241021171237505.png)

### 查询自己的设备信息



serviceName: /device/getThis

request: Void

response: DeviceInfo



#### DeviceInfo

| 字段名   | 类型     | 多样性 | 说明 |
| -------- | -------- | ------ | ---- |
| deviceId | string   |        |      |
| merchant | Merchant |        |      |
| store    | Store    |        |      |

#### Merchant

| 字段名            | 类型   | 多样性   | 说明                                             |
| ----------------- | ------ | -------- | ------------------------------------------------ |
| mid               | string |          |                                                  |
| name              | string | optional |                                                  |
| mcc               | string | optional |                                                  |
| logoImageUrl      | string | optional | 可以直接下载或者使用/multiMedia/getImage获得图片 |
| printLogoImageUrl | string | optional | 可以直接下载或者使用/multiMedia/getImage获得图片 |

#### Store

| 字段名   | 类型   | 多样性   | 说明 |
| -------- | ------ | -------- | ---- |
| name     | string | optional |      |
| location | string | optional |      |





## acquire.proto

![image-20241016154557942](ecr_acquire_resources/image-20241016154557942.png)


### 收款下单

客户端发起收款请求，服务端返回收款订单。

serviceName: /acquire/place

request: PlaceOrderRequest

response: AcquireOrder

#### PlaceOrderRequest

| 字段名        | 类型                      | 多样性   | 说明                                            |
| ------------- | ------------------------- | -------- | ----------------------------------------------- |
| amount        | common.Money              |          |                                                 |
| subject       | string                    | optional | 标题                                            |
| paymentMethod | string                    | optional | 支付方式，不填写的话在pos机选择                 |
| reserved      | string                    | optional |                                                 |
| invokeType    | InvokeType                |          | 必须选择一个，没有默认值                        |
| notification  | PaymentResultNotification | optional | 不填写就是不通知                                |
| printReceipts | Receipt                   | repeated | 在结果界面打印票据，不填写就是不打印,服务端去重 |

#### InvokeParams

| 字段名       | 类型                       | 多样性   | 说明                                |
| ------------ | -------------------------- | -------- | ----------------------------------- |
| invokeType   | InvokeType                 |          |                                     |
| notification | AcquiredResultNotification | optional | do not send notification by default |

##### InvokeType 

| 枚举             | 含义                                                         |
| ---------------- | ------------------------------------------------------------ |
| SYNCHRONIZATION  | 同步调用，返回会在支付结果页面发送                           |
| ASYNCHRONIZATION | 异步调用，返回会在下单后返回，调用者需要后续使用查询接口或者接受通知来获得支付结果 |

##### SYNCHRONIZATION diagram

 同步调用方式的有点在于交互简单，不要轮询或者接收通知。缺点在于调用者需要等待非常长的时间，直到用户完成付款，或者超时。

![image-20240826171123207](ecr_acquire_resources/image-20240826171123207.png)

##### ASYNCHRONIZATION diagram

同步调用方式的有点在于请求返回快。缺点在于调用者需要轮询订单结果或者接收通知。

![image-20240826171834459](ecr_acquire_resources/image-20240826171834459.png)

##### PaymentResultNotification

不使用该字段就是不通知

| 枚举    | 含义                                         |
| ------- | -------------------------------------------- |
| REQUEST | 用请求的方式通知，pos机会等待返回，并尝试3次 |
| EVENT   | 用事件方式，pos机不等待返回，只通知1次       |


#### CashierParams

| 字段名            | 类型          | 多样性   | 说明                                  |
| ----------------- | ------------- | -------- | ------------------------------------- |
| paymentMethods    | PaymentMethod | repeated | display all methods by default        |
| displayResultPage | bool          | optional | do not display result page by default |
| printReceipts     | Receipt       | repeated | print nothing by default              |

##### Receipt

| 枚举值           |
| ---------------- |
| MERCHANT_RECEIPT |
| CUSTOMER_RECEIPT |

##### PaymentMethod

| 常量值                |
| --------------------- |
| BANKCARD              |
| CUSTOMER_PRESENT_CODE |
| POS_PRESENT_CODE      |

样例

```protobuf
amount {
  currencyCode: "AED"
  amount: "1.23"
}
subject: "aaa"
invokeParams {
  invokeType: ASYNCHRONIZATION
  notification: EVENT
}
cashierParams {
  paymentMethod: BANKCARD
  displayResultPage: false
  printReceipts: MERCHANT_RECEIPT
}
```

#### AcquireOrder

| 字段名       | 类型               | 多样性   | 说明          |
| ------------ | ------------------ | -------- | ------------- |
| orderNo      | string             |          | pos收单订单号 |
| amount       | Money              |          |               |
| status       | AcquireOrderStatus |          | 订单状态      |
| reserved     | string             | optional |               |
| createdTime  | Timestamp          |          |               |
| failCode     | string             | optional |               |
| failMessage  | string             | optional |               |
| receiptNo    | string             | optional |               |
| paymentOrder | PaymentOrder       | optional |               |

##### AcquireOrderStatus

| 枚举值     |
| ---------- |
| SUCCESSFUL |
| PROCESSING |
| FAILED     |

#### 

#### PaymentOrder

| 字段名              | 类型                | 多样性        | 说明 |
| ------------------- | ------------------- | ------------- | ---- |
| payMethod           | PayMethod           | optional      |      |
| paidTime            | Timestamp           | optional      |      |
| bankcardPaymentInfo | BankcardPaymentInfo | oneof content |      |
| appPaymentInfo      | AppPaymentInfo      | oneof content |      |

##### PayMethod

| 枚举值            |
| ----------------- |
| PHYSICAL_CARD_PAY |
| APP               |

#### BankcardPaymentInfo

| 字段名              | 类型               | 多样性   | 说明          |
| ------------------- | ------------------ | -------- | ------------- |
| cardBrand           | string             |          | pos收单订单号 |
| authCode            | Money              |          |               |
| tid                 | AcquireOrderStatus |          | 订单状态      |
| rrn                 | string             | optional |               |
| amexMID             | string             | optional |               |
| cardNoMask          | string             | optional |               |
| readType            | ReadType           | optional |               |
| passwordMode        | PasswordMode       | optional |               |
| responseCode        | String             | optional |               |
| responseDescription | String             | optional |               |
| iccData             | bytes              | optional |               |
| isSignature         | bool               | optional |               |
| emvData             | EmvData            | optional |               |



##### ReadType

| 枚举值      |
| ----------- |
| MANUAL      |
| MAGNETIC    |
| CONTACTLESS |
| CONTACT     |

##### PasswordMode

| 枚举值           |
| ---------------- |
| ONLINE_PASSWORD  |
| OFFLINE_PASSWORD |
| NO_PASSWORD      |



##### EmvData

| 字段名   | 类型   | 多样性   | 说明 |
| -------- | ------ | -------- | ---- |
| tc       | string | optional |      |
| aid      | string | optional |      |
| tvr      | string | optional | []() |
| atc      | string | optional |      |
| tsi      | string | optional |      |
| cid      | string | optional |      |
| arqc     | string | optional |      |
| appName  | string | optional |      |
| appLabel | string | optional |      |

#### 错误码

| **错误码**                       | **统一错误码** | **错误描述**                        | **备注** |
| :------------------------------- | :------------- | :---------------------------------- | :------- |
| ***REQUEST_PARAM_INCONSISTENT*** |                | Inconsistent request, field name:%s | 幂等错误 |



#### 支付结果通知

该交互是从pos机发到商户收银台，根据请求的PaymentResultNotification的定义，

1.当没有值时，不通知



2.当值是REQUEST时，使用请求方式并等待返回,pos会尝试通知3[次]()

serviceName: /acquire/notification

body：AcquireOrder

返回：空，只要求返回envelope里面Response的responseCode=SUCCESS



3.当值是EVENT时，使用事件方式并不等待返回

serviceName: /acquire/notification

body：AcquireOrder



![image-20240826172122597](ecr_acquire_resources/image-20240826172122597.png)





### 查询收款订单

serviceName: /acquire/get

请求：OrderNoWrapper

返回：AcquireOrder

#### OrderNoWrapper

| 字段名  | 类型   | 多样性 | 说明          |
| ------- | ------ | ------ | ------------- |
| orderNo | string |        | pos收单订单号 |

样例

```protobuf
orderNo: "123"
```



#### 错误码

| **错误码**                | **统一错误码** | **错误描述**    | **备注**     |
| :------------------------ | :------------- | :-------------- | :----------- |
| ***GLOBAL_ID_NOT_EXIST*** |                | Order not exist | 订单号不存在 |

 

### 查询收款订单分页

客户端发起收款请求，服务端返回收款订单。

serviceName: /acquire/queryPage

请求：QueryOrderPageRequest

返回：AcquireOrderPage

#### QueryAcquireOrderPageRequest

| 字段名     | 类型       | 多样性   | 说明                            |
| ---------- | ---------- | -------- | ------------------------------- |
| reserved   | string     | optional |                                 |
| status     | string     | repeated | 订单状态列表                    |
| orderScope | OrderScope |          | 订单查询范围                    |
| timeScope  | TimeScope  |          | 订单时间范围，范围不能超过1个月 |
| pageParam  | PageParam  |          | 分页参数                        |
| orderNo    | String     | optional | 订单号                          |

###### OrderScope枚举

| 枚举值   |
| -------- |
| DEVICE   |
| MERCHANT |
| STORE    |

#### AcquireOrderPage

| 字段名          | 类型                 | 多样性   | 说明         |
| --------------- | -------------------- | -------- | ------------ |
| paginationParam | PaginationParam      |          |              |
| items           | AcquireOrderListView | repeated | 订单视图列表 |



#### AcquireOrderListView

| 字段名      | 类型               | 多样性   | 说明          |
| ----------- | ------------------ | -------- | ------------- |
| orderNo     | string             |          | pos收单订单号 |
| amount      | Money              |          |               |
| status      | AcquireOrderStatus |          | 订单状态      |
| reserved    | string             | optional |               |
| createdTime | Timestamp          |          |               |

##### 



#### 错误码

| **错误码** | **统一错误码** | **错误描述** | **备注** |
| :--------- | :------------- | :----------- | :------- |

### 打印收单票据

serviceName: /acquire/receipt/get

请求：ReceiptRequest

返回：Void

#### ReceiptsRequest

关于收据清单的请求

| 字段名        | 类型    | 多样性   | 说明     |
| ------------- | ------- | -------- | -------- |
| orderNo       | string  |          | 订单号   |
| printReceipts | Receipt | repeated | 票据清单 |

样例

```protobuf
orderNo: "asdfa"
receipts: MERCHANT
receipts: CUSTOMER
```



## settlement.proto

### 关闭批次

![image-20241010192251467](ecr_acquire_resources/image-20241010192251467.png)



serviceName: /settlement/closeBatch

请求：CloseBatchRequest

返回：TransactionReport

#### CloseBatchRequest

| 字段名     | 类型   | 多样性 | 说明   |
| ---------- | ------ | ------ | ------ |
| operatorId | string |        | 订单号 |



#### TransactionReport

| 字段名         | 类型         | 多样性   | 说明 |
| -------------- | ------------ | -------- | ---- |
| timeScope      | TimeScope    |          |      |
| totalCount     | int32        |          |      |
| totalAmount    | Money        |          |      |
| orderSummaries | OrderSummary | repeated |      |



#### OrderSummary

| 字段名      | 类型       | 多样性   | 说明 |
| ----------- | ---------- | -------- | ---- |
| code        | string     |          |      |
| count       | int32      |          |      |
| summation   | Money      |          |      |
| reportItems | ReportItem | repeated |      |



#### ReportItem

| 字段名    | 类型      | 多样性   | 说明 |
| --------- | --------- | -------- | ---- |
| eventTime | Timestamp |          |      |
| amount    | Money     |          |      |
| orderNo   | string    |          |      |
| reserved  | string    | optional |      |





## void.proto（under construction）

![image-20241010122131361](ecr_acquire_resources/image-20241010122131361.png)

撤销当天的收款，只能在支付成功且未退款的情况下执行，可以反复执行。

serviceName: /acquire/void

请求：ReceiptsRequest

返回：无



### 错误码

| **错误码**                | **统一错误码** | **错误描述**    | **备注**     |
| :------------------------ | :------------- | :-------------- | :----------- |
| ***GLOBAL_ID_NOT_EXIST*** |                | Order not exist | 订单号不存在 |
| VOID_PROCESSING           |                |                 | 需要再次调用 |
| FAILURE                   |                |                 |              |



## refund.proto（under construction）

### 退款

可以部分退款，没有退款订单号（因为商户无法提供唯一的订单号），所以多次执行回多次退款，所以注意不要并发。

serviceName: /acquire/refund/place

请求：RefundRequest

返回：RefundOrder

#### RefundRequest

| 字段名         | 类型         | 多样性   | 说明                                 |
| -------------- | ------------ | -------- | ------------------------------------ |
| refundAmount   | common.Money |          | 退款金额                             |
| acquireOrderNo | string       |          | pos收单订单号                        |
| printReceipts  | Receipt      | repeated | 在结果界面打印票据，不填写就是不打印 |

```protobuf
refundAmount {
  currencyCode: "AED"
  amount: "1.23"
}
acquireOrderNo: "asdfa1"
printReceipts: CUSTOMER
```



#### RefundOrder

| 字段名         | 类型   | 多样性 | 说明          |
| -------------- | ------ | ------ | ------------- |
| refundOrderNo  | string |        | pos退款订单号 |
| acquireOrderNo | string |        | pos收款订单号 |
| refundAmount   | Money  |        | 退款金额      |
| status         | string |        | 退款订单状态  |

样例

```protobuf
refundOrderNo: "123"
acquireOrderNo: "456"
refundAmount {
  currencyCode: "AED"
  amount: "1.23"
}
status: "SETTLED"
```

##### 退款状态常量

不使用枚举是因为随着系统升级，可能会变更状态定义

| 常量值     |
| ---------- |
| SUCCESSFUL |
| REQUESTED  |
| FAILED     |



#### 错误码

| **错误码**                   | **统一错误码** | **错误描述**                                                 | **备注**     |
| :--------------------------- | :------------- | :----------------------------------------------------------- | :----------- |
| ***GLOBAL_ID_NOT_EXIST***    |                | Order not exist                                              | 订单号不存在 |
| ***REFUND_AMOUNT_EXCEEDED*** |                | The refund amount is greater than the remaining refundable amount | 可退余额不足 |

### 查询单笔退款

serviceName: /acquire/refund/get

请求：OrderNoWrapper

返回：RefundOrder

#### 错误码

| **错误码**                | **统一错误码** | **错误描述**    | **备注**     |
| :------------------------ | :------------- | :-------------- | :----------- |
| ***GLOBAL_ID_NOT_EXIST*** |                | Order not exist | 订单号不存在 |

### 打印退款订单的票据

serviceName: /acquire/refund/receipts/print

请求：ReceiptsRequest

返回：空



### 查询收单订单的票据

serviceName: /acquire/refund/receipt/get

请求：ReceiptRequest

返回：



