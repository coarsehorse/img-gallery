package com.agileengine.imggallery.provider.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ImageByIdResponse {
    
    private String id;
    private String author;
    private String camera;
    private String tags;
    private String croppedPicture;
    private String fullPicture;
}
