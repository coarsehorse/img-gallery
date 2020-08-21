package com.agileengine.imggallery.controller;

import com.agileengine.imggallery.provider.ImgProvider;
import com.agileengine.imggallery.provider.payload.response.ImagesResponse;
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

    private final ImgProvider imgProvider;
    
    @GetMapping("images/{searchTerm}")
    public List<ImagesResponse.Picture> getImages(
        @PathVariable String searchTerm
    ) {
        List<ImagesResponse.Picture> pictures = imgProvider.getImages(1).getPictures();
        return pictures;
    }
}
