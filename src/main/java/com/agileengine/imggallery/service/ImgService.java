package com.agileengine.imggallery.service;

import com.agileengine.imggallery.dto.ImgMetaDto;
import com.agileengine.imggallery.dto.ImgResourceDto;
import com.agileengine.imggallery.dto.LocalImagesDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.model.GridFSFile;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImgService {
    
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations operations;
    private final ObjectMapper objectMapper;
    
    @Value("${app.protocol}")
    private String APP_PROTOCOL;
    @Value("${app.domen}")
    private String APP_DOMEN;
    @Value("${local.server.port}")
    private String APP_PORT;
    @Value("${app.default.img.mimetype}")
    private String DEF_IMG_MIME;
    
    public LocalImagesDto searchByTerm(String term) {
        String regex = "^.*" + term + ".*$";
        Query searchQuery = new Query(
            new Criteria().orOperator(
                Criteria.where("metadata.author").regex(regex),
                Criteria.where("metadata.camera").regex(regex),
                Criteria.where("metadata.tags").regex(regex)
            )
        );
        
        List<GridFSFile> fileList = new ArrayList<>();
        gridFsTemplate.find(searchQuery).into(fileList);
    
        List<String> foundImgUrls = Stream.ofAll(fileList)
            .map(file -> file.getObjectId().toString())
            .map(fileId -> APP_PROTOCOL + "://" + APP_DOMEN + ":" + APP_PORT + "/getImage/" + fileId)
            .collect(Collectors.toList());
        
        return new LocalImagesDto(foundImgUrls);
    }
    
    public Option<ImgResourceDto> getImageResource(String imgId) {
        GridFSFile foundFile = gridFsTemplate.findOne(
            new Query(Criteria.where("_id").is(new ObjectId(imgId)))
        );
        return Option.of(foundFile)
            .map(file ->
                new ImgResourceDto(
                    operations.getResource(file),
                    MediaType.parseMediaType(
                        Option.of(file.getMetadata())
                            .map(Document::toJson)
                            .map(json -> Try.of(() -> objectMapper.readValue(json, ImgMetaDto.class))
                                .getOrElseThrow(() -> new RuntimeException("Cannot read img meta from json"))
                            )
                            .map(ImgMetaDto::getMimeType)
                            .getOrElse(DEF_IMG_MIME)
                    )
                )
            );
    }
}
