package cn.helloworld1999.mediaservice.config;

import cn.helloworld1999.mediaservice.entity.Category;
import cn.helloworld1999.mediaservice.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void run(String... args) {
        // 检查并创建默认分类
        Category defaultCategory = new Category();
        defaultCategory.setId(1L);
        defaultCategory.setName("默认分类");
        defaultCategory.setParentId(0L); // 根分类
        defaultCategory.setLevel(1); // 一级分类
        defaultCategory.setSortOrder(1); // 排序
        defaultCategory.setStatus("ACTIVE");
        defaultCategory.setCreateTime(LocalDateTime.now());
        defaultCategory.setUpdateTime(LocalDateTime.now());

        // 如果不存在则插入
        if (categoryMapper.selectById(1L) == null) {
            categoryMapper.insert(defaultCategory);
            System.out.println("已创建默认分类");
        } else {
            System.out.println("默认分类已存在");
        }
    }
}
