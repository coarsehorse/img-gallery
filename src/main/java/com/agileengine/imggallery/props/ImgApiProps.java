package com.agileengine.imggallery.props;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Component
@ConfigurationProperties("img.api")
@Data
@NoArgsConstructor
public class ImgApiProps {
    
    private String url;
    private String key;
    private String authHeader;
    private String authTokenPrefix;
}
