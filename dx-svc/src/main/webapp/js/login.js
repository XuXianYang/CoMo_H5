/**
 * Created by XuWenHao on 8/2/2016.
 */
$('.ui.form').form({
    fields: {
        username : 'empty',
        password : 'empty'
    },
    onSuccess: function (event, fields) {
        $('.ui.form').removeClass('error');
        $('.ui.form').addClass('loading');
        $.ajax({
			type: 'POST',
			url: Global.getWebUrl('user/login'),
			data: $('.ui.form').form('get values'),
            dataType : 'json',
            success : function(data) {
                console.log('response: ');
                console.log(data);
                if (0 === data.code) {
                    if (0 === data.data.user.type) {
                        // 管理员
                        location.href = Global.getWebUrl('sysadmin/index');
                    } else if (1 === data.data.user.type) {
                        // 老师
                        location.href = Global.getWebUrl('teacher/index');
                    } else {
                        $('.ui.form').removeClass('loading');
                        $('.ui.form').addClass('error');
                        $('#errorMsg').text("只有老师可以登录");
                    }
                } else {
                    $('.ui.form').removeClass('loading');
                    $('.ui.form').addClass('error');
                    $('#errorMsg').text(data.message);
                }
            }
		});
    },
    onFailure: function (formErrors, fields) {
        //console.log("onFailure");
        //console.log(formErrors);
        //console.log(fields);
        //console.log($('.ui.form').form('get values'));
    }
});
$("#submit").click(function () {
    console.log("submit clicked");
    $('.ui.form').form('validate form');
});

