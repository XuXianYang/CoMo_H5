$(function(){
  var dx_setting = {

    init:function(){
      var that = this;
      that.bindEvent();
    },

    bindEvent:function(){
      $(".J_modifyPsd").bind("click",function(){

        var oldpsd = $(".J_oldpsd").val();
        if(oldpsd==""){
          $.toast("请输入旧密码",3000);
          return false;
        }
        var newpsd = $(".J_newpsd").val();
        if(newpsd==""){
          $.toast("请输入新密码",3000);
          return false;
        }


        var data = {oldPassword:oldpsd,newPassword:newpsd};
        $.ajax({
          url:dxutil.baseUrl+"/dx-svc/service/user/register/modifyPassword",
          dataType: 'json',
          type:"POST",
          data:dxutil.addSid(data),
          success: function(data){
            $.hideIndicator();
            if(data.code=="0"){
              $.router.back()
            }else if(data.code=="4003"){
              dxutil.noLogin();
            }else{
              $.toast(data.message);
            }

          },
          error:function(){
            $.hideIndicator();
          }
        });


      });
    }
  }
  dx_setting.init();
})
