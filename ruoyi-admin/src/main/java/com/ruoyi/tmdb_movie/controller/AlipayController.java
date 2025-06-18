package com.ruoyi.tmdb_movie.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.domain.server.Sys;
import com.ruoyi.framework.web.service.PermissionService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.tmdb_movie.config.AlipayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.controller.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private TokenService tokenService;

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
        System.out.println(params);

        // 验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getCharset(),
                alipayConfig.getSignType()
        );

        if (signVerified && "TRADE_SUCCESS".equals(params.get("trade_status"))) {
            try {
                // 从订单号中获取用户ID - 需要确保订单号包含用户ID信息
                String outTradeNo = params.get("out_trade_no");

                // 假设订单号格式为 "用户ID_时间戳" 或其他包含用户ID的格式
                // 例如: "12345_202506190001" 表示用户ID为12345
                String[] parts = outTradeNo.split("_");
                if(parts.length == 0) {
                    System.err.println("订单号格式不正确，无法提取用户ID");
                    return "fail";
                }
                Long userId = Long.parseLong(parts[0]); // 解析用户ID
                // 更新用户角色为VIP(101)
                int result = userService.updateUserRole(userId, 101L);
//                SysUser user=userService.selectUserById(userId);
//                LoginUser loginUser=new LoginUser();
//                loginUser.setUser(user);
//
//                // 权限集合
//                Set<String> permissions = permissionService.getMenuPermission(user);
//                loginUser.setPermissions(permissions);
//                tokenService.refreshToken(loginUser);

                if(result > 0) {
                    System.out.println("用户ID: "  + " 角色更新成功");
                } else {
                    System.out.println("用户ID: "  + " 角色更新失败");
                }

                return "success";
            } catch (Exception e) {
                System.err.println("处理支付回调异常: " + e.getMessage());
                return "fail";
            }
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

//    @GetMapping("/return")
//    public void returnCallback(HttpServletRequest request, HttpServletResponse response)
//            throws AlipayApiException, IOException {
//
//        // 1. 验证签名（可选，建议保留）
//        Map<String, String> params = convertRequestParamsToMap(request);
//        boolean signVerified = AlipaySignature.rsaCheckV1(
//                params,
//                alipayConfig.getAlipayPublicKey(),
//                alipayConfig.getCharset(),
//                alipayConfig.getSignType()
//        );
//
//        // 2. 直接重定向到前端主页
//        response.sendRedirect("http://localhost:81");
//        // 或本地测试用：response.sendRedirect("http://localhost:3000");
//    }
}