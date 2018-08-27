package cn.imooc.monitor.core.mapper.system;

import cn.imooc.monitor.core.entity.system.SysResource;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author
 * @version 2018/4/20/16:35
 */
@Mapper
@Repository
public interface SysResourceMapper extends BaseMapper<SysResource> {
}
