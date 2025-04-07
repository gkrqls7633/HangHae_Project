package kr.hhplus.be.server.src.interfaces.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Schema(description = "유저 응답")
public class UserResponse {

    private String userId;

    private Long point;

}
