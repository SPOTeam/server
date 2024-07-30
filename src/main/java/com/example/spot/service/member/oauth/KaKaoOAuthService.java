package com.example.spot.service.member.oauth;

import com.example.spot.web.dto.member.kakao.KaKaoOAuthToken;
import com.example.spot.web.dto.member.kakao.KaKaoOAuthToken.KaKaoOAuthTokenDTO;
import com.example.spot.web.dto.member.kakao.KaKaoUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class KaKaoOAuthService {

    @Value("${spring.OAuth2.kakao.url}")
    private String KAKAO_SNS_URL;

    @Value("${spring.OAuth2.kakao.client-id}")
    private String KAKAO_SNS_CLIENT_ID;

    @Value("${spring.OAuth2.kakao.callback-login-url}")
    private String KAKAO_SNS_CALLBACK_LOGIN_URL;


    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", KAKAO_SNS_CLIENT_ID);
        params.put("redirect_uri", KAKAO_SNS_CALLBACK_LOGIN_URL);
        params.put("response_type", "code");

        String parameterString = params.entrySet().stream()
            .map(x -> x.getKey() + "=" + x.getValue())
            .collect(Collectors.joining("&"));

        String redirectURL = KAKAO_SNS_URL + "?" + parameterString;
        log.info("redirectURL = {}", redirectURL);
        return redirectURL;
    }

    public ResponseEntity<String> requestAccessToken(String code) {
        String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_LOGIN_URL);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(params, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_TOKEN_REQUEST_URL, request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        }
        return null;
    }

    public KaKaoOAuthTokenDTO getAccessToken(ResponseEntity<String> response)
        throws JsonProcessingException {
        log.info("response.getBody() = {}", response.getBody());

        KaKaoOAuthToken.KaKaoOAuthTokenDTO kaKaoOAuthTokenDTO= objectMapper.readValue(response.getBody(),
            KaKaoOAuthTokenDTO.class);
        return kaKaoOAuthTokenDTO;
    }

    public ResponseEntity<String> requestUserInfo(KaKaoOAuthTokenDTO kaKaoOAuthTokenDTO) {
        String KAKAO_USER_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kaKaoOAuthTokenDTO.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(KAKAO_USER_INFO_REQUEST_URL, HttpMethod.GET, request, String.class);

        log.info("response.getBody() = {}", responseEntity.getBody());

        return responseEntity;
    }

    public KaKaoUser getUserInfo(ResponseEntity<String> userInfoRes)
        throws JsonProcessingException {
        KaKaoUser kaKaoUser = objectMapper.readValue(userInfoRes.getBody(), KaKaoUser.class);
        return kaKaoUser;
    }


}