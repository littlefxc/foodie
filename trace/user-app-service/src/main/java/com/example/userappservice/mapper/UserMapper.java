package com.example.userappservice.mapper;

import com.example.userappservice.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author fengxuechao
 * @date 2021/7/27
 */
@Mapper
@Repository
public interface UserMapper {

    @Select("select id, username, password from t_user where id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password")
    })
    User getById(Integer id);

    @Insert("insert into t_user(username, password) values(#{username},#{password})")
    @Options(useGeneratedKeys = true,keyColumn = "id", keyProperty = "id")
    int addUser(User user);

    @Update("update t_user set password = #{password} where id = #{id}")
    int updatePasswordById(User user);

    @Delete("delete from t_user where id = #{id}")
    int deleteUser(Integer id);
}
