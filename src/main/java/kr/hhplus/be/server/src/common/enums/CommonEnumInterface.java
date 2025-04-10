package kr.hhplus.be.server.src.common.enums;

public interface CommonEnumInterface {

    public String getCode();
    default public boolean equalsCode(String code) {
        return getCode().equals(code);
    }
}
