package com.agileengine.imggallery.provider;

import com.agileengine.imggallery.props.ImgApiProps;
import com.agileengine.imggallery.provider.payload.request.AuthRequest;
import com.agileengine.imggallery.provider.payload.response.AuthResponse;
import com.agileengine.imggallery.provider.payload.response.ImageByIdResponse;
import com.agileengine.imggallery.provider.payload.response.ImagesResponse;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

/**
 * Created by coarse_horse on 21/08/2020
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ImgProvider {
    
    private static final Integer AUTH_ATTEMPTS_NUM = 2;
    private static final Integer REQ_ATTEMPTS_NUM = 3;
    
    private static final String AUTH_PATH = "/auth";
    private static final String IMAGES_PATH = "/images";
    
    private final ImgApiProps imgApiProps;
    private final RestTemplate restTemplate;
    
    private String authToken;
    
    /**
     * Executes specified request.
     * Automatically retries auth on auth failures.
     * Automatically retries other requests on failure.
     * @param <T> request object type.
     * @param <R> response object type.
     * @return
     */
    private <T, R> R execute(
        Request<T> request,
        Class<R> respPayload,
        Option<Map<String, String>> params,
        Integer attemptsLeft
    ) {
        log.info("Executing request {}", request);
        try {
            // Execute request
            // Construct url using params
            String url = imgApiProps.getUrl() + request.getPath();
            String urlWithParams = params
                .map(prms ->
                    prms.foldLeft(
                        UriComponentsBuilder.fromHttpUrl(url),
                        (builder, tuple2) -> builder.queryParam(tuple2._1, tuple2._2)
                    )
                )
                .map(UriComponentsBuilder::toUriString)
                .getOrElse(url);
    
            HttpEntity<T> reqEntity = new HttpEntity<>(
                request.getPayload().getOrNull(),
                generateHeaders()
            );
            ResponseEntity<R> respEntity = restTemplate.exchange(
                urlWithParams,
                request.getMethod(),
                reqEntity,
                respPayload
            );
            return respEntity.getBody();
        } catch (HttpClientErrorException.Forbidden fe) {
            if (--attemptsLeft > 0) {
                log.error("Request has been forbidden, attempts left {}, request {}", attemptsLeft, request);
                return execute(request, respPayload, params, attemptsLeft);
            }
            throw fe;
        } catch (HttpClientErrorException.Unauthorized une) {
            log.error("Auth failure");
            this.authToken = updateAuthToken();
            log.info("Auth token was successfully updated");
            return execute(request, respPayload, params, attemptsLeft);
        }
    }
    
    private HttpHeaders generateHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(imgApiProps.getAuthHeader(), imgApiProps.getAuthTokenPrefix() + authToken);
        return headers;
    }
    
    private String updateAuthToken() {
        AuthRequest payload = new AuthRequest(imgApiProps.getKey());
        Request<AuthRequest> authReq = new Request<>(AUTH_PATH, HttpMethod.POST, Option.of(payload));
        AuthResponse authResp = execute(authReq, AuthResponse.class, Option.none(), AUTH_ATTEMPTS_NUM);
        
        return Option.of(authResp)
            .map(AuthResponse::getToken)
            .flatMap(Option::of)
            .getOrElseThrow(() -> new RuntimeException("Token from auth response is empty"));
    }
    
    public ImagesResponse getImages(Integer pageNum) {
        Request<?> request = new Request<>(IMAGES_PATH, HttpMethod.GET, Option.none());
        Map<String, String> params = HashMap.of("page", pageNum.toString());
        ImagesResponse response = execute(request, ImagesResponse.class, Option.of(params), REQ_ATTEMPTS_NUM);
        return response;
    }
    
    public ImageByIdResponse getImageById(String id) {
        Request<?> request = new Request<>(
            IMAGES_PATH + "/" + id,
            HttpMethod.GET,
            Option.none()
        );
        ImageByIdResponse response = execute(request, ImageByIdResponse.class, Option.none(), REQ_ATTEMPTS_NUM);
        return response;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class Request<M> {
        
        private String path;
        private HttpMethod method;
        private Option<M> payload;
    }
}
