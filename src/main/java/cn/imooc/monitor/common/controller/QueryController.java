package cn.imooc.monitor.common.controller;

import cn.imooc.monitor.common.annotation.SysLogs;
import cn.imooc.monitor.common.bean.ResponseCode;
import cn.imooc.monitor.common.bean.ResponseResult;
import cn.imooc.monitor.common.service.QueryService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author
 * @version 2018/5/25/13:27
 */
public interface QueryController<E, FD, S extends QueryService<E, FD>> {

    S getService();

    @PostMapping("/list")
    @SysLogs("分页获取所有列表")
    @ApiOperation(value = "分页获取所有列表")
    @ApiImplicitParam(paramType = "header", name = "Authorization", value = "身份认证Token", required = true)
    default ResponseResult<Page<E>> list(@RequestBody FD findDTO) {
        return ResponseResult.e(ResponseCode.OK, getService().list(findDTO));
    }

}
