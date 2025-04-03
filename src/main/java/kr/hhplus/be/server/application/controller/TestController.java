package kr.hhplus.be.server.application.controller;

import kr.hhplus.be.server.application.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    public TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/test")
    public String testMethod() {

        return testService.testMethod();

    }



}
