$(function(){
    var dx_homework = {
        init:function(){
            var that =this;
            that.getCourse(true);
            that.bindEvent();

        },
        bindEvent:function(){
            var that =this;
            $(".J_changeClass").bind("change",function(){
                var classid = $(this).val();
                location.href = "homework?classid="+classid;
            })
            $("#J_class").bind("change",function(){
                that.getCourse();
            })
            $("#J_course,#J_title,#J_content").bind("click",function(){
                $(this).parents(".f-con").find(".errormsg").text("");
            })
            $("#J_pubhomework").bind("click",function(){
                var _t = $(this);
                if(that.validateForm()){
                    if(_t.hasClass("disabled")){
                        return false;
                    }
                    _t.addClass("disabled");
                    var data = {
                        classId:$("#J_class").val(),
                        courseId:$("#J_course").val(),
                        studyDate:$("#J_date").val(),
                        name:$("#J_title").val(),
                        description:$("#J_content").val()
                    };
                    var ajaxurl = "/dx-svc/service/teacher/homework/create";
                    var homeid = $("#homeId").val();
                    var succmsg = "作业发布成功！";
                    if(homeid){
                        data.id = homeid;
                        succmsg ="作业更新成功！"
                        ajaxurl = "/dx-svc/service/teacher/homework/update";
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
                                    location.href="/dx-svc/web/teacher/homework";
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
        getCourse:function(isEdit){

            var classId = $("#J_class").val();
            if(classId) {
                $("#J_course").html('<option value="0">请选择</option>');
                $.ajax({
                    url: "/dx-svc/service/teacher/class/course/list",
                    dataType: 'json',
                    type: "GET",
                    data: {
                        classId: $("#J_class").val()
                    },
                    success: function (data) {
                        if (data.code == "0") {
                            var html = [];
                            var list = data.data;
                            html.push('<option value="0">请选择</option>');
                            for (var i = 0; i < list.length; i++) {
                                html.push('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                            }
                            $("#J_course").html(html.join(''));
                            if(isEdit){
                                $("#J_course").val($("#homecourseId").val());
                            }
                        }
                    }
                });
            }else{

            }
        },
        validateForm:function(){
            var validate = true;
            var courseid = $("#J_course").val();
            var title = $("#J_title").val();
            var des = $("#J_content").val();
            if(courseid=="0"){
                validate = false;
                $("#J_course").parents(".f-con").find(".errormsg").text("请选择课程！");
            }
            if(title==""){
                validate = false;
                $("#J_title").parents(".f-con").find(".errormsg").text("请填写标题！");
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