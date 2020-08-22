package com.agileengine.imggallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;

/**
 * Created by coarse_horse on 22/08/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImgResourceDto {
    
    private GridFsResource resource;
    private MediaType mediaType;
}
