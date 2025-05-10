package cn.helloworld1999.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * 邮件工具类，基于Hutool实现
 * 
 * @author helloworld1999
 */
@Slf4j
public class EmailUtil {

    /**
     * 发送简单文本邮件
     *
     * @param mailAccount 邮件账户配置
     * @param to 收件人，多个收件人使用逗号分隔
     * @param subject 邮件主题
     * @param content 邮件正文
     * @return 是否发送成功
     */
    public static boolean sendText(MailAccount mailAccount, String to, String subject, String content) {
        try {
            MailUtil.send(mailAccount, to, subject, content, false);
            log.info("简单邮件发送成功，收件人：{}", to);
            return true;
        } catch (Exception e) {
            log.error("简单邮件发送失败，收件人：{}，原因：{}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 发送HTML格式邮件
     *
     * @param mailAccount 邮件账户配置
     * @param to 收件人，多个收件人使用逗号分隔
     * @param subject 邮件主题
     * @param htmlContent HTML格式的邮件正文
     * @return 是否发送成功
     */
    public static boolean sendHtml(MailAccount mailAccount, String to, String subject, String htmlContent) {
        try {
            MailUtil.send(mailAccount, to, subject, htmlContent, true);
            log.info("HTML邮件发送成功，收件人：{}", to);
            return true;
        } catch (Exception e) {
            log.error("HTML邮件发送失败，收件人：{}，原因：{}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param mailAccount 邮件账户配置
     * @param to 收件人，多个收件人使用逗号分隔
     * @param subject 邮件主题
     * @param content 邮件正文
     * @param isHtml 是否为HTML格式
     * @param files 附件列表
     * @return 是否发送成功
     */
    public static boolean sendWithAttachments(MailAccount mailAccount, String to, String subject, 
                                             String content, boolean isHtml, List<File> files) {
        try {
            MailUtil.send(mailAccount, to, subject, content, isHtml, files.toArray(new File[0]));
            log.info("带附件邮件发送成功，收件人：{}", to);
            return true;
        } catch (Exception e) {
            log.error("带附件邮件发送失败，收件人：{}，原因：{}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 使用默认配置创建邮件账户
     *
     * @param host SMTP服务器地址
     * @param port SMTP服务器端口
     * @param username 发件人邮箱地址
     * @param password 发件人邮箱密码或授权码
     * @param from 发件人显示名称
     * @return 邮件账户配置
     */
    public static MailAccount createMailAccount(String host, int port, String username, 
                                              String password, String from) {
        MailAccount account = new MailAccount();
        account.setHost(host);
        account.setPort(port);
        account.setAuth(true);
        account.setFrom(StrUtil.isBlank(from) ? username : from);
        account.setUser(username);
        account.setPass(password);
        account.setSslEnable(true);
        return account;
    }

    /**
     * 快速发送简单文本邮件（使用系统配置）
     * 注意：需要在配置文件中设置邮件相关配置
     *
     * @param to 收件人，多个收件人使用逗号分隔
     * @param subject 邮件主题
     * @param content 邮件正文
     * @return 是否发送成功
     */
    public static boolean sendSimpleText(String to, String subject, String content) {
        try {
            MailUtil.send(to, subject, content, false);
            log.info("简单邮件发送成功，收件人：{}", to);
            return true;
        } catch (Exception e) {
            log.error("简单邮件发送失败，收件人：{}，原因：{}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 快速发送HTML格式邮件（使用系统配置）
     * 注意：需要在配置文件中设置邮件相关配置
     *
     * @param to 收件人，多个收件人使用逗号分隔
     * @param subject 邮件主题
     * @param htmlContent HTML格式的邮件正文
     * @return 是否发送成功
     */
    public static boolean sendSimpleHtml(String to, String subject, String htmlContent) {
        try {
            MailUtil.send(to, subject, htmlContent, true);
            log.info("HTML邮件发送成功，收件人：{}", to);
            return true;
        } catch (Exception e) {
            log.error("HTML邮件发送失败，收件人：{}，原因：{}", to, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 快速发送带附件的邮件（使用系统配置）
     * 注意：需要在配置文件中设置邮件相关配置
     *
     * @param to 收件人，多个收件人使用逗号分隔
     * @param subject 邮件主题
     * @param content 邮件正文
     * @param isHtml 是否为HTML格式
     * @param files 附件列表
     * @return 是否发送成功
     */
    public static boolean sendSimpleWithAttachments(String to, String subject, String content, 
                                                  boolean isHtml, List<File> files) {
        try {
            if (CollUtil.isEmpty(files)) {
                MailUtil.send(to, subject, content, isHtml);
            } else {
                MailUtil.send(to, subject, content, isHtml, files.toArray(new File[0]));
            }
            log.info("带附件邮件发送成功，收件人：{}", to);
            return true;
        } catch (Exception e) {
            log.error("带附件邮件发送失败，收件人：{}，原因：{}", to, e.getMessage(), e);
            return false;
        }
    }
}
