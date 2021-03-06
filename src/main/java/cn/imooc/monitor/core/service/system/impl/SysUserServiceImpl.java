package cn.imooc.monitor.core.service.system.impl;

import cn.imooc.monitor.common.bean.ResponseCode;
import cn.imooc.monitor.common.exception.RequestException;
import cn.imooc.monitor.common.util.Encrypt;
import cn.imooc.monitor.common.util.Tools;
import cn.imooc.monitor.core.config.jwt.JwtToken;
import cn.imooc.monitor.core.dto.SignInDTO;
import cn.imooc.monitor.core.dto.system.user.FindUserDTO;
import cn.imooc.monitor.core.dto.system.user.ResetPasswordDTO;
import cn.imooc.monitor.core.dto.system.user.UserAddDTO;
import cn.imooc.monitor.core.dto.system.user.UserUpdateDTO;
import cn.imooc.monitor.core.entity.system.SysResource;
import cn.imooc.monitor.core.entity.system.SysRole;
import cn.imooc.monitor.core.entity.system.SysUser;
import cn.imooc.monitor.core.entity.system.SysUserRole;
import cn.imooc.monitor.core.mapper.system.SysUserMapper;
import cn.imooc.monitor.core.service.global.ShiroService;
import cn.imooc.monitor.core.service.system.SysResourceService;
import cn.imooc.monitor.core.service.system.SysRoleService;
import cn.imooc.monitor.core.service.system.SysUserService;
import cn.imooc.monitor.core.vo.system.SysUserVO;
import cn.imooc.monitor.common.bean.ResponseCode;
import cn.imooc.monitor.common.exception.RequestException;
import cn.imooc.monitor.common.util.Encrypt;
import cn.imooc.monitor.common.util.Tools;
import cn.imooc.monitor.core.config.jwt.JwtToken;
import cn.imooc.monitor.core.dto.SignInDTO;
import cn.imooc.monitor.core.dto.system.user.FindUserDTO;
import cn.imooc.monitor.core.dto.system.user.ResetPasswordDTO;
import cn.imooc.monitor.core.dto.system.user.UserAddDTO;
import cn.imooc.monitor.core.dto.system.user.UserUpdateDTO;
import cn.imooc.monitor.core.entity.system.SysResource;
import cn.imooc.monitor.core.entity.system.SysRole;
import cn.imooc.monitor.core.entity.system.SysUser;
import cn.imooc.monitor.core.entity.system.SysUserRole;
import cn.imooc.monitor.core.mapper.system.SysUserMapper;
import cn.imooc.monitor.core.service.global.ShiroService;
import cn.imooc.monitor.core.service.system.SysResourceService;
import cn.imooc.monitor.core.service.system.SysRoleService;
import cn.imooc.monitor.core.service.system.SysUserRoleService;
import cn.imooc.monitor.core.service.system.SysUserService;
import cn.imooc.monitor.core.vo.system.SysUserVO;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper,SysUser> implements SysUserService {

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysUserRoleService userRoleService;

    @Autowired
    private ShiroService shiroService;

    @Autowired
    private SysResourceService resourceService;

    @Override
    public SysUser findUserByName(String name,boolean hasResource) {
        SysUser user = this.selectOne(new EntityWrapper<SysUser>().eq("username",name));
        if(user == null){
            return null;
        }
        user.setRoles(roleService.findAllRoleByUserId(user.getId(),hasResource));
        return user;
    }

    @Override
    public SysUser findUserById(String id,boolean hasResource) {
        SysUser user = this.selectById(id);
        if(user == null){
            return null;
        }
        user.setRoles(roleService.findAllRoleByUserId(user.getId(),false));
        return user;
    }

    @Override
    public void signIn(SignInDTO signInDTO) {
        if( "".equals(signInDTO.getUsername()) || "".equals(signInDTO.getPassword()) ){
            throw new RequestException(ResponseCode.SING_IN_INPUT_EMPTY);
        }
        JwtToken token = new JwtToken(null,signInDTO.getUsername(),signInDTO.getPassword());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            if(!subject.isAuthenticated()){
                throw new RequestException(ResponseCode.SIGN_IN_INPUT_FAIL);
            }
        }catch (DisabledAccountException e){
            throw new RequestException(ResponseCode.SIGN_IN_INPUT_FAIL.code,e.getMessage(),e);
        }catch (Exception e){
            throw new RequestException(ResponseCode.SIGN_IN_FAIL,e);
        }
    }


    public SysUserVO getCurrentUser(){
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        boolean b = Tools.executeLogin(request);
        if(!b){
            throw RequestException.fail("身份已过期或无效，请重新认证");
        }
        Subject subject = SecurityUtils.getSubject();
        if(!subject.isAuthenticated()){
            throw new RequestException(ResponseCode.NOT_SING_IN);
        }
        JwtToken jwtToken = new JwtToken();
        Object principal = subject.getPrincipal();
        if(principal==null){
            throw RequestException.fail("用户信息获取失败");
        }
        BeanUtils.copyProperties(principal,jwtToken);
        SysUser user = this.findUserByName(jwtToken.getUsername(),false);
        if(user==null){
            throw RequestException.fail("用户不存在");
        }
        //获取菜单/权限信息
        List<SysResource> allPer = userRolesRegexResource(roleService.findAllRoleByUserId(user.getId(),true));
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user,vo);
        vo.setResources(allPer);
        return vo;
    }

    public List<SysResource> userRolesRegexResource(List<SysRole> roles){
        if(roles!=null && roles.size()>0){
            Map<String,SysResource> resourceMap = new LinkedHashMap<>();
            roles.forEach(role -> {
                if(role.getResources()!=null && role.getResources().size()>0){
                    role.getResources().forEach(resource -> //含有则不覆盖
                            resourceMap.putIfAbsent(resource.getId(), resource));
                }
            });
            Map<String,SysResource> cacheMap = new ConcurrentHashMap<>();
            List<SysResource> resourceList = new CopyOnWriteArrayList<>();
            resourceMap.forEach((k,v)-> {
                SysResource allParent = resourceService.getResourceAllParent(v, cacheMap,resourceMap);
                //判断是否已经包含此对象
                if(!resourceList.contains(allParent)){
                    resourceList.add(allParent);
                }
            });
            return resourceList;
        }
        return null;
    }

    @Override
    public Page<SysUserVO> getAllUserBySplitPage(FindUserDTO findUserDTO) {
        EntityWrapper<SysUser> wrapper = new EntityWrapper<>();
        wrapper.orderBy("id",findUserDTO.getAsc());
        Page<SysUser> userPage = this.selectPage(new Page<>(findUserDTO.getPage(),
                findUserDTO.getPageSize()), wrapper);
        Page<SysUserVO> userVOPage = new Page<>();
        BeanUtils.copyProperties(userPage,userVOPage);
        List<SysUserVO> userVOS = new ArrayList<>();
        userPage.getRecords().forEach(v->{
            SysUserVO vo = new SysUserVO();
            BeanUtils.copyProperties(v,vo);
            //查找匹配所有用户的角色
            vo.setRoles(roleService.findAllRoleByUserId(v.getId(),false));
            userVOS.add(vo);
        });
        userVOPage.setRecords(userVOS);
        return userVOPage;
    }

    @Override
    public void statusChange(String userId, Integer status) {
        SysUser user = this.selectById(userId);
        if(user==null){
            throw RequestException.fail("用户不存在");
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(SecurityUtils.getSubject().getPrincipal(),sysUser);
        if(user.getUsername().equals(sysUser.getUsername())){
            throw RequestException.fail("不能锁定自己的账户");
        }
        user.setStatus(status);
        try {
            this.updateById(user);
            //若为0 需要进行清除登陆授权以及权限信息
            /*if(status==0){

            }*/
            shiroService.clearAuthByUserId(userId,true,true);
        }catch (Exception e){
            throw RequestException.fail("操作失败",e);
        }
    }

    @Override
    public void removeUser(String userId) {
        SysUser user = this.selectById(userId);
        if(user==null){
            throw RequestException.fail("用户不存在！");
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(SecurityUtils.getSubject().getPrincipal(),sysUser);
        if(user.getUsername().equals(sysUser.getUsername())){
            throw RequestException.fail("不能删除自己的账户！");
        }
        try {
            this.deleteById(userId);
            shiroService.clearAuthByUserId(userId,true,true);
        }catch (Exception e){
            throw RequestException.fail("删除失败",e);
        }
    }

    @Override
    public void add(UserAddDTO addDTO) {
        SysUser findUser = this.findUserByName(addDTO.getUsername(),false);
        if(findUser!=null){
            throw RequestException.fail(
                    String.format("已经存在用户名为 %s 的用户",addDTO.getUsername()));
        }
        try {
            findUser = new SysUser();
            BeanUtils.copyProperties(addDTO,findUser);
            findUser.setCreateDate(new Date());
            findUser.setPassword(Encrypt.md5(String.valueOf(findUser.getPassword())+findUser.getUsername()));
            this.insert(findUser);
            this.updateUserRole(findUser);
        }catch (Exception e){
            throw RequestException.fail("添加用户失败",e);
        }
    }

    @Override
    public void update(String id, UserUpdateDTO updateDTO) {
        SysUser user = this.selectById(id);
        if(user==null){
            throw RequestException.fail(
                    String.format("更新失败，不存在ID为 %s 的用户",id));
        }
        SysUser findUser = this.selectOne(new EntityWrapper<SysUser>()
                    .eq("username",updateDTO.getUsername()).ne("id",id));
        if(findUser!=null){
            throw RequestException.fail(
                    String.format("更新失败，已经存在用户名为 %s 的用户",updateDTO.getUsername()));
        }
        BeanUtils.copyProperties(updateDTO,user);
        try {
            this.updateById(user);
            this.updateUserRole(user);
            shiroService.clearAuthByUserId(user.getId(),true,false);
        }catch (RequestException e){
            throw RequestException.fail(e.getMsg(),e);
        }catch (Exception e){
            throw RequestException.fail("用户信息更新失败",e);
        }
    }

    @Override
    public void updateUserRole(SysUser user) {
        try {
            userRoleService.delete(new EntityWrapper<SysUserRole>().eq("uid",user.getId()));
            if(user.getRoles()!=null && user.getRoles().size()>0){
                user.getRoles().forEach(v-> userRoleService.insert(SysUserRole.builder()
                        .uid(user.getId())
                        .rid(v.getId()).build()));
            }
        }catch (Exception e){
            throw RequestException.fail("用户权限关联失败",e);
        }
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO){
        SysUser user = this.selectById(resetPasswordDTO.getUid().trim());
        if(user==null){
            throw RequestException.fail(String.format("不存在ID为 %s 的用户",resetPasswordDTO.getUid()));
        }
        String password = Encrypt.md5(String.valueOf(resetPasswordDTO.getPassword())+user.getUsername());
        user.setPassword(password);
        try {
            this.updateById(user);
            shiroService.clearAuthByUserId(user.getId(),true,true);
        }catch (Exception e){
            throw RequestException.fail(String.format("ID为 %s 的用户密码重置失败",resetPasswordDTO.getUid()),e);
        }
    }
}
