package kr.hhplus.be.server.application.repository.test;

import org.springframework.stereotype.Repository;

@Repository
public class TestRepository {

    public String testMethod() {

        return "testMethod 입니다.";
    }
}
