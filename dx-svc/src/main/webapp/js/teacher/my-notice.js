$(function(){
    var dx_homework = {
        init:function(){
            var that =this;
            that.bindEvent();

        },
        bindEvent:function(){
            var that =this;
            $(".J_changeClass").bind("change",function(){
                var classid = $(this).val();
                location.href = "notice?classid="+classid;
            })
            $("#J_class,#J_title,#J_content").bind("click",function(){
                $(this).parents(".f-con").find(".errormsg").text("");
            })
            $("#J_pubnotice").bind("click",function(){
                var _t = $(this);
                if(that.validateForm()){
                    if(_t.hasClass("disabled")){
                        return false;
                    }
                    _t.addClass("disabled");
                    var data = {
                        type:3,
                        classId:$("#J_class").val(),
                        title:$("#J_title").val(),
                        content:$("#J_content").val()
                    };
                    var ajaxurl = "/dx-svc/service/teacher/announcement/create";
                    var noticeId = $("#noticeId").val();
                    var succmsg = "公告发布成功！";
                    if(noticeId){
                        data.id = noticeId;
                        succmsg ="公告更新成功！"
                        ajaxurl = "/dx-svc/service/teacher/announcement/update";
                    }
                    $.ajax({
                        url: ajaxurl,
                        dataType: 'json',
                        type: "POST",
                        data: data,
                        success: function (data) {
                            _t.removeClass("disabled");
                            if(data.code=="0"){
                                $("#J_title").val("");
                                global_dx.message("提示",succmsg,1,function(){
                                    location.href="/dx-svc/web/teacher/notice";
                                })
                            }
                        },
                        error:function(){
                            _t.removeClass("disabled");
                            alert("系统异常，稍后再试！");
                        }
                    });
                }else{
                    return;
                }
            })

        },
        validateForm:function(){
            var validate = true;
            var title = $("#J_title").val();
            var des = $.trim($("#J_content").val());
            if(title==""){
                validate = false;
                $("#J_title").parents(".f-con").find(".errormsg").text("请填写标题！");
            }
            if(des==""){
                validate = false;
                $("#J_content").parents(".f-con").find(".errormsg").text("内容不能为空！");
            }
            if(des.length>200){
                validate = false;
                $("#J_content").parents(".f-con").find(".errormsg").text("内容长度不能超过200字！");
            }
            return validate;
        }
    }
    dx_homework.init();

})
