package com.agileengine.imggallery.provider.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public class ImgsResponse {
    
    private List<Picture> pictures;
    private Integer page;
    private Integer pageCount;
    private Boolean hasMore;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Picture {
        
        private String id;
        private String croppedPicture;
    }
}
