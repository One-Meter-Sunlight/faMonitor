package cn.imooc.monitor.core.service.system;

import cn.imooc.monitor.common.service.QueryService;
import cn.imooc.monitor.core.dto.system.log.FindLogDTO;
import cn.imooc.monitor.core.entity.system.SysLog;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * @author Licoy
 * @version 2018/4/28/9:56
 */
public interface SysLogService extends IService<SysLog>, QueryService<SysLog, FindLogDTO> {

    Page<SysLog> list(FindLogDTO findLogDTO);

    void remove(List<String> idList);

}
