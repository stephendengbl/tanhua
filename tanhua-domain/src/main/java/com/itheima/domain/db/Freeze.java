package com.itheima.domain.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_freeze")
public class Freeze implements Serializable {
    private Integer id;
    private Integer userId;//用户id
    private Integer freezingTime;//冻结时间
    private Integer freezingRange;//冻结范围
    private String userStatus;//1.正常 2.冻结
    private String reasonsForFreezing;//冻结原因
    private String frozenRemarks;//冻结备注
    private String reasonsForThawing;

}
