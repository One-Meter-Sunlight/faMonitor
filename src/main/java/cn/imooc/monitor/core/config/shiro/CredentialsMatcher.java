package cn.imooc.monitor.core.config.shiro;


import cn.imooc.monitor.common.util.Encrypt;
import cn.imooc.monitor.common.util.JwtUtil;
import cn.imooc.monitor.core.config.jwt.JwtToken;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * @author
 * @version 2017/9/25
 */
public class CredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        JwtToken jwtToken = (JwtToken) token;
        Object accountCredentials = getCredentials(info);
        if (jwtToken.getPassword() != null) {
            Object tokenCredentials = Encrypt.md5(String.valueOf(
                    jwtToken.getPassword()) + jwtToken.getUsername());
            if (!accountCredentials.equals(tokenCredentials)) {
                throw new DisabledAccountException("密码不正确！");
            }
        } else {
            boolean verify = JwtUtil.verify(jwtToken.getToken(), jwtToken.getUsername(), accountCredentials.toString());
            if (!verify) {
                throw new DisabledAccountException("verifyFail");
            }
        }
        return true;
    }

}
