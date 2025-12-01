package com.example.demo;

import java.nio.file.Files;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebhookRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        RestTemplate rest = new RestTemplate();

        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "name", "Shreya Gupta",     
                "regNo", "22BCE3818",     
                "email", "shreya320gupta@gmail.com"
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> genResp = rest.postForEntity(generateUrl, entity, Map.class);
        Map<String, Object> genRespBody = genResp.getBody();

        String webhook = (String) genRespBody.get("webhook");
        String token = (String) genRespBody.get("accessToken");

        System.out.println("Webhook: " + webhook);
        System.out.println("Access Token: " + token);

        String sql = Files.readString(new ClassPathResource("final_query.sql").getFile().toPath());

        String submitUrl = webhook; 

        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.set("Authorization", token);

        Map<String, String> submitBody = Map.of("finalQuery", sql);

        HttpEntity<Map<String, String>> submitEntity = new HttpEntity<>(submitBody, submitHeaders);

        ResponseEntity<String> submitResp = rest.postForEntity(submitUrl, submitEntity, String.class);

        System.out.println("Submission response: " + submitResp.getStatusCode());
        System.out.println("Body: " + submitResp.getBody());
    }
}
