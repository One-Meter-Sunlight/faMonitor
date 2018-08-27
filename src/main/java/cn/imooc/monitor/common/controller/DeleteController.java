package cn.imooc.monitor.common.controller;

import cn.imooc.monitor.common.annotation.SysLogs;
import cn.imooc.monitor.common.bean.ResponseCode;
import cn.imooc.monitor.common.bean.ResponseResult;
import cn.imooc.monitor.common.service.DeleteService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author
 * @version 2018/5/25/13:27
 */
public interface DeleteController<UID, S extends DeleteService<UID>> {

    S getService();

    @PostMapping("/remove/{id}")
    @ApiOperation(value = "删除指定ID的对象")
    @SysLogs("删除指定ID的对象")
    @ApiImplicitParam(paramType = "header", name = "Authorization", value = "身份认证Token", required = true)
    default ResponseResult remove(@PathVariable("id") UID id) {
        getService().remove(id);
        return ResponseResult.e(ResponseCode.OK);
    }

}
