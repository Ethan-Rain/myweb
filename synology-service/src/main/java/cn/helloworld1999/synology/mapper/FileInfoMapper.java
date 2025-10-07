package cn.helloworld1999.synology.mapper;

import cn.helloworld1999.synology.bean.FileTree;
import cn.helloworld1999.synology.entity.FileInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfoEntity> {
    int insertBatch(@Param("list") List<FileInfoEntity> list);
}
