'use strict';


function joinClassResultHandler(data) {
	if (!data || !data.code) {
		showMessageBox(false, '加入班级', '成功加入班级', function() {
			// GOTO student list
			location.href = (Global.getWebUrl('teacher') + location.search);
		})
	} else {
		commonErrorResultHandler(data, '加入班级');
	}
}

$(function() {
	var $classSeletion = $('#class-selection');
	var $enrollYearSeletion = $('#enroll-year-selection');
	var $courseSeletion = $('#course-selection');
	var $btnJoinClass = $('#btn-join-class');

	$.ajaxSettings.traditional = true;

	$('.btn-selector-group')
		.btnSelector()
		.on('valueChanged', function() {
			var selectedClass = $classSeletion.data('value');
			var selectedEnrollYear = $enrollYearSeletion.data('value');
			var selectedCourse = $courseSeletion.data('value');

			if (selectedClass && selectedEnrollYear && selectedCourse) {
				$btnJoinClass.removeAttr('disabled').removeClass('disabled');
			} else {
				$btnJoinClass.attr('disabled', 'disabled').addClass('disabled');
			}
		});

	$btnJoinClass.click(function() {
		var url = Global.getServiceUrl('teacher/class/join');
		var selectedClass = $classSeletion.data('value');
		var selectedEnrollYear = $enrollYearSeletion.data('value');
		var selectedCourse = $courseSeletion.data('value');

		$(document.body).addClass('busy');
		$.post(url, {
			enrolYear: selectedEnrollYear,
			classNumber: selectedClass,
			courseIds: selectedCourse
		})
		.then(function(data) {
			setTimeout(function() {
				$(document.body).removeClass('busy');
				joinClassResultHandler(data);
			}, 500)
	  })
		.fail(function() {
				setTimeout(function() {
					$(document.body).removeClass('busy');
					showMessageBox(true, '加入班级', '请求失败，请稍后重试')
				}, 500)
		});

	})
});
