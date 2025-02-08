package cn.helloworld1999.myweb.demos.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/time")
public class Time {
    //以String返回当前日期时间，年月日时分秒
    @RequestMapping("/getTime")
    public String getTime() {
        java.util.Date date = new java.util.Date();
        return date.toString();
    }

    //以JSON形式返回当前时间
    @RequestMapping("/getTimeJson")
    public @ResponseBody
    java.sql.Time getTimeJson() {
        java.util.Date date = new java.util.Date();
        return new java.sql.Time(date.getTime());
    }

}
