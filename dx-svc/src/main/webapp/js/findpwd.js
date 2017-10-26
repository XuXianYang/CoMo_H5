$(function(){
    var findpwd = {
        timer:null,
        init:function(){
            var that = this;
            that.bindEvent();
            that.yzmInit();
        },
        bindEvent:function(){
            var that =this;
            $("#J_usernamef,#J_yzmf,#J_passwordf").bind("focus",function(){
                $(this).parents(".col").next(".errormsg").text("");
            })

            $("#J_submit").bind("click",function(){
                if(that.validateReg()){



                    if($(this).hasClass("disabled")){
                        return false;
                    }
                    $(this).addClass("disabled");
                    var data = {
                        mobileNo:$("#J_usernamef").val(),
                        password:$("#J_passwordf").val(),
                        otp:$("#J_yzmf").val()
                    }
                    $.ajax({
                        type: 'POST',
                        url: Global.getServiceUrl('user/register/resetPassword'),
                        data: data,
                        dataType : 'json',
                        success : function(data) {
                            $("#J_submit").removeClass("disabled");
                            if (0 === data.code) {
                                alert("密码找回成功，请去登录");
                                location.href = Global.getWebUrl('user/login');
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

        },
        validateReg:function(){
            var res = true;
            var username = $.trim($("#J_usernamef").val());
            var password = $("#J_passwordf").val();
            var yzm = $("#J_yzmf").val();
            if(username==""){
                res = false;
                $("#J_usernamef").parents(".col").next(".errormsg").text("请输入手机号码！");
            }
            if(yzm==""){
                res = false;
                $("#J_yzmf").parents(".col").next(".errormsg").text("请输入手机验证码！");
            }
            if(password==""){
                res = false;
                $("#J_passwordf").parents(".col").next(".errormsg").text("请输入密码！");
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
            $("#J_usernamef").bind("keyup",function(){
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
                    url: Global.getServiceUrl('user/findPassword/otp?mobileNo='+$("#J_usernamef").val()),
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

    findpwd.init();
})