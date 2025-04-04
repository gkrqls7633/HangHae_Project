package kr.hhplus.be.server.application.enumeration;

public interface CommonEnumInterface {

    public String getCode();
    default public boolean equalsCode(String code) {
        return getCode().equals(code);
    }
}
