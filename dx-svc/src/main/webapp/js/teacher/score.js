// Dropzone.options.coursewareDropzone = {
//   maxFiles: 1
// };
'use strict';


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

function removeQuizResultHandler(data) {
	if (!data || !data.code) {
		showMessageBox(false, '移除成绩', '已成功移除成绩', refreshPage)
	} else {
		commonErrorResultHandler(data, '移除成绩');
	}
}

function crearteScoreResultHandler(data) {
	if (!data || !data.code) {
		showMessageBox(false, '发布成绩', '已成功发布成绩', function redirectToListPage() {
			location.href = Global.getWebUrl('teacher/score/list');
		})
	} else {
		commonErrorResultHandler(data, '发布成绩');
	}
}

function refreshPage() {
  location.reload();
}

function getParentsString(parents) {
	if (!parents || !parents.length) return null;

	var i, p, pp = '';
	for (i = 0; i < parents.length; i++) {
		p = parents[i];
		if (p.relation == 1) {
			pp += '（父）';
		} else {
			pp += '（母）';
		}

		if (p.realName) {
			pp += ' ' + p.realName;
		}

		if (p.mobileNo) {
			pp += ' ' + p.mobileNo;
		}
	}
	return pp;
}

function mapScore(scores) {
	var i, s, mappedScore = {};

	if (!scores) return mappedScore;
	for (i = 0; i < scores.length; i++) {
		s = scores[i]
		mappedScore[s.studentId] = s.score;
	}
	return mappedScore;
}


$(function() {
	function listScoreTable() {
		var studentsJson = $('#students-json-input').html();
		var scoresJson = $('#scores-json-input').html();
		var students = null;
		var scores = null;
		var hot = null;
		var mappedArr = [];
		var mappedScore = {};
		var i, j, s;
		var canEdit = $('#can-edit-input').val() === 'false';

		if (studentsJson) {
			students = $.parseJSON(studentsJson);
			if (scoresJson) {
				scores = $.parseJSON(scoresJson);
				mappedScore = mapScore(scores);
			}

			for (i = 0; i < students.length; i++) {
				s = students[i];

				mappedArr.push({
					id: s.studentId,
					name: s.realName,
					phoneNumber: s.mobileNo,
					parents: getParentsString(s.parents),
					score: mappedScore[s.studentId]
				});
			}

			// Set score-list height
			var scoreList = document.getElementById('score-list');
			scoreTable = new Handsontable(scoreList, {
				data: mappedArr,
				minSpareRows: 1,
				maxRows: mappedArr.length,
				colHeaders: ['学生ID', '姓名', '手机号码', '家长', '成绩'],
				rowHeaders: true,
				columns: [
					{data: 'id', readOnly: true},
					{data: 'name', readOnly: true},
					{data: 'phoneNumber', readOnly: true},
					{data: 'parents', readOnly: true},
					{data: 'score', readOnly: canEdit}
				],
				currentRowClassName: 'currentRow',
				currentColClassName: 'currentCol',
				contextMenu: false
			});

			var h = scoreTable.view.wt.wtTable.TABLE.clientHeight + 20;
			scoreList.style.height = h + 'px';
		}
	}

	var scoreTable = null;
	if ($('#students-json-input').length) {
		listScoreTable();
	}

	$('.btn-publish-score').click(function() {
		var classId = $('#class-input').val();
		var quizId = $('#quiz-input').val();
		var scores = scoreTable.getData();
		var mapScores = {};
		var i, s;

		for (i = 0; i < scores.length; i++) {
			s = scores[i];
			mapScores[s[0]] = s[4];
		}

		$(document.body).addClass('busy');
		$.post(Global.getServiceUrl('teacher/class/quiz/score/set'), {
			classId: classId,
			quizId: quizId,
			studentScores: JSON.stringify(mapScores)
		})
		.then(function(data) {
			setTimeout(function() {
				$(document.body).removeClass('busy');
				crearteScoreResultHandler(data);
			}, 500);
	  })
		.fail(function() {
				setTimeout(function() {
					$(document.body).removeClass('busy');
					showMessageBox(true, '发布成绩', '请求失败，请稍后重试');
				}, 500);
		});

	});
});
