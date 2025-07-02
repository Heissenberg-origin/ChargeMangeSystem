package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.LoginInfo;

@Mapper
public interface LoginInfoMapper extends BaseMapper<LoginInfo> {

    // 登录验证
    @Select("SELECT * FROM login_info WHERE account = #{account} AND password = #{password} LIMIT 1")
    LoginInfo checkLogin(@Param("account") String account, @Param("password") String password);

    // 修改密码
    @Update("UPDATE login_info SET password = #{newPassword} WHERE account = #{account} AND password = #{oldPassword}")
    int updatePassword(@Param("account") String sccount,
                       @Param("oldPassword") String oldPassword,
                       @Param("newPassword") String newPassword);
}
