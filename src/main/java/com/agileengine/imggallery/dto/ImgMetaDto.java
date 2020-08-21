package com.agileengine.imggallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImgMetaDto {
    
    private String author;
    private String camera;
    private String tags;
}
