package cn.imooc.monitor.core.mapper.system;

import cn.imooc.monitor.core.entity.system.SysRoleResource;
import cn.imooc.monitor.core.entity.system.SysRoleResource;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author
 * @version 2018/4/16/9:00
 */
@Mapper
@Repository
public interface SysRolePermissionMapper extends BaseMapper<SysRoleResource> {
}
