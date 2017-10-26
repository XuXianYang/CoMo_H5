$(function(){
  var dx_prepare = {
    timer:null,
    init:function(){
      var that = this;
      that.yzmInit();
      that.bindEvent();
      $(".content")[0].scrollTop = $(".content")[0].scrollHeight;
    },
    bindEvent:function(){
      var that = this;
      $("#J_register").bind("click",function(){
        if(that.validateReg()){
          that.submitRegister();
        }
      })

      $("#J_OK").bind("click",function(){
        if(that.validateFwd()){
          that.submitFwd();
        }
      })

      $("#J_login").bind("click",function(){
        that.submitLogin();

      })
    },
    submitLogin:function(){
      var tel = $("#J_loginTel").val();
      var password = $("#J_loginPassword").val();
      if(tel==""){
        $.toast("请填写手机号",3000);
        return false;
      }
      if(password==""){
        $.toast("请输入密码",3000);
        return false;
      }
      var data = {
        username:tel,
        password:password
      };
      $.ajax({
        url: dxutil.baseUrl+"/dx-svc/service/user/login",
        dataType: 'json',
        type:"POST",
        data:data,
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            $.fn.cookie(dxutil.sid, data.data.sid, { expires: 7,path:"/" });
            if(data.data.user.type==2){
              if(data.data.user.studentInfo.classId){
                location.href="home.html";
              }else{
                location.href="join.html";
              }
            }else{
              $.toast("该客户端只支持学生登录!");
            }

          }else{
            $.toast(data.message);
          }

        },
        error:function(){
          $.hideIndicator();
        }
      });
    },

    validateReg:function(){
      var errorMsg = "";
      var type = $("#J_role").val();
      var tel = $("#J_tel").val();
      var otp = $("#J_otp").val();
      var password = $("#password").val();
      if(type==0){
        errorMsg = "请选择角色！";
      }else if(tel.length<11){
        errorMsg = "请填写正确的手机号！";
      }else if(otp.length<6){
        errorMsg = "请填写正确的验证码！";
      }else if(password==""){
        errorMsg = "密码不能为空！";
      }
      if(errorMsg){
        $.toast(errorMsg,3000);
        return false;
      }else{
        return true;
      }
    },

    validateFwd:function(){
      var errorMsg = "";
      var tel = $("#J_ftel").val();
      var otp = $("#J_otp").val();
      var password = $("#J_fpassword").val();
      if(tel.length<11){
        errorMsg = "请填写正确的手机号！";
      }else if(otp.length<6){
        errorMsg = "请填写正确的验证码！";
      }else if(password==""){
        errorMsg = "新密码不能为空！";
      }
      if(errorMsg){
        $.toast(errorMsg,3000);
        return false;
      }else{
        return true;
      }
    },

    submitFwd:function(){
      var data = {
        mobileNo:$("#J_ftel").val(),
        password:$("#J_fpassword").val(),
        otp:$("#J_otp").val()
      }

      $.ajax({
        url: dxutil.baseUrl+"/dx-svc/service/user/register/resetPassword",
        dataType: 'json',
        type:"POST",
        data:data,
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            $.toast("密码找回成功！");
            setTimeout(function () {
              $.router.load('index.html#login', true); //
            }, 2000);

          }else{
            $.toast(data.message);
          }
        },
        error:function(){
          $.hideIndicator();
        }
      });


    },
    submitRegister:function(){
      var data = {
        type:$("#J_role").val(),
        username:$("#J_tel").val(),
        password:$("#password").val(),
        mobileNo:$("#J_tel").val(),
        otp:$("#J_otp").val()
      }
      $.ajax({
        url: dxutil.baseUrl+"/dx-svc/service/user/register",
        dataType: 'json',
        type:"POST",
        data:data,
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            $.toast("注册成功");
            setTimeout(function () {
              $.router.load("#login");  //加载内联页面
            }, 2000);

          }else{
            $.toast(data.message);
          }

        },
        error:function(){
          $.hideIndicator();
        }
      });

    },

    yzmInit:function(){
      var that = this;
      function clearYzm(){
        $(".J_yzmbtn").text("获取验证码");
        $(".J_yzmbtn").removeClass('disabled');
        if(that.timer){
          clearInterval(that.timer);
          that.timer = null;
        }
      }
      $(".J_tel").bind("keyup",function(){
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
        var url = dxutil.baseUrl+"/dx-svc/service/user/register/otp?mobileNo="+$(".J_tel").val();
        if($("#J_pageforfwd").val()=="1"){
          url = dxutil.baseUrl+"/dx-svc/service/user/findPassword/otp?mobileNo="+$(".J_tel").val();
        }
        $.ajax({
          url: url,
          dataType: 'json',
          type:"POST",
          success: function(data){
            $.hideIndicator();
            var count = 60;
            that.timer = setInterval(function(){
              if(count <= 0){
                clearYzm();
              }else{
                $(".J_yzmbtn").text(count-- +"秒后重新获取");
              }
            },1000);
          },
          error:function(){
            $.hideIndicator();
            clearYzm();
          }
        });


      })
    }
  }

  $(".swiper-container").swiper({
    pagination: '.swiper-pagination'
  });

  dx_prepare.init();
})
