package com.agileengine.imggallery.controller;

import com.agileengine.imggallery.provider.ImgApiProvider;
import com.agileengine.imggallery.provider.payload.response.ImgByIdResponse;
import com.agileengine.imggallery.provider.payload.response.ImgsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by coarse_horse on 21/08/2020
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SearchController {

    private final ImgApiProvider imgApiProvider;
    
    @GetMapping("images/{searchTerm}")
    public List<ImgsResponse.Picture> getImages(
        @PathVariable String searchTerm
    ) {
        List<ImgsResponse.Picture> pictures = imgApiProvider.getImages(Integer.valueOf(searchTerm)).getPictures();
        return pictures;
    }
    
    @GetMapping("images/s/{searchTerm}")
    public ImgByIdResponse getImageById(
        @PathVariable String searchTerm
    ) {
        ImgByIdResponse imageById = imgApiProvider.getImageById(searchTerm);
        return imageById;
    }
}
