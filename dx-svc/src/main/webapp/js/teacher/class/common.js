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
		4006: '您的操作太频繁，请稍后再试'
	}
	showMessageBox(true, action, messages[data.code] || '请求失败，请稍后重试');
}

$(function() {
	$('.class-list.btn-selector-group')
		.on('click', 'a.btn-selector', function(jEvent) {
			var classId = $(this).data('value');
			location.search = '?classId=' + classId;
		});
});

