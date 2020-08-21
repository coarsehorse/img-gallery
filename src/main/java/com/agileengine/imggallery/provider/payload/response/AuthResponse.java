package com.agileengine.imggallery.provider.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Boolean auth;
    private String token;
}
