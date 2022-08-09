package com.gaofeicm.qqbot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * (Ql)实体类
 *
 * @author gaofeicm
 * @since 2022-08-05 14:37:22
 */
@Data
@TableName("ql")
public class Ql implements Serializable {

    private static final long serialVersionUID = -69234038314003936L;

    private String id;

        
    @TableField("name")
    private String name;
        
        
    @TableField("address")
    private String address;
        
        
    @TableField("client_id")
    private String clientId;
        
        
    @TableField("client_secret")
    private String clientSecret;
        
        
    @TableField("toke_type")
    private String tokeType;
        
        
    @TableField("cookie_count")
    private Integer cookieCount;
        
        
    @TableField("weigth")
    private Integer weigth;
        
        
    @TableField("max_count")
    private Integer maxCount;
        
        
    @TableField("token")
    private String token;
    
}
