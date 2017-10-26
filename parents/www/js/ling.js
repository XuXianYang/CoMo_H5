$(function(){
  var dx_child = {
    lingUrl:dxutil.baseUrl+"/dx-svc/service/student/quiz/list",
    init:function(){
      var that =this;
      that.renderPage();
    },
    renderPage:function(){
      var that = this;
      $.ajax({
          url: that.lingUrl,
          data: dxutil.addSid({}),
          type: "GET",
          success: function(data) {
            if(data.code==0){
              $.hideIndicator();
              var list = data.data;
              if(list.length>0){
                $(".J_linklistno").hide();
                that.showLing(list);
              }else{
                $(".J_linklistno").show();
                $(".J_linglist").html("");
              }
            }else if(data.code=="4003"){
              dxutil.noLogin();
            }else{
              $.hideIndicator();
              $.toast(data.message);
            }

          },
          error: function() {
            $.hideIndicator();
            $(".J_nowork").show();
          }
      });
    },
    showLing:function(list){
      var html = [];
      var categoryToChinese = ["","期末考试","期中考试","月考"];
      for(var i=0;i<list.length;i++){
        var start = new Date(list[i].quiz.startTime);
        var leave = list[i].quiz.daysAfterNow;
        var leavehtml = "";
        if(leave==0){
          leavehtml = '<div class="ling-remind tody"><span>今天<br/>考试</span></div>'
        }
        if(leave>0){
          var leavehtml = '<div class="ling-remind otherday"><span>还剩<span class="leaveday">'+leave+'</span>天</span></div>';
        }
        html.push('<div class="ling-item">');
        html.push('<div class="ling-main">');
        html.push('<span class="ling-item-title">'+categoryToChinese[list[i].quiz.category]+'('+list[i].course.name+')</span>');
        html.push('<span class="ling-item-date">'+start.Format('yyyy-MM-DD 周W HH:mm')+'</span>');
        html.push('<span class="ling-item-location"></span>');
        html.push('</div>');
        html.push(leavehtml);
        html.push('</div>');
      }
      $(".J_linglist").html(html.join(''));
    }

  }

  dx_child.init();
});
