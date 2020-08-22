package com.agileengine.imggallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalImagesDto {
    
    private List<String> foundImages;
}
