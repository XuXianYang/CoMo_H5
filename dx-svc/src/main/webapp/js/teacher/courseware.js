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

function removeCoursewareResultHandler(data) {
  if (!data || !data.code) {
    showMessageBox(false, '移除课件', '已成功移除课件', refreshPage)
  } else {
    commonErrorResultHandler(data, '移除课件');
  }
}

function uploadCoursewareResultHandler(data) {
  if (!data || !data.code) {
    showMessageBox(false, '上传课件', '已成功上传课件')
  } else {
    commonErrorResultHandler(data, '上传课件');
  }
}

function crearteCoursewareResultHandler(data) {
  if (!data || !data.code) {
    showMessageBox(false, '发布课件', '已成功发布课件', function redirectToListPage() {
      location.href = Global.getWebUrl('teacher/courseware/list');
    })
  } else {
    commonErrorResultHandler(data, '发布课件');
  }
}

function refreshPage() {
  location.reload();
}

$(function() {
  // Buttons to remove courseware
  $('.btn-remove-courseware').click(function() {
    var $this = $(this);
    var id = $this.data("id");
    var url = Global.getServiceUrl('teacher/courseware/remove');
    $(document.body).addClass('busy');
    $.post(url, {id: id})
    .then(function(data) {
      setTimeout(function() {
        $(document.body).removeClass('busy');
        removeCoursewareResultHandler(data);
      }, 500)
    })
    .fail(function() {
        setTimeout(function() {
          $(document.body).removeClass('busy');
          showMessageBox(true, '移除课件', '请求失败，请稍后重试');
        }, 500)
    });
  });

	$('#input-courseware').click(function() {
		$('.courseware-fileupload:file').click();
	});

  // files to upload file
  $('.courseware-fileupload:file').fileupload({
      url: Global.getServiceUrl('teacher/course/material/upload'),
      replaceFileInput: false,
      dataType: 'json',
    error: function(){
      showMessageBox(true, '错误提示', '上传课件失败，请稍后重试');
    },
      done: function (e, data) {
        if (data.result && data.result.code === 0) {
          $('#input-courseware').val(data.files[0].name);
          var title = $('#title-input').val();
          if (!title) {
            $('#title-input').val(data.files[0].name);
          }
          $('#input-courseware').data('resourceId', data.result.data);
        }
        uploadCoursewareResultHandler(data.result);
      }
  });

  $('.btn-publish-courseware').click(function() {
    var reciever = $('#reciever-selection').data('value');
    var type = $('#type-selection').data('value');
    var title = $('#title-input').val() || $('#input-courseware').val();
    var content = $('#content-input').val();
    var resourceId = $('#input-courseware').data('resourceId');
    var isUpdate = $(this).data('type') === 'update';

    if(!resourceId) {
      showMessageBox(true, '错误提示', '您还没有上传课件，请上传课件');
      return;
    }

    $(document.body).addClass('busy');
    var url = Global.getServiceUrl('teacher/course/material/create');
    var params = {
      classId: reciever,
      resourceFileId: resourceId,
      courseId: 1, // TODO: to be real
      type: type,
      name: title,
      description: content
    };
    if (isUpdate) {
      url = Global.getServiceUrl('teacher/course/material/update');
      params.id = $('#courseware-id').val();
    }

    $.post(url, params)
      .then(function(data) {
        setTimeout(function() {
          $(document.body).removeClass('busy');
          crearteCoursewareResultHandler(data);
        }, 500);
      })
      .fail(function() {
          setTimeout(function() {
            $(document.body).removeClass('busy');
            showMessageBox(true, '移除课件', '请求失败，请稍后重试');
          }, 500);
      });
  });
});
