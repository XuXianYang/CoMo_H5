$(function(){
  var dx_join = {
    init:function(){
      var that = this;
      that.bindEvent();
    },
    bindEvent:function(){
      var that = this;
      $("#J_join").bind("click",function(){
          that.join();
      });
      $(document).on('touchstart click', '.clean-text-input', function (ev) {
          ev.preventDefault();
          $(this).siblings("input").val('').change();
      });
    },
    join:function(){
      var that = this;
      var classNum = $("#J_classNum").val();
      if(classNum==""){
        $.toast("请填写邀请码",3000);
        return false;
      }

      var data = {
        code:classNum
      };
      $.ajax({
        url: dxutil.baseUrl+"/dx-svc/service/student/class/join",
        dataType: 'json',
        type:"POST",
        data:dxutil.addSid(data),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            location.href="home.html";
          }else{
            $.toast(data.message);
          }
        },
        error:function(){
          $.hideIndicator();
        }
      });
    }
  }

  dx_join.init();
})
