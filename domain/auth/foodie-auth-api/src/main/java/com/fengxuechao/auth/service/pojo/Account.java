package com.fengxuechao.auth.service.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author fengxuechao
 * @date 2021/10/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    private String userId;

    private String token;

    private String refreshToken;

    private boolean skipVerification = false;

}
