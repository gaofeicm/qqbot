package com.gaofeicm.qqbot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * (QlCookie)实体类
 *
 * @author gaofeicm
 * @since 2022-08-05 14:37:22
 */
@Data
@TableName("ql_cookie")
public class QlCookie implements Serializable {

    private static final long serialVersionUID = -32696556218723900L;
    
        
    private String id;
    
        
    @TableField("ql_id")
    private String qlId;
        
        
    @TableField("cookie_id")
    private String cookieId;
    
}
