package kr.hhplus.be.server.application.service.test;

import kr.hhplus.be.server.application.repository.test.TestRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }



    public String testMethod() {

        return testRepository.testMethod();
    }
}
