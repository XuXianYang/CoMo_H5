-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.9-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.2.0.4947
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table dianxian.announcement
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE IF NOT EXISTS `announcement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL COMMENT '类型. 1:学校公告 2:年级公告 3:班级公告',
  `school_id` bigint(20) NOT NULL COMMENT '学校id',
  `enrol_year` int(11) NOT NULL COMMENT '哪一年入学的.类型=学校公告，则为0',
  `class_id` bigint(20) NOT NULL COMMENT '班级id. 类型=学校公告或年级公告，则为0',
  `title` varchar(64) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `school_id` (`school_id`),
  KEY `class_id` (`class_id`),
  KEY `school_id_type` (`school_id`,`type`),
  KEY `school_id_enrol_year_type` (`school_id`,`enrol_year`,`type`),
  KEY `class_id_type` (`class_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='公告';

-- Dumping data for table dianxian.announcement: ~0 rows (approximately)
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;


-- Dumping structure for table dianxian.class
DROP TABLE IF EXISTS `class`;
CREATE TABLE IF NOT EXISTS `class` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `school_id` bigint(20) NOT NULL COMMENT '学校id',
  `name` varchar(128) NOT NULL COMMENT '显示名称：1班、2班',
  `code` varchar(8) NOT NULL COMMENT '系统内部唯一编号',
  `enrol_year` int(11) NOT NULL COMMENT '哪一年入学的',
  `class_number` int(11) NOT NULL COMMENT '第几个班级，和入学年度组合起来可以确定一个班级',
  `parent_im_group_id` bigint(20) DEFAULT NULL COMMENT '家长班群id',
  `student_im_group_id` bigint(20) DEFAULT NULL COMMENT '学生班群id',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `school_id_enrol_year_class_number` (`school_id`,`enrol_year`,`class_number`),
  KEY `school_id` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='班级';

-- Dumping data for table dianxian.class: ~0 rows (approximately)
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
/*!40000 ALTER TABLE `class` ENABLE KEYS */;


