package cn.imooc.monitor.core.service.system.impl;

import cn.imooc.monitor.core.entity.system.SysUserRole;
import cn.imooc.monitor.core.mapper.system.SysUserRoleMapper;
import cn.imooc.monitor.core.entity.system.SysUserRole;
import cn.imooc.monitor.core.mapper.system.SysUserRoleMapper;
import cn.imooc.monitor.core.service.system.SysUserRoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Licoy
 * @version 2018/4/16/11:32
 */
@Service
@Transactional
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper,SysUserRole> implements SysUserRoleService {
}
