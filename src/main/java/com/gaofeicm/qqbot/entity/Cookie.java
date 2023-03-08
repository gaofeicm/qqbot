package com.gaofeicm.qqbot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Generated;

import java.io.Serializable;
import java.util.Date;

/**
 * (Cookie)实体类
 *
 * @author gaofeicm
 * @since 2022-08-05 16:40:30
 */
@Data
@TableName("cookie")
public class Cookie implements Serializable {

    private static final long serialVersionUID = -66921937068445824L;
    
    @Generated
    private String id;


    @TableField("oid")
    private String oid;


    @TableField("wxid")
    private String wxid;

    @TableField("priority")
    private Integer priority;
        
        
    @TableField("remark")
    private String remark;
        
        
    @TableField("qq")
    private String qq;
        
        
    @TableField("available")
    private Integer available;
        
        
    @TableField("create_time")
    private Date createTime;
        
        
    @TableField("update_time")
    private Date updateTime;
        
        
    @TableField("expiration_time")
    private Date expirationTime;
        
        
    @TableField("pt_key")
    private String ptKey;
        
        
    @TableField("pt_pin")
    private String ptPin;
        
        
    @TableField("nick_name")
    private String nickName;
        
        
    @TableField("cookie")
    private String cookie;
    
}
