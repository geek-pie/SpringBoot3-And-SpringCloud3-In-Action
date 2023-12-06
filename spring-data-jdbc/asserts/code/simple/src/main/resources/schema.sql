CREATE TABLE IF NOT EXISTS `users`
(
    `id`                 INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `encrypted_password` VARCHAR(10)  NOT NULL COMMENT '加密后的密码'
) ENGINE = INNODB
  charset = utf8mb4 COMMENT '用户信息表';

