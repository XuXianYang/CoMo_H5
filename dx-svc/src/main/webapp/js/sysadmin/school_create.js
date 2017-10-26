/**
 * Created by XuWenHao on 8/2/2016.
 */
$(function () {
    $('select.dropdown').dropdown();
    $('.ui.form').form({
        fields: {
            name : 'empty',
            category : 'empty'
        },
        onSuccess: function (event, fields) {
            $('.ui.form').removeClass('error');
            $('.ui.form').addClass('loading');
            $.ajax({
                type: 'POST',
                url: Global.getServiceUrl('admin/school/create'),
                data: $('.ui.form').form('get values'),
                dataType : 'json',
                success : function(data) {
                    console.log('response: ');
                    console.log(data);
                    if (0 === data.code) {
                        location.href = Global.getWebUrl("sysadmin/school/list");
                    } else {
                        $('.ui.form').removeClass('loading');
                        $('.ui.form').addClass('error');
                        $('.ui.form').empty();
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
    $("#submitBtn").click(function () {
        $('.ui.form').form('validate form');
    });
});
