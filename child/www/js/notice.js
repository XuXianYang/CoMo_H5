$(function(){
  var dx_notice = {
    noticeUrl:dxutil.baseUrl+"/dx-svc/service/student/announcement/list",
    init:function(){
      var that =this;
      that.renderPage();
    },
    renderPage:function(){
      var that = this;
      $.ajax({
          url: that.noticeUrl,
          data: dxutil.addSid({}),
          type: "GET",
          success: function(data) {
            if(data.code==0){
              $.hideIndicator();
              var list = data.data;
              if(list.length>0){
                $(".J_noticelistno").hide();
                that.showNotice(list);
              }else{
                $(".J_noticelistno").show();
                $(".J_noticelist").html("");
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
    showNotice:function(list){
      var that =this;
      var html = [];
      for(var i=0;i<list.length;i++){
        html.push('<div class="card">');
        html.push('');
        html.push('<div class="card-header">'+list[i].title+'</div>');
        html.push('<div class="card-content">');
        html.push('<div class="card-content-inner">'+list[i].content+'</div>');
        html.push('</div>');
        html.push('<div class="card-footer">'+new Date(list[i].createdAt).Format("yyyy-MM-DD")+'</div></div>');
      }
      $(".J_noticelist").html(html.join(''));
    }

  }

  dx_notice.init();
});
