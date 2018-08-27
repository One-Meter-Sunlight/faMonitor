package cn.imooc.monitor.core.mapper.system;

import cn.imooc.monitor.core.entity.system.SysUserRole;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author
 * @version 2018/4/16/11:31
 */
@Mapper
@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
