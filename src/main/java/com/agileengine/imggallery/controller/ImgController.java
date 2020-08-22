package com.agileengine.imggallery.controller;

import com.agileengine.imggallery.dto.ImgResourceDto;
import com.agileengine.imggallery.dto.LocalImagesDto;
import com.agileengine.imggallery.service.ImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * Created by coarse_horse on 21/08/2020
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImgController {

    private final ImgService imgService;
    
    @GetMapping("images/{searchTerm}")
    public LocalImagesDto getImages(
        @PathVariable String searchTerm
    ) {
        LocalImagesDto localImagesDto = imgService.searchByTerm(searchTerm);
        return localImagesDto;
    }
    
    @GetMapping("getImage/{imgId}")
    public ResponseEntity<InputStreamResource> getImage(
        @PathVariable String imgId
    ) {
        ImgResourceDto imgResource = imgService.getImageResource(imgId)
            .getOrElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        GridFsResource resource = imgResource.getResource();
        try {
            return ResponseEntity
                .ok()
                .contentType(imgResource.getMediaType())
                .contentLength(resource.contentLength())
                .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
