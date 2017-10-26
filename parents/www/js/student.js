$(function(){
  var dx_student = {

    init:function(){
      var that = this;
      that.getStudentList();
      that.bindEvent();
    },
    getStudentList:function(){
      var that = this;
      $(".no-student").hide();
      $(".have-student").hide();
      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/parent/child/list",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid({}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            var list = data.data;
            if(list.length==0){
              $(".no-student").show();
              $(".have-student").hide();
            }else{
              $(".no-student").hide();
              that.renderStudentList(data.data);
              $(".have-student").show();
            }



          }else if(data.code=="4003"){
            dxutil.noLogin();
          }else{
            $.toast(data.message);
          }

        },
        error:function(){
          $.hideIndicator();
          $.toast("加载数据失败，请确认网络状况");
        }
      });


    },
    renderStudentList:function(list){
      var html = [];
      for(var i=0;i<list.length;i++){
        var obj = list[i];
        var aratar = "";
        if(obj.avatarUrl){
          aratar = obj.avatarUrl;
        }else{
          aratar = "./img/nohead.png";
        }
        html.push('<li data-userId="'+obj.id+'" class="item-content">');
        html.push('<div class="item-media">');
        html.push('<img src="'+aratar+'" width="60">');
        html.push('</div>');
        html.push('<div class="item-inner"><div class="item-title-row">');
        html.push('<div class="item-title">'+(obj.realName?obj.realName:"--")+'</div>');
        if(obj.isDefaultChild){
          html.push('<span class="default-student">默认</span>');
        }
        html.push('</div>');
        html.push('<div class="item-subtitle">');
        html.push(obj.studentInfo.schoolName+' '+obj.studentInfo.className);
        html.push('</div>');
        html.push('</div>');
        html.push('</li>');
      }
      $(".J_studentList").html(html.join(''));

    },
    bindEvent:function(){
      var that = this;
      $(".J_addStudent").bind("click",function(){

        var code = $("#J_studentCode").val();
        var relation = $("#J_studentrelation").val();
        if(code==""){
          $.toast("请输入孩子的学生码",3000);
          return false;
        }
        var data = {studentCode:code,relation:relation};
        $.ajax({
          url:dxutil.baseUrl+"/dx-svc/service/parent/child/add",
          dataType: 'json',
          type:"POST",
          data:dxutil.addSid(data),
          success: function(data){
            $.hideIndicator();
            if(data.code=="0"){
              $.router.back();
              that.getStudentList();
            }else if(data.code=="4003"){
              dxutil.noLogin();
            }else{
              $.toast(data.message);
            }

          },
          error:function(){
            $.hideIndicator();
            $.toast("系统异常，请稍后再试");
          }
        });


      });

      $(".J_studentList").on("click",".item-content",function(){
        var item = $(this);
        var studentUserId = $(this).attr("data-userId");
        if(item.find(".default-student").length>0){
          return;
        }else{
          $.ajax({
            url:dxutil.baseUrl+"/dx-svc/service/parent/child/select",
            dataType: 'json',
            type:"POST",
            data:dxutil.addSid({studentUserId:studentUserId}),
            success: function(data){
              $.hideIndicator();
              if(data.code=="0"){
                $(".default-student").remove();
                item.find(".item-title-row").append('<span class="default-student">默认</span>');
              }else if(data.code=="4003"){
                dxutil.noLogin();
              }else{
                $.toast(data.message);
              }

            },
            error:function(){
              $.hideIndicator();
              $.toast("加载数据失败，请确认网络状况");
            }
          });

        }

      })
    }
  }
  dx_student.init();
})
