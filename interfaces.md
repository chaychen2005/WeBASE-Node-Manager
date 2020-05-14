# 密钥管理服务（KMS）接口设计

##  <span id="catalog_top">目录</span>
- [1.帐号管理模块](#1)
  - [1.1.新增子帐号](#1.1)
  - [1.2.删除子帐号](#1.2)
  - [1.3.查询子帐号列表](#1.3)
  - [1.4.更改当前密码](#1.4)
  - [1.5.获取用于加密子账号托管密钥的公钥](#1.5)
- [2.密钥管理模块](#2)
  - [2.1.新增密钥](#2.1)
  - [2.2.删除密钥](#2.2)
  - [2.3.查询密钥列表](#2.3)
  - [2.4.查询指定密钥](#2.4)


## <span id="1">1 帐号管理模块</span>  [top](#catalog_top)

### <span id="1.1">1.1 新增子帐号</span>  [top](#catalog_top)

#### 1.1.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/account/addAccount`
* 请求方式：post
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 1.1.2 参数信息详情

| 序号 | 输入参数      | 类型          | 可为空 | 备注                       |
|------|---------------|---------------|--------|----------------------------|
| 1    | account       | String        | 否     | 帐号名称                   |
| 2    | accountPwd    | String        | 否     | 登录密码（sha256）         |
| 3    | roleId        | int           | 否     | 所属角色                   |
| 4    | publicKey     | String        | 是     | 用于加密子账号托管的密钥   |
| 序号 | 输出参数      | 类型          |        | 备注                       |
| 1    | code          | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2    | message       | String        | 否     | 描述                       |
| 3    | data          | object        | 否     | 返回信息实体               |
| 3.1  | account       | String        | 否     | 帐号                       |
| 3.2  | roleId        | Integer       | 否     | 所属角色                   |
| 3.3  | roleName      | String        | 否     | 角色名称                   |
| 3.4  | roleNameZh    | String        | 否     | 角色中文名                 |
| 3.5  | accountStatus | Integer       | 否     | 帐号状态                   |
| 3.6  | description   | String        | 是     | 备注                       |
| 3.7  | createTime    | LocalDateTime | 否     | 创建时间                   |
| 3.8  | modifyTime    | LocalDateTime | 否     | 修改时间                   |
| 3.9  | publicKey     | String        | 否     | 用于加密子账号托管的密钥   |
| 3.10 | creator       | String        | 否     | 创建者账号                 |

### 1.1.3 入参示例

`https://127.0.0.1:8080/FISCO-Key-Manager/account/addAccount`
```
{
    "account": "testAccount",
    "accountPwd": "3f21a8490cef2bfb60a9702e9d2ddb7a805c9bd1a263557dfd51a7d0e9dfa93e",
    "roleId": 100001,
    "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49"
}
```

#### 1.1.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "account": "testAccount",
        "roleId": 100001,
        "roleName": "visitor",
        "roleNameZh": "访客",
        "accountStatus": 1,
        "description": null,
        "createTime": "2019-03-04 15:11:44",
        "modifyTime": "2019-03-04 15:11:44"
        "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49"
        "creator": "admin"
    }
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### <span id="1.2">1.2 删除子帐号</span>  [top](#catalog_top)

#### 1.2.1 传输协议规范
* 网络传输协议：使用HTTPS协议
* 请求地址：`account/deleteAccount/{account}`
* 请求方式：DELETE
* 返回格式：JSON

#### 1.2.2 参数信息详情

| 序号 | 输入参数 | 类型   | 可为空 | 备注                       |
|------|----------|--------|--------|----------------------------|
| 1    | account  | String | 否     | 帐号名称                   |
| 序号 | 输出参数 | 类型   |        | 备注                       |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

#### 1.2.3 入参示例
`https://127.0.0.1:8080/FISCO-Key-Manager/deleteAccount/testAccount`

#### 1.2.4 出参示例
* 成功：
```
{
    "code": 0,
    "data": {},
    "message": "Success"
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

###  <span id="1.3">1.3 查询子帐号列表</span>  [top](#catalog_top)

#### 1.3.1 传输协议规范
* 网络传输协议：使用HTTPS协议
* 请求地址: `/account/accountList/{pageNumber}/{pageSize}`
* 请求方式：GET
* 返回格式：JSON

#### 1.3.2 参数信息详情

| 序号  | 输入参数      | 类型          | 可为空 | 备注                       |
|-------|---------------|---------------|--------|----------------------------|
| 1     | pageSize      | Int           | 否     | 每页记录数                 |
| 2     | pageNumber    | Int           | 否     | 当前页码                   |
|       | 输出参数      | 类型          |        | 备注                       |
| 1     | code          | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2     | message       | String        | 否     | 描述                       |
| 3     | totalCount    | Int           | 否     | 总记录数                   |
| 4     | data          | List          | 是     | 信息列表                   |
| 4.1   |               | Object        |        | 信息对象               |
| 4.1.1 | account       | String        | 否     | 帐号                       |
| 4.1.2 | roleId        | Integer       | 否     | 所属角色                   |
| 4.1.3 | roleName      | String        | 否     | 角色名称                   |
| 4.1.4 | roleNameZh    | String        | 否     | 角色中文名                 |
| 4.1.5 | accountStatus | Integer       | 否     | 帐号状态                   |
| 4.1.6 | description   | String        | 是     | 备注                       |
| 4.1.7 | createTime    | LocalDateTime | 否     | 创建时间                   |
| 4.1.8 | modifyTime    | LocalDateTime | 否     | 修改时间                   |
| 4.1.9  | publicKey    | String        | 否     | 用于加密子账号托管的密钥   |
| 4.1.10 | creator      | String        | 是     | 创建者账号                 |

#### 1.3.3 入参示例
`https://127.0.0.1:8080/FISCO-Key-Manager/account/accountList/1/10`

#### 1.3.4 出参示例
* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "account": "testAccount",
            "roleId": 100001,
            "roleName": "visitor",
            "roleNameZh": "访客",
            "accountStatus": 1,
            "description": null,
            "createTime": "2019-03-04 15:11:44",
            "modifyTime": "2019-03-04 15:18:47"
            "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49"
            "creator": "admin"
        },
        {
            "account": "admin",
            "roleId": 100000,
            "roleName": "admin",
            "roleNameZh": "管理员",
            "accountStatus": 2,
            "description": null,
            "createTime": "2019-02-14 17:33:50",
            "modifyTime": "2019-02-14 17:45:53"
            "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49"
            "creator": ""
        }
    ],
    "totalCount": 2
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### <span id="1.4">1.4 更新当前密码</span>  [top](#catalog_top)

#### 1.4.1 传输协议规范
* 网络传输协议：使用HTTPS协议
* 请求地址：`/account/updatePassword`
* 请求方式：Put
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 1.4.2 参数信息详情

| 序号 | 输入参数      | 类型   | 可为空 | 备注                       |
|------|---------------|--------|--------|----------------------------|
| 序号 | 输出参数      | 类型   |        | 备注                       |
| 1    | code          | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message       | String | 否     | 描述                       |

### 1.4.3 入参示例
`https://127.0.0.1:8080/FISCO-Key-Manager/account/updatePassword`
```
{
    "oldAccountPwd": "dfdfgdg490cef2bfb60a9702erd2ddb7a805c9bd1arrrewefd51a7d0etttfa93e ",
    "newAccountPwd": "3f21a8490cef2bfb60a9702e9d2ddb7a805c9bd1a263557dfd51a7d0e9dfa93e"
}
```

#### 1.4.4 出参示例
* 成功：
```
{
    "code": 0,
    "message": "success"
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### <span id="1.5">1.5 获取用于加密子账号托管密钥的公钥</span>  [top](#catalog_top)

#### 1.5.1 传输协议规范
* 网络传输协议：使用HTTPS协议
* 请求地址：`/account/getPublicKey`
* 请求方式：GET
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 1.5.2 参数信息详情

| 序号 | 输入参数      | 类型   | 可为空 | 备注                       |
|------|---------------|--------|--------|----------------------------|
| 序号 | 输出参数      | 类型   |        | 备注                       |
| 1    | code          | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message       | String | 否     | 描述                       |

### 1.5.3 入参示例
`https://127.0.0.1:8080/FISCO-Key-Manager/account/getPublicKey`

#### 1.5.4 出参示例
* 成功：
```
{
    "code": 0,
    "message": "success"
    "data": {
        "publicKey": "8d83963610117ed59cf2011d5b7434dca7bb570d4a16e63c66f0803f4c4b1c03a1125500e5ca699dfbb6b48d450a82a5020fcb3b43165b508c10cb1479c6ee49"
    }
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

## <span id="2">2 密钥管理模块</span>  [top](#catalog_top)

### <span id="2.1">2.1 新增密钥</span>  [top](#catalog_top)

#### 2.1.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/escrow/addKey`
* 请求方式：post
* 请求头：Content-type: application/json
* 返回格式：JSON

#### 2.1.2 参数信息详情

| 序号 | 输入参数      | 类型          | 可为空 | 备注                       |
|------|---------------|---------------|--------|----------------------------|
| 1    | cipherText    | String        | 否     | 经账号创建者公钥加密       |
| 2    | keyAlias      | String        | 否     | 密钥别名                   |
| 3    | privateKey    | String        | 否     | 经账号提供的密码加密       |
| 序号 | 输出参数      | 类型          |        | 备注                       |
| 1    | code          | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2    | message       | String        | 否     | 描述                       |
| 3    | data          | object        | 否     | 返回信息实体               |
| 3.1  | account       | String        | 否     | 帐号                       |
| 3.2  | cipherText    | Integer       | 否     | 经账号创建者公钥加密       |
| 3.3  | createTime    | LocalDateTime | 否     | 密钥托管时间               |
| 3.4  | keyAlias      | String        | 否     | 密钥别名                   |
| 3.5  | privateKey    | String        | 否     | 经账号提供的密码加密       |

### 1.1.3 入参示例

`https://127.0.0.1:8080/FISCO-Key-Manager/escrow/addAccount`
```
{
    "cipherText":"048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
    "keyAlias":"key1",
    "privateKey":"F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7"}
```

#### 1.1.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "account":"testAccount",
        "cipherText":"048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
        "createTime":"2020-05-14T20:00:39",
        "keyAlias":"key1",
        "privateKey":"F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7"
    }
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

### <span id="2.2">2.2 删除密钥</span>  [top](#catalog_top)

#### 2.2.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址：`/escrow/deleteKey/{account}/{keyAlias}`
* 请求方式：DELETE
* 返回格式：JSON

#### 2.2.2 参数信息详情

| 序号 | 输入参数 | 类型   | 可为空 | 备注                       |
|------|----------|--------|--------|----------------------------|
| 1    | account  | String | 否     | 帐号名称                   |
| 2    | keyAlias | String | 否     | 密钥别名                   |
| 序号 | 输出参数 | 类型   |        | 备注                       |
| 1    | code     | Int    | 否     | 返回码，0：成功 其它：失败 |
| 2    | message  | String | 否     | 描述                       |
| 3    | data     | object | 是     | 返回信息实体（空）         |

#### 2.2.3 入参示例

`https://127.0.0.1:8080/FISCO-Key-Manager/escrow/deleteKey/testAccount/key1`

#### 2.2.4 出参示例

* 成功：
```
{
    "code": 0,
    "data": {},
    "message": "Success"
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

###  <span id="2.3">2.3 查询密钥列表</span>  [top](#catalog_top)

#### 2.3.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/escrow/keyList/{pageSize}/{pageNumber}`
* 请求方式：GET
* 返回格式：JSON

#### 2.3.2 参数信息详情

| 序号  | 输入参数      | 类型          | 可为空 | 备注                       |
|-------|---------------|---------------|--------|----------------------------|
| 1     | pageSize      | Int           | 否     | 每页记录数                 |
| 2     | pageNumber    | Int           | 否     | 当前页码                   |
|       | 输出参数      | 类型          |        | 备注                       |
| 1     | code          | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2     | message       | String        | 否     | 描述                       |
| 3     | totalCount    | Int           | 否     | 总记录数                   |
| 4     | data          | List          | 是     | 信息列表                   |
| 4.1   |               | Object        |        | 信息对象               |
| 4.1.1 | account       | String        | 否     | 帐号                       |
| 4.1.2 | cipherText    | Integer       | 否     | 经账号创建者公钥加密       |
| 4.1.3 | createTime    | LocalDateTime | 否     | 密钥托管时间               |
| 4.1.4 | keyAlias      | String        | 否     | 密钥别名                   |
| 4.1.5 | privateKey    | String        | 否     | 经账号提供的密码加密       |

#### 2.3.3 入参示例

`https://127.0.0.1:8080/FISCO-Key-Manager/escrow/keyList/1/10`

#### 2.3.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "account":"testAccount",
            "cipherText":"048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
            "createTime":"2020-05-14T20:00:39",
            "keyAlias":"key1",
            "privateKey":"F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7"
        }
    ],
    "totalCount": 1
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```

###  <span id="2.4">2.4 查询指定密钥</span>  [top](#catalog_top)

#### 2.4.1 传输协议规范

* 网络传输协议：使用HTTPS协议
* 请求地址: `/escrow/queryKey/{account}/{keyAlias}`
* 请求方式：GET
* 返回格式：JSON

#### 2.4.2 参数信息详情

| 序号  | 输入参数      | 类型          | 可为空 | 备注                       |
|-------|---------------|---------------|--------|----------------------------|
| 1     | account       | String        | 否     | 帐号名称                   |
| 2     | keyAlias      | String        | 否     | 密钥别名                   |
|       | 输出参数      | 类型          |        | 备注                       |
| 1     | code          | Int           | 否     | 返回码，0：成功 其它：失败 |
| 2     | message       | String        | 否     | 描述                       |
| 3     | data          | object        | 否     | 返回信息实体               |
| 3.1   | account       | String        | 否     | 帐号                       |
| 3.2   | cipherText    | Integer       | 否     | 经账号创建者公钥加密       |
| 3.3   | createTime    | LocalDateTime | 否     | 密钥托管时间               |
| 3.4   | keyAlias      | String        | 否     | 密钥别名                   |
| 3.5   | privateKey    | String        | 否     | 经账号提供的密码加密       |

#### 2.4.3 入参示例

`https://127.0.0.1:8080/FISCO-Key-Manager/escrow/queryKey/testAccount/key1`

#### 2.4.4 出参示例

* 成功：
```
{
    "code": 0,
    "message": "success",
    "data": {
        "account":"testAccount",
        "cipherText":"048A292A94A6DDF84006C074B63627A7FAC1CD4B84EFC556124C1258CFEDC402285A66F9AB27310FA5E253D65038A664A649C35F259882E9678034928158AA90DD518C78A6B81F3A7075E74DC9320E32DB25596249EB1AC404955AC715E3812C0B61204939E8AE5CE430DBBDD014F96DA42B824C994266B2CD7A49AC92254EC2534D6AAB79F4E36367EB3EDEE6461A7A26A1A7038B",
        "createTime":"2020-05-14T20:00:39",
        "keyAlias":"key1",
        "privateKey":"F2764D0F7118080EABC9236830BC714B2B249AE209C6D969E9E953D7283B42E9C9600DA7F5447158C83410CC5E91514C05B8234003465978C924D7F505221CFACB53B966BB008522E33737F44C63B4E7"
    }
}
```

* 失败：
```
{
    "code": 102000,
    "message": "system exception",
    "data": {}
}
```
