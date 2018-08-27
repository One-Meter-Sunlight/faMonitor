package cn.imooc.monitor.core.controller;

import cn.imooc.monitor.common.annotation.SysLogs;
import cn.imooc.monitor.core.config.jwt.JwtToken;
import cn.imooc.monitor.core.dto.SignInDTO;
import cn.imooc.monitor.common.bean.ResponseCode;
import cn.imooc.monitor.common.bean.ResponseResult;
import cn.imooc.monitor.core.service.system.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 * @version 2018/4/13/14:02
 */
@RestController
@RequestMapping(value = {"/account"})
@Api(tags = {"账户相关"})
public class AccountController {

    @Autowired
    private SysUserService userService;

    @PostMapping(value = {"/sign-in"})
    @ApiOperation(value = "登录")
    @SysLogs("登录")
    public ResponseResult signIn(@RequestBody @Validated @ApiParam(value = "登录数据", required = true)
                                         SignInDTO signInDTO) {
        userService.signIn(signInDTO);
        return ResponseResult.e(ResponseCode.SIGN_IN_OK, ((JwtToken) SecurityUtils.getSubject().getPrincipal()).getToken());
    }

    @PostMapping(value = "/current")
    @ApiOperation(value = "获取当前用户信息")
    @SysLogs("获取当前用户信息")
    @ApiImplicitParam(paramType = "header", name = "Authorization", value = "身份认证Token")
    public ResponseResult current() {
        return ResponseResult.e(ResponseCode.OK, userService.getCurrentUser());
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "注销登录")
    @SysLogs("注销登录")
    @ApiImplicitParam(paramType = "header", name = "Authorization", value = "身份认证Token")
    public ResponseResult logout() {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
        } catch (Exception e) {
            return ResponseResult.e(ResponseCode.LOGOUT_FAIL);
        }
        return ResponseResult.e(ResponseCode.LOGOUT_OK);
    }

}
