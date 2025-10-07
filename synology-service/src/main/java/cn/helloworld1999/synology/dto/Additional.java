package cn.helloworld1999.synology.dto;

import lombok.Data;

@Data
public class Additional {
    private Description description;
    private boolean indexed;
    private String mount_point_type;
    private Owner owner;
    private Perm perm;
    private String real_path;
    private long size;
    private Time time;
    private String type;

    @Data
    public static class Description {
        // API 返回的是空对象 {}, 可以保持空类
    }

    @Data
    public static class Owner {
        private int uid;
        private String user;
        private int gid;
        private String group;
    }

    @Data
    public static class Perm {
        private Acl acl;
        private boolean is_acl_mode;
        private int posix;

        @Data
        public static class Acl {
            private boolean append;
            private boolean del;
            private boolean exec;
            private boolean read;
            private boolean write;
        }
    }

    @Data
    public static class Time {
        private long atime;
        private long crtime;
        private long ctime;
        private long mtime;
    }
}


