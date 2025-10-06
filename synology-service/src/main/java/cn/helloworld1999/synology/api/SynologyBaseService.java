package cn.helloworld1999.synology.api;

import lombok.Data;

@Data
public class SynologyBaseService {
    private String sid;
    private String cookie;

    public void setSidAndCookie(String sid) {
        setSid(sid);
        String sidValue = sid.replace("\"", "");
        this.cookie = "id=" + sidValue;
    }
}
