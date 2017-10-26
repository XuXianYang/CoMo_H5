$(function(){
    var login = {
        timer:null,
        init:function(){
            var that = this;
            that.bindEvent();
            that.yzmInit();
        },
        bindEvent:function(){
            var that =this;
            $("#J_username,#J_yzm,#J_passwordagain,#J_password").bind("focus",function(){
                $(this).parents(".col").next(".errormsg").text("");
            })
            $("#J_submit").bind("click",function(){
                if(that.validate()){
                    if($(this).hasClass("disabled")){
                        return false;
                    }
                    $(this).addClass("disabled");
                    $.ajax({
                        type: 'POST',
                        url: Global.getWebUrl('user/login'),
                        data: {
                            username:$.trim($("#J_username").val()),
                            password:$("#J_password").val()
                        },
                        dataType : 'json',
                        success : function(data) {
                            $("#J_submit").removeClass("disabled");
                            if (0 === data.code) {
                                if (0 === data.data.user.type) {
                                    // 管理员
                                    location.href = Global.getWebUrl('sysadmin/index');
                                } else if (1 === data.data.user.type) {
                                    // 老师
                                    location.href = Global.getWebUrl('teacher/index');
                                } else {

                                    alert("家长和学生请在手机端登录！");
                                }
                            } else {
                                alert(data.message);
                            }
                        },
                        error:function(){
                            $("#J_submit").removeClass("disabled");
                            alert("网络异常，请稍后再试！");
                        }
                    });

                }
            })

            $("#J_reg").bind("click",function(){
                if(that.validateReg()){



                    if($(this).hasClass("disabled")){
                        return false;
                    }
                    $(this).addClass("disabled");
                    var data = {
                        type:1,
                        username:$("#J_username").val(),
                        password:$("#J_password").val(),
                        mobileNo:$("#J_username").val(),
                        otp:$("#J_yzm").val()
                    }
                    $.ajax({
                        type: 'POST',
                        url: Global.getServiceUrl('user/register'),
                        data: data,
                        dataType : 'json',
                        success : function(data) {
                            $("#J_reg").removeClass("disabled");
                            if (0 === data.code) {
                                alert("注册成功");
                                location.href = Global.getWebUrl('user/login');
                            } else {
                                alert(data.message);
                            }
                        },
                        error:function(){
                            $("#J_reg").removeClass("disabled");
                            alert("网络异常，请稍后再试！");
                        }
                    });





                }
            })

        },
        validate:function(){
            var res = true;
            var username = $.trim($("#J_username").val());
            var password = $("#J_password").val();
            if(username==""){
                res = false;
                $("#J_username").parents(".col").next(".errormsg").text("请输入用户名！");
            }
            if(password==""){
                res = false;
                $("#J_password").parents(".col").next(".errormsg").text("请输入密码！");
            }
            return res;

        },
        validateReg:function(){
            var res = true;
            var username = $.trim($("#J_username").val());
            var password = $("#J_password").val();
            var yzm = $("#J_yzm").val();
            var passwordagain = $("#J_passwordagain").val();
            if(username==""){
                res = false;
                $("#J_username").parents(".col").next(".errormsg").text("请输入手机号码！");
            }
            if(yzm==""){
                res = false;
                $("#J_yzm").parents(".col").next(".errormsg").text("请输入手机验证码！");
            }
            if(password==""){
                res = false;
                $("#J_password").parents(".col").next(".errormsg").text("请输入密码！");
            }
            if(passwordagain==""){
                res = false;
                $("#J_passwordagain").parents(".col").next(".errormsg").text("请再输一次密码！");
            }
            if(passwordagain!="" && passwordagain!=password){
                res = false;
                $("#J_passwordagain").parents(".col").next(".errormsg").text("两次密码不同！");
            }
            return res;

        },

        yzmInit:function(){
            var that =this;
            function clearYzm() {
                $(".J_yzmbtn").text("获取验证码");
                $(".J_yzmbtn").removeClass('disabled');
                if (that.timer) {
                    clearInterval(that.timer);
                    that.timer = null;
                }
            }
            $("#J_username").bind("keyup",function(){
                var val = $(this).val();
                if(val.length==11){
                    clearYzm();
                }else{
                    $(".J_yzmbtn").addClass('disabled');
                }
            })

            $(".J_yzmbtn").bind("click",function(){
                if($(this).hasClass("disabled")){
                    return;
                }
                $(this).addClass("disabled");

                $.ajax({
                    url: Global.getServiceUrl('user/register/otp?mobileNo='+$("#J_username").val()),
                    dataType: 'json',
                    data:{},
                    type:"POST",
                    success: function(data){
                        var count = 60;
                        that.timer = setInterval(function(){
                            if(count <= 0){
                                clearYzm();
                            }else{
                                $(".J_yzmbtn").text(count-- +"秒后重发");
                            }
                        },1000);
                    },
                    error:function(){
                        clearYzm();
                    }
                });


            })



        }
    }

    login.init();
})