CREATE TABLE `t_user_info` (
	`account`     varchar(32)    NOT NULL    comment '用户名，唯一标识',
	`password`    varchar(200)   NOT NULL    comment '经过PBKDF2加密过得密码密钥',
	PRIMARY KEY (`account`)
) COMMENT='用户信息表，用于校验账号密码';
