package com.agileengine.imggallery.init;

import com.agileengine.imggallery.dto.ImgMetaDto;
import com.agileengine.imggallery.provider.ImgApiProvider;
import com.agileengine.imggallery.provider.payload.response.ImgByIdResponse;
import com.agileengine.imggallery.provider.payload.response.ImgsResponse;
import com.agileengine.imggallery.util.Utils;
import io.vavr.Tuple;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class GalleryInitializer implements CommandLineRunner {
    
    private final ImgApiProvider imgProvider;
    private final GridFsTemplate gridFsTemplate;
    
    @Value("${app.init.request.delay}")
    private Long REQ_DELAY;
    @Value("${app.default.img.mimetype}")
    private String DEF_IMG_MIME;
    
    @Override
    @Scheduled(fixedDelayString = "${app.reload.images.delay}", initialDelayString = "${app.reload.images.delay}")
    public void run(String... args) throws Exception {
        log.info("Filling up local storage...");

        // Delete old documents first
        gridFsTemplate.delete(new Query());

        // Get first page of images to determine boundaries
        ImgsResponse firstPageImgs = imgProvider.getImages(0);
        Stream<String> firstPagePicIds = Stream.ofAll(firstPageImgs.getPictures())
            .map(ImgsResponse.Picture::getId);

        // Download all full sized images and upload to GridFS storage with meta
        List<ObjectId> objectIds = Stream.range(1, firstPageImgs.getPageCount() + 1)
            .map(imgProvider::getImages)
            .peek(x -> Utils.sleep(REQ_DELAY))
            .flatMap(ImgsResponse::getPictures)
            .map(ImgsResponse.Picture::getId)
            .appendAll(firstPagePicIds)
            // Get unique img ids
            .distinct()
            // Download detailed data
            .map(imgProvider::getImageById)
            .peek(x -> Utils.sleep(REQ_DELAY))
            .map(img ->
                Tuple.of(
                    img,
                    Try.of(() -> new URL(img.getFullPicture()).openStream())
                        .onFailure(t ->
                            log.error(
                                "Cannot download full sized image, skipping... Image {}",
                                img
                            )
                        )
                )
            )
            // Filter out unavailable images
            .filter(tuple -> tuple._2().isSuccess())
            // Upload into local storage
            .map(tuple -> {
                ImgByIdResponse img = tuple._1;
                InputStream imgContent = tuple._2.get();

                byte[] imgBytes = Try.of(imgContent::readAllBytes)
                    .getOrElseThrow(() -> new RuntimeException("Cannot read image bytes"));
                String mimeType = Try.of(() -> Magic.getMagicMatch(imgBytes).getMimeType())
                    .getOrElse(DEF_IMG_MIME);
                ImgMetaDto imgMeta = new ImgMetaDto(img.getAuthor(), img.getCamera(), img.getTags(), mimeType);

                return gridFsTemplate.store(new ByteArrayInputStream(imgBytes), img.getId(), imgMeta);
            })
            .collect(Collectors.toList());

        log.info("Successfully uploaded {} images", objectIds.size());
    }
}
