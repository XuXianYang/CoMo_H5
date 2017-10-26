$(function(){
  var dx_setting = {

    init:function(){
      var that = this;
      that.getData();
      that.bindEvent();
    },
    getData:function(){
      var that =this;

      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/student/sos/msg/getWarningTime",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid({}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            if(data.data&&data.data.expectTime){
                $(".J_expectTime").val(data.data.expectTime.split(":").slice(0,2).join(": "));
            }
            if(data.data&&data.data.timeOut){
              var timeouts = parseInt(data.data.timeOut);
              var mintus = parseInt(timeouts/60);
              $(".J_timeout").val(mintus+"分钟");
            }

            that.setDatePick();

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

      $(".J_safeBtn").bind("click",function(){
        var expectTime = $(".J_expectTime").val();
        var timeOut = $(".J_timeout").val();
        if(expectTime==""){
          $.toast("请选择预期到家时间",3000);
          return false;
        }
        expectTime = expectTime.split(": ").join(":")+":00";
        timeOut = parseInt(timeOut.split("分钟")[0])*60;
        var data = {expectTime:expectTime,timeout:timeOut};
        $.ajax({
          url:dxutil.baseUrl+"/dx-svc/service/student/sos/msg/warningConfig",
          dataType: 'json',
          type:"POST",
          data:dxutil.addSid(data),
          success: function(data){
            $.hideIndicator();
            if(data.code=="0"){
              $.toast("设置成功！",3000);
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
      })




    }
    ,
    setDatePick:function(){
      $(".J_expectTime").picker({
        toolbarTemplate: '<header class="bar bar-nav">\
        <button class="button button-link pull-right close-picker">确定</button>\
        <h1 class="title">请选择预计到家时间</h1>\
        </header>',
        cols: [
          {
            textAlign: 'center',
            displayValues: ['10','11','12','13','14','15', '16', '17', '18', '19', '20', '21', '22'],
            values: ['10:','11:','12:','13:','14:','15:', '16:', '17:', '18:', '19:', '20:', '21:', '22:'],
            //如果你希望显示文案和实际值不同，可以在这里加一个displayValues: [.....]
          },
          {
            textAlign: 'center',
            values: ['00','10', '20', '30', '40', '50'],
            displayValues: ['00','10', '20', '30', '40', '50'],
          }
        ]
      });

      $(".J_timeout").picker({
        toolbarTemplate: '<header class="bar bar-nav">\
        <button class="button button-link pull-right close-picker">确定</button>\
        <h1 class="title">请选择误差时间</h1>\
        </header>',
        cols: [
          {
            textAlign: 'center',
            values: ['0分钟','15分钟','30分钟','45分钟','60分钟','75分钟','90分钟', '105分钟', '120分钟'],
            //如果你希望显示文案和实际值不同，可以在这里加一个displayValues: [.....]
          }
        ]
      });
    }
  }
  dx_setting.init();
})
