--
-- 由SQLiteStudio v3.3.3 产生的文件 周三 12月 8 14:24:35 2021
--
-- 文本编码：UTF-8
--
pragma foreign_keys = off;
begin transaction;

-- 表：cms_article
drop table if exists cms_article;

create table cms_article (
    id           integer        primary key autoincrement
                                not null,
    type         int
                                default 1,
    title        varchar (1024) not null,
    summary      varchar (1024)
                                default null,
    tags         varchar (255)
                                default null,
    content      text,
    category     varchar (25)
                                default null,
    sub_category varchar (25)
                                default null,
    create_time  TIMESTAMP      NOT NULL
                                DEFAULT (datetime('now', 'localtime') ),
    update_time  TIMESTAMP
                                DEFAULT NULL,
    open_count   INTEGER
                                DEFAULT 0,
    start_time   TIMESTAMP
                                DEFAULT (datetime('now', 'localtime') ),
    end_time     TIMESTAMP
                                DEFAULT NULL,
    target_link  VARCHAR (255) 
                                DEFAULT NULL,
    image        VARCHAR (255) 
                                DEFAULT NULL
);


-- 表：sys_captcha
drop table if exists sys_captcha;

create table sys_captcha (
    uuid        CHAR (36)   PRIMARY KEY
                            NOT NULL,
    code        VARCHAR (6) NOT NULL,
    expire_time TIMESTAMP
                            DEFAULT NULL
);


-- 表：sys_config
drop table if exists sys_config;

create table sys_config (
    id          integer        primary key autoincrement
                               not null,
    param_key   varchar (50)
                               default null,
    param_value varchar (2000)
                               default null,
    status      int
                               default 1,
    remark      varchar (500)
                               default null
);

insert into sys_config (id, param_key, param_value, status, remark) values (1, 'CLOUD_STORAGE_CONFIG_KEY', '{"type":3,"qiniuDomain":"","qiniuPrefix":"","qiniuAccessKey":"","qiniuSecretKey":"","qiniuBucketName":"","aliyunDomain":"","aliyunPrefix":"","aliyunEndPoint":"","aliyunAccessKeyId":"","aliyunAccessKeySecret":"","aliyunBucketName":"","qcloudDomain":"","qcloudPrefix":"","qcloudAppId":"","qcloudSecretId":"","qcloudSecretKey":"","qcloudBucketName":"","qcloudRegion":"ap-guangzhou"}', 0, '云存储配置信息');

-- 表：sys_log
drop table if exists sys_log;

create table sys_log (
    id          integer        primary key autoincrement
                               not null,
    username    varchar (50)
                               default null,
    operation   varchar (50)
                               default null,
    method      varchar (200)
                               default null,
    params      varchar (5000)
                               default null,
    time        bigint
                               default null,
    ip          varchar (64)
                               default null,
    create_date TIMESTAMP
                               DEFAULT NULL
);


-- 表：sys_menu
drop table if exists sys_menu;

create table sys_menu (
    menu_id   integer       primary key autoincrement
                            not null,
    parent_id bigint
                            default null,
    name      varchar (50)
                            default null,
    url       varchar (200)
                            default null,
    perms     varchar (500)
                            default null,
    type      integer
                            default null,
    icon      varchar (50)
                            default null,
    order_num integer
                            default null
);

insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (1, 0, '系统管理', null, null, 0, 'el-icon-s-tools', 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (2, 1, '管理员列表', 'sys/user', null, 1, 'admin', 1);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (3, 1, '角色管理', 'sys/role', null, 1, 'role', 2);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (4, 1, '菜单管理', 'sys/menu', null, 1, 'menu', 3);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (6, 0, '微信管理', null, null, 0, 'el-icon-s-promotion', 1);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (7, 0, '内容管理', '', '', 0, 'el-icon-document-copy', 2);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (9, 0, '日志报表', '', '', 0, 'el-icon-s-order', 4);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (15, 2, '查看', null, 'sys:user:list,sys:user:info', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (16, 2, '新增', null, 'sys:user:save,sys:role:select', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (17, 2, '修改', null, 'sys:user:update,sys:role:select', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (18, 2, '删除', null, 'sys:user:delete', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (19, 3, '查看', null, 'sys:role:list,sys:role:info', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (20, 3, '新增', null, 'sys:role:save,sys:menu:list', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (21, 3, '修改', null, 'sys:role:update,sys:menu:list', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (22, 3, '删除', null, 'sys:role:delete', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (23, 4, '查看', null, 'sys:menu:list,sys:menu:info', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (24, 4, '新增', null, 'sys:menu:save,sys:menu:select', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (25, 4, '修改', null, 'sys:menu:update,sys:menu:select', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (26, 4, '删除', null, 'sys:menu:delete', 2, null, 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (27, 1, '参数管理', 'sys/config', 'sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete', 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (29, 9, '系统日志', 'sys/log', 'sys:log:list', 1, 'log', 7);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (30, 1, '文件上传', 'oss/oss', 'sys:oss:all', 1, 'oss', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (32, 6, '公众号菜单', 'wx/wx-menu', '', 1, 'log', 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (33, 6, '素材管理', 'wx/wx-assets', '', 1, '', 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (41, 7, '文章管理', 'wx/article', null, 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (42, 41, '查看', null, 'wx:article:list,wx:article:info', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (43, 41, '新增', null, 'wx:article:save', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (44, 41, '修改', null, 'wx:article:update', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (45, 41, '删除', null, 'wx:article:delete', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (66, 6, '自动回复规则', 'wx/msg-reply-rule', null, 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (67, 66, '查看', null, 'wx:msgreplyrule:list,wx:msgreplyrule:info', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (68, 66, '新增', null, 'wx:msgreplyrule:save', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (69, 66, '修改', null, 'wx:msgreplyrule:update', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (70, 66, '删除', null, 'wx:msgreplyrule:delete', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (71, 6, '模板消息', 'wx/msg-template', null, 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (72, 71, '查看', null, 'wx:msgtemplate:list,wx:msgtemplate:info', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (73, 71, '新增', null, 'wx:msgtemplate:save', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (74, 71, '修改', null, 'wx:msgtemplate:update', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (75, 71, '删除', null, 'wx:msgtemplate:delete', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (81, 9, '模版消息发送记录', 'wx/template-msg-log', null, 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (84, 81, '列表', null, 'wx:templatemsglog:list', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (85, 81, '删除', null, 'wx:templatemsglog:delete', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (99, 32, '更新公众号菜单', '', 'wx:menu:save', 2, '', 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (100, 33, '查看', '', 'wx:wxassets:list', 2, '', 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (101, 33, '新增修改', '', 'wx:wxassets:save', 2, '', 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (103, 6, '带参二维码', 'wx/wx-qrcode', null, 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (104, 103, '查看', null, 'wx:wxqrcode:list,wx:wxqrcode:info', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (105, 103, '新增', null, 'wx:wxqrcode:save', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (107, 103, '删除', null, 'wx:wxqrcode:delete', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (108, 6, '粉丝管理', 'wx/wx-user', null, 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (109, 108, '查看', null, 'wx:wxuser:list,wx:wxuser:info', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (110, 108, '删除', null, 'wx:wxuser:delete', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (111, 108, '同步', '', 'wx:wxuser:save', 2, '', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (112, 33, '删除', '', 'wx:wxassets:delete', 2, '', 0);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (113, 6, '公众号消息', 'wx/wx-msg', null, 1, '', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (114, 113, '查看', null, 'wx:wxmsg:list,wx:wxmsg:info', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (115, 113, '新增', null, 'wx:wxmsg:save', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (117, 113, '删除', null, 'wx:wxmsg:delete', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (118, 6, '公众号账号', 'wx/wx-account', null, 1, 'config', 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (119, 118, '查看', null, 'wx:wxaccount:list,wx:wxaccount:info', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (120, 118, '新增', null, 'wx:wxaccount:save', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (121, 118, '修改', null, 'wx:wxaccount:update', 2, null, 6);
insert into sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num) values (122, 118, '删除', null, 'wx:wxaccount:delete', 2, null, 6);

-- 表：sys_oss
drop table if exists sys_oss;

create table sys_oss (
    id          integer       primary key autoincrement
                              not null,
    url         varchar (200)
                              default null,
    create_date TIMESTAMP
                              DEFAULT NULL
);


-- 表：sys_role
drop table if exists sys_role;

create table sys_role (
    role_id        integer       primary key autoincrement
                                 not null,
    role_name      varchar (100)
                                 default null,
    remark         varchar (100)
                                 default null,
    create_user_id bigint
                                 default null,
    create_time    TIMESTAMP
                                 DEFAULT NULL
);


-- 表：sys_role_menu
drop table if exists sys_role_menu;

create table sys_role_menu (
    id      integer primary key autoincrement
                    not null,
    role_id bigint
                    default null,
    menu_id bigint
                    default null
);


-- 表：sys_user
drop table if exists sys_user;

create table sys_user (
    user_id        integer       primary key autoincrement
                                 not null,
    username       varchar (50)  not null,
    password       varchar (100)
                                 default null,
    salt           varchar (20)
                                 default null,
    email          varchar (100)
                                 default null,
    mobile         varchar (100)
                                 default null,
    status         int
                                 default null,
    create_user_id bigint
                                 default null,
    create_time    TIMESTAMP
                                 DEFAULT NULL
);

insert into sys_user (user_id, username, password, salt, email, mobile, status, create_user_id, create_time) values (1, 'admin', 'cdac762d0ba79875489f6a8b430fa8b5dfe0cdd81da38b80f02f33328af7fd4a', 'YzcmCZNvbXocrsz9dm8e', 'niefy@qq.com', '16666666666', 1, 1, '2016-11-11 11:11:11');

-- 表：sys_user_role
drop table if exists sys_user_role;

create table sys_user_role (
    id      integer primary key autoincrement
                    not null,
    user_id bigint
                    default null,
    role_id bigint
                    default null
);


-- 表：sys_user_token
drop table if exists sys_user_token;

create table sys_user_token (
    user_id     bigint        not null,
    token       varchar (100) not null,
    expire_time TIMESTAMP
                              DEFAULT NULL,
    update_time TIMESTAMP
                              DEFAULT NULL
);


-- 表：wx_account
drop table if exists wx_account;

create table wx_account (
    appid    CHAR (20)    PRIMARY KEY
                          NOT NULL,
    name     VARCHAR (50) NOT NULL,
    type     INT
                          DEFAULT 1,
    verified INT
                          DEFAULT 1,
    secret   CHAR (32)    NOT NULL,
    token    VARCHAR (32) 
                          DEFAULT NULL,
    aes_key  VARCHAR (43) 
                          DEFAULT NULL
);


-- 表：wx_msg
drop table if exists wx_msg;

create table wx_msg (
    id          integer      primary key autoincrement
                             not null,
    appid       CHAR (20)    NOT NULL,
    openid      VARCHAR (32) NOT NULL,
    in_out      INT
                             DEFAULT NULL,
    msg_type    CHAR (25) 
                             DEFAULT NULL,
    detail      TEXT,
    create_time TIMESTAMP
                             DEFAULT (datetime('now', 'localtime') ) 
);


-- 表：wx_msg_reply_rule
drop table if exists wx_msg_reply_rule;

create table wx_msg_reply_rule (
    rule_id           integer        primary key autoincrement
                                     not null,
    appid             CHAR (20) 
                                     DEFAULT '',
    sync              INT            NOT NULL
                                     DEFAULT 1,
    rule_name         VARCHAR (20)   NOT NULL,
    match_value       VARCHAR (200)  NOT NULL,
    exact_match       INT            NOT NULL
                                     DEFAULT 0,
    reply_type        VARCHAR (20)   NOT NULL
                                     DEFAULT '1',
    reply_content     VARCHAR (1024) NOT NULL,
    status            INT            NOT NULL
                                     DEFAULT 1,
    [desc]            VARCHAR (255) 
                                     DEFAULT NULL,
    effect_time_start TIME (0) 
                                     DEFAULT -28800000,
    effect_time_end   TIME (0) 
                                     DEFAULT 57599000,
    priority          INTEGER
                                     DEFAULT 0,
    update_time       TIMESTAMP      NOT NULL
                                     DEFAULT (datetime('now', 'localtime') ) 
);

insert into wx_msg_reply_rule (rule_id, appid, sync, rule_name, match_value, exact_match, reply_type, reply_content, status, "desc", effect_time_start, effect_time_end, priority, update_time) values (1, '', 1, '关注公众号', 'subscribe', 0, 'text', '你好，欢迎关注！\n<a href=\"https://github.com/niefy\">点击链接查看我的主页</a>', 1, '关注回复', -28800000, 57599000, 0, '2020-05-20 15:15:00');

-- 表：wx_msg_template
drop table if exists wx_msg_template;

create table wx_msg_template (
    id          integer       primary key autoincrement
                              not null,
    appid       CHAR (20)     NOT NULL,
    template_id VARCHAR (100) NOT NULL,
    name        VARCHAR (50) 
                              DEFAULT NULL,
    title       VARCHAR (20) 
                              DEFAULT NULL,
    content     TEXT,
    data        TEXT,
    url         VARCHAR (255) 
                              DEFAULT NULL,
    miniprogram TEXT,
    status      INT           NOT NULL,
    update_time TIMESTAMP     NOT NULL
                              DEFAULT (datetime('now', 'localtime') ) 
);


-- 表：wx_qr_code
drop table if exists wx_qr_code;

create table wx_qr_code (
    id          integer       primary key autoincrement
                              not null,
    appid       CHAR (20)     NOT NULL,
    is_temp     INT
                              DEFAULT NULL,
    scene_str   VARCHAR (64) 
                              DEFAULT NULL,
    ticket      VARCHAR (255) 
                              DEFAULT NULL,
    url         VARCHAR (255) 
                              DEFAULT NULL,
    expire_time TIMESTAMP
                              DEFAULT NULL,
    create_time TIMESTAMP
                              DEFAULT NULL
);


-- 表：wx_template_msg_log
drop table if exists wx_template_msg_log;

create table wx_template_msg_log (
    log_id      integer       primary key autoincrement
                              not null,
    appid       CHAR (20)     NOT NULL,
    touser      VARCHAR (50) 
                              DEFAULT NULL,
    template_id VARCHAR (50) 
                              DEFAULT NULL,
    data        TEXT,
    url         VARCHAR (255) 
                              DEFAULT NULL,
    miniprogram TEXT,
    send_time   TIMESTAMP
                              DEFAULT NULL,
    send_result VARCHAR (255) 
                              DEFAULT NULL
);


-- 表：wx_user
drop table if exists wx_user;

create table wx_user (
    openid          varchar (50)  primary key
                                  not null,
    appid           CHAR (20)     NOT NULL,
    phone           CHAR (11) 
                                  DEFAULT NULL,
    nickname        VARCHAR (50) 
                                  DEFAULT NULL,
    sex             INT
                                  DEFAULT NULL,
    city            VARCHAR (20) 
                                  DEFAULT NULL,
    province        VARCHAR (20) 
                                  DEFAULT NULL,
    headimgurl      VARCHAR (255) 
                                  DEFAULT NULL,
    subscribe_time  TIMESTAMP
                                  DEFAULT NULL,
    subscribe       INT
                                  DEFAULT 1,
    unionid         VARCHAR (50) 
                                  DEFAULT NULL,
    remark          VARCHAR (255) 
                                  DEFAULT NULL,
    tagid_list      TEXT,
    subscribe_scene VARCHAR (50) 
                                  DEFAULT NULL,
    qr_scene_str    VARCHAR (64) 
                                  DEFAULT NULL
);


-- 索引：cms_article_idx_title
drop index if exists cms_article_idx_title;

create unique index cms_article_idx_title on cms_article (
    title
);


-- 索引：sys_config_param_key
drop index if exists sys_config_param_key;

create unique index sys_config_param_key on sys_config (
    param_key
);


-- 索引：sys_user_token_token
drop index if exists sys_user_token_token;

create unique index sys_user_token_token on sys_user_token (
    token
);


-- 索引：sys_user_username
drop index if exists sys_user_username;

create unique index sys_user_username on sys_user (
    username
);


-- 索引：wx_msg_idx_appid
drop index if exists wx_msg_idx_appid;

create index wx_msg_idx_appid on wx_msg (
    appid
);


-- 索引：wx_msg_reply_rule_idx_appid
drop index if exists wx_msg_reply_rule_idx_appid;

create index wx_msg_reply_rule_idx_appid on wx_msg_reply_rule (
    appid
);


-- 索引：wx_msg_template_idx_appid
drop index if exists wx_msg_template_idx_appid;

create index wx_msg_template_idx_appid on wx_msg_template (
    appid
);


-- 索引：wx_msg_template_idx_name
drop index if exists wx_msg_template_idx_name;

create unique index wx_msg_template_idx_name on wx_msg_template (
    name
);


-- 索引：wx_msg_template_idx_status
drop index if exists wx_msg_template_idx_status;

create index wx_msg_template_idx_status on wx_msg_template (
    status
);


-- 索引：wx_qr_code_idx_appid
drop index if exists wx_qr_code_idx_appid;

create index wx_qr_code_idx_appid on wx_qr_code (
    appid
);


-- 索引：wx_template_msg_log_idx_appid
drop index if exists wx_template_msg_log_idx_appid;

create index wx_template_msg_log_idx_appid on wx_template_msg_log (
    appid
);


-- 索引：wx_user_idx_appid
drop index if exists wx_user_idx_appid;

create index wx_user_idx_appid on wx_user (
    appid
);


-- 索引：wx_user_idx_unionid
drop index if exists wx_user_idx_unionid;

create index wx_user_idx_unionid on wx_user (
    unionid
);


-- 触发器：wx_msg_reply_rule_update_time
drop trigger if exists wx_msg_reply_rule_update_time;
create trigger wx_msg_reply_rule_update_time
         after update
            on wx_msg_reply_rule
begin
    update wx_msg_reply_rule
       set update_time = (datetime('now', 'localtime') )
     where rule_id = old.rule_id;
end;


-- 触发器：wx_msg_template_update_time
drop trigger if exists wx_msg_template_update_time;
create trigger wx_msg_template_update_time
        before update
            on wx_msg_template
begin
    update wx_msg_template
       set update_time = (current_timestamp)
     where id = old.id;
end;


commit transaction;
pragma foreign_keys = on;
