

INSERT INTO `role` (`id`, `name`, `description`, `type`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
  (1, '系统管理员', '系统管理员', 0, NULL, NULL, now(), NULL),
  (2, '学校管理员', '学校管理员', 0, NULL, NULL, now(), NULL),
  (3, '教导主任', '教导主任', 0, NULL, NULL, now(), NULL),
  (4, '年级组长', '年级组长', 0, NULL, NULL, now(), NULL),
  (5, '班主任', '班主任', 0, NULL, NULL, now(), NULL),
	(6, '普通教师', '普通教师', 0, NULL, NULL, now(), NULL),
	(7, '家长', '家长', 0, NULL, NULL, now(), NULL),
	(8, '学生', '学生', 0, NULL, NULL, now(), NULL);

INSERT INTO `permission` (`id`, `name`, `description`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(1, 'createSchool', '', NULL, NULL, now(), NULL),
	(2, 'createSchoolAdmin', '', NULL, NULL, now(), NULL),
	(3, 'setClassCourseSchedule', NULL, NULL, NULL, now(), NULL),
	(4, 'createSchoolAnnouncement', NULL, NULL, NULL, now(), NULL),
	(5, 'createClassAnnouncement', NULL, NULL, NULL, now(), NULL),
	(6, 'getSchoolInfo', NULL, NULL, NULL, now(), NULL);

INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(1, 1, 1, NULL, NULL, now(), NULL),
	(2, 1, 2, NULL, NULL, now(), NULL),
	(3, 2, 4, NULL, NULL, now(), NULL),
	(4, 5, 3, NULL, NULL, now(), NULL),
	(5, 5, 5, NULL, NULL, now(), NULL),
	(6, 1, 6, NULL, NULL, now(), NULL),
	(7, 2, 6, NULL, NULL, now(), NULL);

/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` (`id`, `name`, `description`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(1, '语文', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(2, '数学', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(3, '外语', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(4, '思想品德', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(5, '科学', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(6, '物理', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(7, '化学', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(8, '生命科学', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(9, '地理', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(10, '历史', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(11, '社会', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(12, '音乐', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(13, '美术', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(14, '艺术', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(15, '体育与健身', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(16, '劳动技术', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(17, '信息科技', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL),
	(18, '探究型课程', NULL, NULL, NULL, '2016-06-20 16:01:30', NULL);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;

INSERT INTO `user` (`id`, `username`, `password`, `pwd_salt`, `email`, `mobile_no`, `status`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(1, 'sys_admin', 'd46981e9d92f300af8a3f6ccec03a141', 'IIKu27ms', NULL, '13000000000', 1, NULL, NULL, now(), NULL);
INSERT INTO `user_info` (`user_id`, `name`, `type`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(1, 'sys_admin', 0, NULL, NULL, now(), NULL);
INSERT INTO `user_role` (`id`, `user_id`, `role_id`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(1, 1, 1, NULL, NULL, now(), NULL);

-- 删除学生功能
INSERT INTO `permission` (`id`, `name`, `description`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(7, 'removeStudentFromClass', '', NULL, NULL, now(), NULL);
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(8, 5, 7, NULL, NULL, now(), NULL);

-- 创建考试功能
INSERT INTO `permission` (`id`, `name`, `description`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(8, 'createQuiz', '', NULL, NULL, now(), NULL);
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`, `created_by`, `updated_by`, `created_at`, `updated_at`) VALUES
	(9, 2, 8, NULL, NULL, now(), NULL);
