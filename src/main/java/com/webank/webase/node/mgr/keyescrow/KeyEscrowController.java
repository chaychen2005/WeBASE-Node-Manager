
/**
 * Copyright 2014-2020  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.node.mgr.keyescrow;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import com.webank.webase.node.mgr.account.AccountService;
import com.webank.webase.node.mgr.base.entity.BasePageResponse;
import com.webank.webase.node.mgr.base.enums.SqlSortType;
import com.webank.webase.node.mgr.keyescrow.entity.KeyListParam;
import com.webank.webase.node.mgr.keyescrow.entity.PrivateKeyInfo;
import com.webank.webase.node.mgr.keyescrow.entity.TbPKeyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.webank.webase.node.mgr.account.entity.TbAccountInfo;
import com.webank.webase.node.mgr.base.code.ConstantCode;
import com.webank.webase.node.mgr.base.controller.BaseController;
import com.webank.webase.node.mgr.base.entity.BaseResponse;
import com.webank.webase.node.mgr.base.exception.NodeMgrException;
import com.webank.webase.node.mgr.base.tools.NodeMgrTools;
import com.webank.webase.node.mgr.token.TokenService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "escrow")
public class KeyEscrowController extends BaseController {

    @Autowired
    private KeyEscrowService keyEscrowService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenService tokenService;

    /**
     * add key info.
     */
    @PostMapping(value = "/addKey")
    public BaseResponse addKey(@RequestBody @Valid PrivateKeyInfo info, BindingResult result)
            throws NodeMgrException {
        checkBindResult(result);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start addKey. startTime:{} keyInfo:{}", startTime.toEpochMilli(), JSON.toJSONString(info));

        // current
        String currentAccount = getCurrentAccount(request);

        // add key row
        keyEscrowService.addKeyRow(currentAccount, info);

        // query row
        TbPKeyInfo tbKey = keyEscrowService.queryByAccountWithKey(currentAccount, info.getKeyAlias());
        baseResponse.setData(tbKey);

        log.info("end addKey useTime:{} result:{}", Duration.between(startTime, Instant.now()).toMillis(),
                JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    /**
     * query key.
     */
    @GetMapping(value = "/queryKey/{account}/{keyAlias}")
    public BaseResponse queryKey(@PathVariable("account") String account, @PathVariable("keyAlias") String keyAlias)
            throws NodeMgrException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryKey. startTime:{} account:{} keyAlias:{}", startTime.toEpochMilli(), account, keyAlias);

        String currentAccount = getCurrentAccount(request);
        if (!account.equals(currentAccount)) {
            TbAccountInfo tbCurAccount = accountService.queryByAccount(account);
            if (tbCurAccount == null || !currentAccount.equals(tbCurAccount.getCreator())) {
                log.info("lack of access to the key");
                throw new NodeMgrException(ConstantCode.LACK_ACCESS);
            }
        }

        int count = keyEscrowService.countOfAccountWithKey(account, keyAlias);
        if (count > 0) {
            TbPKeyInfo keyInfo = keyEscrowService.queryByAccountWithKey(account, keyAlias);
            baseResponse.setData(keyInfo);
        }

        log.info("end queryKey useTime:{} result:{}", Duration.between(startTime, Instant.now()).toMillis(),
                JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    /**
     * query key list.
     */
    @GetMapping(value = "/keyList/{pageNumber}/{pageSize}")
    public BasePageResponse keyAccountList(@PathVariable("pageNumber") Integer pageNumber,
                                             @PathVariable("pageSize") Integer pageSize) throws NodeMgrException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();

        String account = getCurrentAccount(request);
        log.info("start keyAccountList.  startTime:{} pageNumber:{} pageSize:{} account:{} ",
                startTime.toEpochMilli(), pageNumber, pageSize, account);

        int count = keyEscrowService.countOfKeyByAccount(account);
        if (count > 0) {
            Integer start = Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize)
                    .orElse(0);
            KeyListParam param = new KeyListParam(start, pageSize, account,
                    SqlSortType.DESC.getValue());
            List<TbPKeyInfo> listOfKey = keyEscrowService.listOfKeyByAccount(param);
            pagesponse.setData(listOfKey);
            pagesponse.setTotalCount(count);
        }

        log.info("end queryAccountList useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(), JSON.toJSONString(pagesponse));
        return pagesponse;
    }

    /**
     * delete key.
     */
    @DeleteMapping(value = "/deleteKey/{account}/{keyAlias}")
    public BaseResponse deleteKey(@PathVariable("account") String account, @PathVariable("keyAlias") String keyAlias)
            throws NodeMgrException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start deleteKey. startTime:{} account:{} keyAlias:{}", startTime.toEpochMilli(), account, keyAlias);

        String currentAccount = getCurrentAccount(request);
        if (!account.equals(currentAccount)) {
            TbAccountInfo tbCurAccount = accountService.queryByAccount(account);
            if (tbCurAccount == null || !currentAccount.equals(tbCurAccount.getCreator())) {
                log.info("lack of access to the key");
                throw new NodeMgrException(ConstantCode.LACK_ACCESS);
            }
        }

        keyEscrowService.deleteKeyRow(account, keyAlias);

        log.info("end deleteKey. useTime:{} result:{}", Duration.between(startTime, Instant.now()).toMillis(),
                JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    /**
     * get current account.
     */
    private String getCurrentAccount(HttpServletRequest request) {
        String token = NodeMgrTools.getToken(request);
        log.debug("getCurrentAccount account:{}", token);
        return tokenService.getValueFromToken(token);
    }
}
