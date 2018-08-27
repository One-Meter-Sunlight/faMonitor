package cn.imooc.monitor.core.mapper.system;

import cn.imooc.monitor.core.entity.system.SysUser;
import cn.imooc.monitor.core.entity.system.SysUser;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

}
