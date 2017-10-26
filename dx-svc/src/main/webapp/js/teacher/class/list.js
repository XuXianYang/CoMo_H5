'use strict';
function removeStudentFromClassResultHandler(data) {
	if (!data || !data.code) {
		showMessageBox(false, '移除学生', '已成功移除学生', refreshPage)
	} else {
		commonErrorResultHandler(data, '移除学生');
	}
}

function refreshPage() {
  location.reload();
}
$(function() {
  $('.btn-selector-group').btnSelector();

  $('.student-list .btn-remove-student-from-class').click(function() {
	  var $this = $(this);
	  var classId = $this.data("classId");
	  var studentId = $this.data("studentId");
	  var url = Global.getServiceUrl('teacher/class/student/remove');
		$(document.body).addClass('busy');
	  $.post(url, {
		  classId: classId,
		  studentId: studentId
	  })
		.then(function(data) {
			setTimeout(function() {
				$(document.body).removeClass('busy');
				removeStudentFromClassResultHandler(data);
			}, 500)
	  })
		.fail(function() {
				setTimeout(function() {
					$(document.body).removeClass('busy');
					showMessageBox(true, '移除学生', '请求失败，请稍后重试')
				}, 500)
		});
	});
});
