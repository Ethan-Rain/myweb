package cn.helloworld1999.myweb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

    /**
     * 数据库连接检查器
     * 在应用启动时检查数据库连接是否正常
     */
    @Order(1)  // 指定执行顺序为1，确保在其他组件之前执行
    @Component  // 将类标记为Spring组件，使其能被Spring容器管理
    public class DatabaseCheckRunner implements ApplicationRunner {
        /**
         * 日志记录器实例
         */
        private static final Logger logger = LoggerFactory.getLogger(DatabaseCheckRunner.class);

        /**
         * Spring JDBC模板，用于执行SQL操作
         */
        private final JdbcTemplate jdbcTemplate;

        /**
         * 构造函数，通过依赖注入接收JdbcTemplate实例
         * @param jdbcTemplate Spring JDBC模板实例
         */
        public DatabaseCheckRunner(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        /**
         * 应用启动时执行的方法
         * 通过执行简单的SQL查询来测试数据库连接
         * @param args 应用程序参数
         */
        @Override
        public void run(ApplicationArguments args) {
            try {
                // 执行简单的SQL查询来测试数据库连接
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                logger.info("✅ 数据库连接成功");
            } catch (Exception e) {
                // 如果查询失败，记录错误信息
                logger.error("❌ 数据库连接失败，原因: {}", e.getMessage());
            }
        }
    }