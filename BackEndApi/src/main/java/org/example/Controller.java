package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final RestTemplate restTemplate;

    @Value("${url.gateway}")
    private String urlGateway;

    public Controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/getBill")
    public ResponseEntity path1() {

        logger.info("Incoming request at for request /getBill ");
        String response = restTemplate.getForObject(urlGateway+"/getBillAddress", String.class);
        return ResponseEntity.ok("response from /getBill + " + response);
    }

    @GetMapping("/getBillAddress")
    public ResponseEntity path2() {
        logger.info("Incoming request /getBillAddress");
        return ResponseEntity.ok("response from /getBillingAddress ");
    }
}