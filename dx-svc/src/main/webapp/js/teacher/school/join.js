'use strict';

var currentPage = window.currentPage = 'my-class';

function showMessageBox(hasError, title, messageContent, messageContentAddons, afterAction) {
	var $messageBox = $('#message-box');
	var $title = $messageBox.find('#message-box-title');
	var $message = $messageBox.find('.message');
	if (!messageContentAddons || $.isFunction(messageContentAddons)) {
		$title.html(title);
		$message.html('<p id="message-content-after">' + messageContent + '</p>');
	} else {
		$title.html(title);
		$message.html([
			'<span class="message-content">', messageContent, '</span>',
			'<p>', messageContentAddons, '</p>'
		].join(''));
	}
	if (hasError) {
		$messageBox.addClass('has-error');
	} else {
		$messageBox.removeClass('has-error');
	}
	$messageBox.modal('show');

	if (!afterAction) {
		afterAction = messageContentAddons;
	}
	if ($.isFunction(afterAction)) {
		$messageBox.one('hidden.bs.modal', afterAction);
	}
}

function commonErrorResultHandler(data, action) {
	// public static final String SUCCESS_MSG = "success";
	// public static final int INTERNAL_ERROR = 5000;
	// public static final int DATA_ERROR = 5001;
	// public static final int REQUEST_FAIL = 4000;
	// public static final int UN_AUTHORIZED = 4001;
	// public static final int INVALID_PARAM = 4002;
	// public static final int NOT_LOGIN = 4003;
	// public static final int NOT_FOUND = 4004;
	// public static final int VALID_FAIL = 4005;
	// public static final int FREQUENCY_LIMIT = 4006;
	var messages = {
		5000: '系统内部错误，请联系系统维护人员',
		5001: '数据错误',
		4000: '请求失败，请稍后重试',
		4001: '没有该操作的权限',
		4002: '参数错误',
		4003: '登录已失效，请重新登录',
		4004: '找不到该服务',
		4005: '参数错误',
		4006: '您的操作太频繁，请稍后再试',
		9201: '已经加入过学校'
	}
	showMessageBox(true, action, messages[data.code] || '请求失败，请稍后重试');
}


function joinSchoolResultHandler(data) {
	if (!data || !data.code) {
		showMessageBox(false, '加入学校', '成功加入学校', function() {
			// GOTO student list
			location.href = (Global.getWebUrl('teacher') + location.search);
		})
	} else {
		commonErrorResultHandler(data, '加入学校');
	}
}

$(function() {
	var $schoolSeletion = $('#school-selection');
	var $btnJoinSchool = $('#btn-join-school')

	$('.btn-selector-group')
		.btnSelector()
		.on('valueChanged', function() {
			var selectedSchool = $schoolSeletion.data('value');

			if (selectedSchool) {
				$btnJoinSchool.removeAttr('disabled').removeClass('disabled');
			} else {
				$btnJoinSchool.attr('disabled', 'disabled').addClass('disabled');
			}
		});

	$btnJoinSchool.click(function() {
		var url = Global.getServiceUrl('teacher/school/join');
		var selectedSchool = $schoolSeletion.data('value');
		var values = selectedSchool.split(':');

		$(document.body).addClass('busy');
		$.post(url, {
		    id: values[0],
			code: values[1]
		})
		.then(function(data) {
			setTimeout(function() {
				$(document.body).removeClass('busy');
				joinSchoolResultHandler(data);
			}, 500)
	  })
		.fail(function() {
				setTimeout(function() {
					$(document.body).removeClass('busy');
					showMessageBox(true, '加入学校', '请求失败，请稍后重试')
				}, 500)
		});

	})
});
