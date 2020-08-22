package com.agileengine.imggallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImgGalleryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImgGalleryApplication.class, args);
    }

}
