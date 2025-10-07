package cn.helloworld1999.synology.entity;

import cn.helloworld1999.synology.bean.FileTree;
import cn.helloworld1999.synology.dto.Additional;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("file_info")
public class FileInfoEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String path;
    private Long size;
    private Boolean isDir;

    private Long mtime;
    private Long ctime;
    private Long atime;

    private String ownerUser;
    private String ownerGroup;
    private Integer ownerUid;
    private Integer ownerGid;

    private Integer permPosix;
    private Boolean permAclMode;
    private String permAcl; // 可以序列化为 JSON 存这里

    private String mountPointType;
    private Boolean indexed;
    public static FileInfoEntity convertToEntity(FileTree item) {
        FileInfoEntity entity = new FileInfoEntity();
        entity.setName(item.getName());
        entity.setPath(item.getPath());

        Additional additional = item.getAdditional(); // 先取出来
        if (additional != null) {
            entity.setSize(additional.getSize());
            entity.setIsDir("dir".equals(additional.getType()));

            Additional.Time time = additional.getTime();
            if (time != null) {
                entity.setMtime(time.getMtime());
                entity.setCtime(time.getCtime());
                entity.setAtime(time.getAtime());
            }

            Additional.Owner owner = additional.getOwner();
            if (owner != null) {
                entity.setOwnerUser(owner.getUser());
                entity.setOwnerGroup(owner.getGroup());
                entity.setOwnerUid(owner.getUid());
                entity.setOwnerGid(owner.getGid());
            }

            Additional.Perm perm = additional.getPerm();
            if (perm != null) {
                entity.setPermPosix(perm.getPosix());
                entity.setPermAclMode(perm.is_acl_mode());
                entity.setPermAcl(JSONUtil.toJsonStr(perm.getAcl()));
            }

            entity.setMountPointType(additional.getMount_point_type());
            entity.setIndexed(additional.isIndexed());
        } else {
            // 如果 additional 为 null，可以设置默认值
            entity.setSize(0L);
            entity.setIsDir(false);
            entity.setIndexed(false);
        }

        return entity;
    }
}