-- Dumping structure for table dianxian.course
DROP TABLE IF EXISTS `course`;
CREATE TABLE IF NOT EXISTS `course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '名字',
  `description` text COMMENT '简介',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程数据';

-- Dumping data for table dianxian.course: ~0 rows (approximately)
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
/*!40000 ALTER TABLE `course` ENABLE KEYS */;

-- Dumping structure for table dianxian.course_material
DROP TABLE IF EXISTS `course_material`;
CREATE TABLE IF NOT EXISTS `course_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `resource_file_id` bigint(20) DEFAULT NULL COMMENT '附件id',
  `type` int(11) NOT NULL COMMENT '材料类型. 1->预习向导， 2->课堂笔记，3->课件',
  `name` varchar(128) NOT NULL,
  `description` text,
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `class_id_course_id_type` (`class_id`,`course_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程材料';
-- Dumping data for table dianxian.course_material: ~0 rows (approximately)
/*!40000 ALTER TABLE `course_material` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_material` ENABLE KEYS */;

-- Dumping structure for table dianxian.course_schedule
DROP TABLE IF EXISTS `course_schedule`;
CREATE TABLE IF NOT EXISTS `course_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `day_of_week` int(11) NOT NULL COMMENT '星期几',
  `lesson_of_day` int(11) NOT NULL COMMENT '一天中的第几节课',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程安排';

-- Dumping data for table dianxian.course_schedule: ~0 rows (approximately)
/*!40000 ALTER TABLE `course_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_schedule` ENABLE KEYS */;


-- Dumping structure for table dianxian.course_teacher
DROP TABLE IF EXISTS `course_teacher`;
CREATE TABLE IF NOT EXISTS `course_teacher` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师id',
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `course_id_teacher_id_class_id` (`course_id`,`teacher_id`,`class_id`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程任教安排';

-- Dumping data for table dianxian.course_teacher: ~0 rows (approximately)
/*!40000 ALTER TABLE `course_teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_teacher` ENABLE KEYS */;

-- Dumping structure for table dianxian.homework
DROP TABLE IF EXISTS `homework`;
CREATE TABLE IF NOT EXISTS `homework` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师id',
  `resource_file_id` bigint(20) DEFAULT NULL COMMENT '附件id',
  `name` varchar(128) NOT NULL,
  `study_date` date NOT NULL COMMENT '学习日期。代表哪天布置的作业',
  `description` text,
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `class_id_study_date` (`class_id`,`study_date`),
  KEY `class_id_course_id` (`class_id`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='作业';

-- Dumping data for table dianxian.homework: ~0 rows (approximately)
/*!40000 ALTER TABLE `homework` DISABLE KEYS */;
/*!40000 ALTER TABLE `homework` ENABLE KEYS */;


-- Dumping structure for table dianxian.homework_review
DROP TABLE IF EXISTS `homework_review`;
CREATE TABLE IF NOT EXISTS `homework_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `homework_id` bigint(20) NOT NULL COMMENT '作业id',
  `student_id` bigint(20) NOT NULL COMMENT '学生id',
  `study_date` date NOT NULL COMMENT '学习日期。代表哪天布置的作业',
  `review_time` datetime NOT NULL COMMENT '作业签字时间',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `homework_id` (`homework_id`),
  KEY `homework_id_student_id` (`homework_id`,`student_id`),
  KEY `student_id_study_date` (`student_id`,`study_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='作业审阅';

-- Dumping data for table dianxian.homework_review: ~0 rows (approximately)
/*!40000 ALTER TABLE `homework_review` DISABLE KEYS */;
/*!40000 ALTER TABLE `homework_review` ENABLE KEYS */;

-- Dumping structure for table dianxian.message
DROP TABLE IF EXISTS `message`;
CREATE TABLE IF NOT EXISTS `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id. 谁发的消息',
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `content` text NOT NULL COMMENT '消息内容',
  `attachment_id` bigint(20) NOT NULL COMMENT '附件或图片id',
  `type` varchar(32) NOT NULL COMMENT '类型：SOS，REPORT',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `class_id` (`class_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sos消息、小报告';

-- Dumping data for table dianxian.message: ~0 rows (approximately)
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
/*!40000 ALTER TABLE `message` ENABLE KEYS */;


-- Dumping structure for table dianxian.one_time_password
DROP TABLE IF EXISTS `one_time_password`;
CREATE TABLE IF NOT EXISTS `one_time_password` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile_no` varchar(16) NOT NULL COMMENT '手机号码',
  `otp_code` varchar(16) NOT NULL COMMENT '验证码明文',
  `send_date` datetime NOT NULL COMMENT '发送时间',
  `expire_date` datetime NOT NULL COMMENT '过期时间',
  `status` int(11) NOT NULL COMMENT '一次性短信密码的状态，0-初始 1-发送成功 2-发送失败 3-验证成功 4-验证失败 5-locked',
  `sms_id` bigint(20) DEFAULT NULL COMMENT '短信记录id',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `mobile_no` (`mobile_no`),
  KEY `mobile_no_status` (`mobile_no`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信验证码';

-- Dumping data for table dianxian.one_time_password: ~0 rows (approximately)
/*!40000 ALTER TABLE `one_time_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `one_time_password` ENABLE KEYS */;

-- Dumping structure for table dianxian.parent
DROP TABLE IF EXISTS `parent`;
CREATE TABLE IF NOT EXISTS `parent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `default_child_user_id` bigint(20) DEFAULT NULL COMMENT '默认展示哪个孩子的信息',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='家长表';

-- Data exporting was unselected.

-- Dumping structure for table dianxian.parent_child
DROP TABLE IF EXISTS `parent_child`;
CREATE TABLE IF NOT EXISTS `parent_child` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_user_id` bigint(20) NOT NULL COMMENT '家长id',
  `child_user_id` bigint(20) NOT NULL COMMENT '孩子id',
  `relation` int(11) NOT NULL COMMENT '关系. 1->父亲, 2->母亲',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `parent_user_id_child_user_id` (`parent_user_id`,`child_user_id`),
  KEY `parent_user_id` (`parent_user_id`),
  KEY `child_user_id` (`child_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='家长和子女关联信息';

-- Dumping data for table dianxian.parent_child: ~0 rows (approximately)
/*!40000 ALTER TABLE `parent_child` DISABLE KEYS */;
/*!40000 ALTER TABLE `parent_child` ENABLE KEYS */;


-- Dumping structure for table dianxian.permission
DROP TABLE IF EXISTS `permission`;
CREATE TABLE IF NOT EXISTS `permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '权限显示名',
  `description` text COMMENT '简介',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限表';

-- Dumping data for table dianxian.permission: ~0 rows (approximately)
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;


-- Dumping structure for table dianxian.quiz
DROP TABLE IF EXISTS `quiz`;
CREATE TABLE IF NOT EXISTS `quiz` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '名称',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `school_id` bigint(20) NOT NULL COMMENT '学校id',
  `enrol_year` int(11) NOT NULL COMMENT '代表哪一年入学的年级',
  `description` text COMMENT '简介',
  `category` int(11) NOT NULL COMMENT '1-期末, 2-期中, 3-月考',
  `study_year` int(11) NOT NULL COMMENT '学年',
  `study_term` int(11) NOT NULL COMMENT '学期',
  `study_month` int(11) NOT NULL COMMENT '月度. 月考则为具体月份',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `school_id_enrol_year_category_study_year` (`school_id`,`enrol_year`,`category`,`study_year`),
  KEY `school_id_enrol_year_course_id` (`school_id`,`enrol_year`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='考试';

-- Dumping data for table dianxian.quiz: ~0 rows (approximately)
/*!40000 ALTER TABLE `quiz` DISABLE KEYS */;
/*!40000 ALTER TABLE `quiz` ENABLE KEYS */;


-- Dumping structure for table dianxian.quiz_score
DROP TABLE IF EXISTS `quiz_score`;
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `quiz_id` bigint(20) NOT NULL COMMENT '考试id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `student_id` bigint(20) NOT NULL COMMENT '学生的用户id',
  `school_id` bigint(20) NOT NULL COMMENT '学校id',
  `enrol_year` int(11) NOT NULL COMMENT '代表哪一年入学的年级',
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `score` float NOT NULL COMMENT '成绩',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `quiz_id_class_id` (`quiz_id`,`class_id`),
  KEY `quiz_id_enrol_year` (`quiz_id`,`enrol_year`),
  KEY `quiz_id_student_id` (`quiz_id`,`student_id`),
  KEY `course_id_student_id` (`course_id`,`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='考试分数';

-- Dumping data for table dianxian.quiz_score: ~0 rows (approximately)
/*!40000 ALTER TABLE `quiz_score` DISABLE KEYS */;
/*!40000 ALTER TABLE `quiz_score` ENABLE KEYS */;

-- Dumping structure for table dianxian.resource_file
DROP TABLE IF EXISTS `resource_file`;
CREATE TABLE IF NOT EXISTS `resource_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bucket` varchar(128) NOT NULL COMMENT 'storage bucket',
  `url_prefix` varchar(256) NOT NULL COMMENT 'bucket的访问路径前缀',
  `relative_path` varchar(1024) NOT NULL COMMENT '相对bucket的路径',
  `name` varchar(64) NOT NULL COMMENT '原始文件名称',
  `category` varchar(32) NOT NULL COMMENT '文件用于哪类业务',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资源文件';

-- Dumping data for table dianxian.resource_file: ~0 rows (approximately)
/*!40000 ALTER TABLE `resource_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_file` ENABLE KEYS */;

-- Dumping structure for table dianxian.role
DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '角色显示名',
  `description` text COMMENT '简介',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '角色类型：0-系统角色,1-业务角色',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色';

-- Dumping data for table dianxian.role: ~0 rows (approximately)
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;


-- Dumping structure for table dianxian.role_permission
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `permission_id` bigint(20) NOT NULL COMMENT '权限id',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_id_permission_id` (`role_id`,`permission_id`),
  KEY `role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色-权限表';

-- Dumping data for table dianxian.role_permission: ~0 rows (approximately)
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;


-- Dumping structure for table dianxian.school
DROP TABLE IF EXISTS `school`;
CREATE TABLE IF NOT EXISTS `school` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '学校名称',
  `code` varchar(8) NOT NULL COMMENT '系统内部唯一编号',
  `description` text COMMENT '简介',
  `region_id` int(11) NOT NULL DEFAULT '0' COMMENT '区域id',
  `category` int(11) NOT NULL COMMENT '学校类型：1-小学, 2-初中. 3-高中, 4-大学',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `code` (`code`),
  KEY `region_id` (`region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学校';

-- Dumping data for table dianxian.school: ~0 rows (approximately)
/*!40000 ALTER TABLE `school` DISABLE KEYS */;
/*!40000 ALTER TABLE `school` ENABLE KEYS */;

-- Dumping structure for table dianxian.sms
DROP TABLE IF EXISTS `sms`;
CREATE TABLE IF NOT EXISTS `sms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile_no` varchar(16) NOT NULL COMMENT '手机号码',
  `app_key` varchar(32) NOT NULL COMMENT '在第三方平台中的标识',
  `sms_template_id` varchar(16) NOT NULL COMMENT '短消息模板ID',
  `sms_request` text NOT NULL COMMENT '消息内容',
  `sms_response` text COMMENT '短信服务方响应内容',
  `status` int(11) NOT NULL COMMENT '0->失败, 1->成功',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `mobile_no` (`mobile_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='发送的短信记录';

-- Dumping data for table dianxian.sms: ~0 rows (approximately)
/*!40000 ALTER TABLE `sms` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms` ENABLE KEYS */;

-- Dumping structure for table dianxian.sneaking_message
DROP TABLE IF EXISTS `sneaking_message`;
CREATE TABLE IF NOT EXISTS `sneaking_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `student_id` bigint(20) NOT NULL COMMENT '学生id. 谁发的消息',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师id, 谁接收的消息',
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `content` text NOT NULL COMMENT '消息内容',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `class_id` (`class_id`),
  KEY `user_id` (`student_id`),
  KEY `teacher_user_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='小报告';

-- Dumping data for table dianxian.sneaking_message: ~0 rows (approximately)
/*!40000 ALTER TABLE `sneaking_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `sneaking_message` ENABLE KEYS */;

-- Dumping structure for table dianxian.sos_message
DROP TABLE IF EXISTS `sos_message`;
CREATE TABLE IF NOT EXISTS `sos_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `student_id` bigint(20) NOT NULL COMMENT '学生id. 谁发的消息',
  `school_id` bigint(20) NOT NULL COMMENT '学校id',
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `content` varchar(256) NOT NULL COMMENT '消息内容',
  `longitude` float DEFAULT NULL COMMENT '经度',
  `latitude` float DEFAULT NULL COMMENT '纬度',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `message_type` INT(10) NULL DEFAULT NULL COMMENT '消息类型SOS：1，SAFE：2',
  `biz_timestamp` BIGINT(20) NOT NULL COMMENT '时间戳',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='求救消息';

ALTER TABLE `sos_message`
	CHANGE COLUMN `message_type` `message_type` INT(11) NULL DEFAULT NULL COMMENT '消息类型. 1：SOS，2：SAFE，3：SYS_WARNING' AFTER `updated_at`,
	ADD COLUMN `status` INT NOT NULL DEFAULT '0' COMMENT '状态. 当message_type=3时表示，0：已关闭警告，1：未关闭警告' AFTER `biz_timestamp`;

ALTER TABLE `sos_message`
  ALTER `content` DROP DEFAULT;
ALTER TABLE `sos_message`
  CHANGE COLUMN `content` `content` VARCHAR(256) NULL COMMENT '消息内容' AFTER `class_id`;

-- Dumping data for table dianxian.sos_message: ~0 rows (approximately)
/*!40000 ALTER TABLE `sos_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `sos_message` ENABLE KEYS */;

-- Dumping structure for table dianxian.student
DROP TABLE IF EXISTS `student`;
CREATE TABLE IF NOT EXISTS `student` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `school_id` bigint(20) NOT NULL COMMENT '学校id. 未加入班级前为0',
  `enrol_year` int(11) NOT NULL COMMENT '入学年度',
  `class_number` int(11) NOT NULL COMMENT '几班',
  `class_id` bigint(20) NOT NULL COMMENT '班级id. 未加入班级前为0',
  `student_no` varchar(128) DEFAULT NULL COMMENT '学籍号',
  `code` varchar(8) NOT NULL COMMENT '内部编号',
  `description` text COMMENT '简介',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `student_no` (`student_no`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='学生';

-- Dumping data for table dianxian.student: ~0 rows (approximately)
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
/*!40000 ALTER TABLE `student` ENABLE KEYS */;

ALTER TABLE `student`
	ADD COLUMN `join_class_at` DATETIME NULL COMMENT '加入班级时间' AFTER `class_id`;

ALTER TABLE `student`
	ADD COLUMN `expected_at_home_time` DATETIME NULL DEFAULT NULL COMMENT '预计到家时间' AFTER `description`,
	ADD COLUMN `expected_at_home_time_buffer` TIME NULL DEFAULT NULL COMMENT '预计到家的缓冲时间' AFTER `expected_at_home_time`,
	ADD COLUMN `expected_at_home_time_update_at` DATETIME NULL DEFAULT NULL COMMENT '什么时候设置的预计到家时间' AFTER `expected_at_home_time_buffer`;

ALTER TABLE `student`
  ADD COLUMN `expected_at_home_timeout` DATETIME NULL DEFAULT NULL COMMENT '根据预计到家时间和缓冲时间计算出来的超时时间' AFTER `expected_at_home_time_buffer`;


-- Dumping structure for table dianxian.teacher
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE IF NOT EXISTS `teacher` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `school_id` bigint(20) NOT NULL COMMENT '学校id',
  `work_no` varchar(128) DEFAULT NULL COMMENT '工号',
  `description` text COMMENT '简介',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `school_id` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教师';

-- 新的小报告通知功能
ALTER TABLE `teacher`
	ADD COLUMN `view_sneaking_msg_at` DATETIME NULL DEFAULT NULL COMMENT '最后一次查看小报告的时间' AFTER `work_no`;


-- Dumping data for table dianxian.teacher: ~0 rows (approximately)
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;


-- Dumping structure for table dianxian.teacher_role
DROP TABLE IF EXISTS `teacher_role`;
CREATE TABLE IF NOT EXISTS `teacher_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint(20) NOT NULL COMMENT '教师id',
  `school_id` bigint(20) NOT NULL COMMENT '学校id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `enrol_year` int(11) NOT NULL COMMENT '代表哪一年入学的年级。如果是年级组长，则有值，否则0',
  `class_id` bigint(20) NOT NULL COMMENT '班级id. 如果是班主任，则有值',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `teacher_id` (`teacher_id`),
  KEY `teacher_id_role_id_class_id` (`teacher_id`,`role_id`,`class_id`),
  KEY `teacher_id_role_id_enrol_year` (`teacher_id`,`role_id`,`enrol_year`),
  KEY `teacher_id_school_id_role_id` (`teacher_id`,`school_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='老师-角色';

-- Dumping data for table dianxian.teacher_role: ~0 rows (approximately)
/*!40000 ALTER TABLE `teacher_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher_role` ENABLE KEYS */;


-- Dumping structure for table dianxian.user
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(128) NOT NULL COMMENT '登录帐号',
  `password` varchar(128) NOT NULL COMMENT '登录密码',
  `pwd_salt` varchar(8) NOT NULL COMMENT '加密用随机字符串',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `mobile_no` varchar(128) DEFAULT NULL COMMENT '手机号码',
  `status` int(8) NOT NULL COMMENT '1:启用，0禁用',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `mobile_no` (`mobile_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户账户表';

-- Dumping data for table dianxian.user: ~0 rows (approximately)
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;


-- Dumping structure for table dianxian.user_info
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE IF NOT EXISTS `user_info` (
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `type` int(11) NOT NULL COMMENT '类型: 1-老师, 2-学生, 3-家长',
  `name` varchar(128) DEFAULT NULL COMMENT '姓名',
  `avatar_file_id` varchar(128) DEFAULT NULL COMMENT '用户头像文件id',
  `avatar_url` varchar(256) DEFAULT NULL COMMENT '用户头像url',
  `im_user_id` varchar(64) DEFAULT NULL COMMENT '即时通讯用户id',
  `im_token` varchar(256) DEFAULT NULL COMMENT '即时通讯用户token口令',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `im_user_id_UNIQUE` (`im_user_id`),
  KEY `im_token` (`im_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';


-- Dumping data for table dianxian.user_info: ~0 rows (approximately)
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;


-- Dumping structure for table dianxian.user_role
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-角色';

-- Dumping data for table dianxian.user_role: ~0 rows (approximately)
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '群名',
  `type` int(11) NOT NULL COMMENT '群聊组类型, 1:班级家长群，2:班级学生群',
  `im_group_id` varchar(64) NOT NULL COMMENT '融云群聊组id',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='聊天群组表';


DROP TABLE IF EXISTS `im_user_group`;
CREATE TABLE `im_user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户_id',
  `im_user_id` varchar(64) NOT NULL COMMENT '即时通讯用户id',
  `group_id` bigint(20) NOT NULL COMMENT '群聊组id',
  `im_group_id` varchar(64) NOT NULL COMMENT '融云群聊组id',
  `role` int(11) NOT NULL COMMENT '角色: 1-老师, 2-学生, 3-家长',
  `is_admin` bit default false COMMENT '是否群管理员标志',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `member_UNIQUE` (`user_id`, `group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='聊天群组成员表';