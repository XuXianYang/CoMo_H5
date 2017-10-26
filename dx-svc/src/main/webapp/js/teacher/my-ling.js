$(function(){
    var dxling = {
        init:function(){
            var that = this;
            that.bindEvent();


        },
        bindEvent:function() {
            var that = this;
            laydate({
                elem: '#J_start',
                format: 'YYYY-MM-DD hh:mm:ss',
                istime: true
            });
            laydate({
                elem: '#J_end',
                format: 'YYYY-MM-DD hh:mm:ss',
                istime: true
            });
            $("#J_name,#J_start,#J_end").bind("click",function(){
                $(this).parents(".f-con").find(".errormsg").text("");
            })

            $("#J_category").bind("change", function () {
                var v = $(this).val();
                if (v == 3) {
                    $("#J_selMonth").show();
                } else {
                    $("#J_selMonth").hide();
                }
            })

            $("#J_pubquiz").bind("click",function(){
                var _t = $(this);
                if(that.validateForm()){
                    if(_t.hasClass("disabled")){
                        return false;
                    }
                    _t.addClass("disabled");
                    var data = {
                        name:$("#J_name").val(),
                        courseId:$("#J_course").val(),
                        enrolYear:$("#J_year").val(),
                        startTime:$("#J_start").val(),
                        endTime:$("#J_end").val(),
                        category:$("#J_category").val(),
                        description:$("#J_content").val(),
                    };
                    if(data.category==3){
                        data.studyMonth = $("#J_month").val();
                    }
                    var ajaxurl = "/dx-svc/service/teacher/quiz/create";
                    var quizid = $("#quizId").val();
                    var succmsg = "考试发布成功！";
                    if(quizid){
                        data.id = quizid;
                        succmsg ="考试更新成功！"
                        ajaxurl = "/dx-svc/service/teacher/quiz/update";
                    }
                    $.ajax({
                        url: ajaxurl,
                        dataType: 'json',
                        type: "POST",
                        data: data,
                        success: function (data) {
                            _t.removeClass("disabled");
                            if(data.code=="0"){
                                $("#J_name").val("");
                                global_dx.message("提示",succmsg,1,function(){
                                    location.href="/dx-svc/web/teacher/ling";
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
            var name = $("#J_name").val();
            var start = $("#J_start").val();
            var end = $("#J_end").val();
            var des = $("#J_content").val();
            if(name==""){
                validate = false;
                $("#J_name").parents(".f-con").find(".errormsg").text("请填写考试名称！");
            }
            if(start==""){
                validate = false;
                $("#J_start").parents(".f-con").find(".errormsg").text("请选择考试开始时间！");
            }
            if(end==""){
                validate = false;
                $("#J_end").parents(".f-con").find(".errormsg").text("请选择考试结束时间！");
            }
            if(start>=end){
                validate = false;
                $("#J_end").parents(".f-con").find(".errormsg").text("开始时间不能晚于结束时间！");
            }
            if(des.length>200){
                validate = false;
                $("#J_content").parents(".f-con").find(".errormsg").text("内容长度不能超过200字！");
            }
            return validate;
        }
    }
    dxling.init();
})