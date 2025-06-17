package com.ruoyi.tmdb_movie.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.ruoyi.tmdb_movie.config.AlipayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    private AlipayConfig alipayConfig;

    @GetMapping("/pay")
    public void pay(@RequestParam String outTradeNo,
                    @RequestParam String totalAmount,
                    @RequestParam String subject,
                    HttpServletResponse response) throws AlipayApiException, IOException {

        // 1. 创建AlipayClient实例
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getMerchantPrivateKey(),
                "JSON",
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType()
        );

        // 2. 设置请求参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());

        // 3. 构建业务参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);    // 商户订单号
        bizContent.put("total_amount", totalAmount);   // 支付金额
        bizContent.put("subject", subject);            // 订单标题
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY"); // 产品码

        request.setBizContent(bizContent.toString());

        // 4. 执行请求并返回支付页面
        String form = alipayClient.pageExecute(request).getBody();

        response.setContentType("text/html;charset=" + alipayConfig.getCharset());
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }

    @PostMapping("/notify")
    public String notify(HttpServletRequest request) throws AlipayApiException {
        // 将异步通知的参数转换为Map
        Map<String, String> params = convertRequestParamsToMap(request);

        // 验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getCharset(),
                alipayConfig.getSignType()
        );

        if (signVerified && "TRADE_SUCCESS".equals(params.get("trade_status"))) {
            String outTradeNo = params.get("out_trade_no");
            // TODO: 更新vip状态
            return "success"; // 通知支付宝已处理
        }
        return "fail"; // 验证失败
    }

    // 辅助方法：将request参数转换为Map
    private Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }
}