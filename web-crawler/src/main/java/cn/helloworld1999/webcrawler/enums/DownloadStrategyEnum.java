package cn.helloworld1999.webcrawler.enums;

public enum DownloadStrategyEnum {
    /**
     * FAPELLO 示例：
     * 网页：https://fapello.com/taylor-swift/
     * 消息：
     * {
     *   "platform": "0",
     *   "model_name": "taylor-swift",
     *   "start_num": "1",
     *   "end_num": "10436"
     * }
     */
    FAPELLO(0, "Fapello"),
    /**
     * COSPLAYTELE 示例：
     * 网页：https://cosplaytele.com/saber-alter/
     * 特别的，如果不需要解压的话就不用提供password字段
     * 消息：
     * {
     *   "platform": "1",
     *   "url": "https://cosplaytele.com/saber-alter/"
     *   "password": "cosplaytele"
     * }
     */
    COSPLAYTELE(1, "Cosplaytele");

    private final int code;

    private final String description;

    DownloadStrategyEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DownloadStrategyEnum fromCode(int code) {
        for (DownloadStrategyEnum strategy : values()) {
            if (strategy.getCode() == code) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}