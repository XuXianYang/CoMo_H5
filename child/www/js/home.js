
$(function(){
  var dx_home = {
    dataUrl:dxutil.baseUrl+"/dx-svc/service/student/home/info",
    init:function(){
      var that = this;
      that.getData();
      that.calnavHeight();

    },
    getData:function(){
      var that = this;
      $.ajax({
        url: that.dataUrl,
        dataType: 'json',
        data:dxutil.addSid({}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            that.renderNotice(data.data.latestAnnouncement);
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
    //渲染公告列表
    renderNotice:function(noticeList){
      if(noticeList.length>0){
        var html = [];
        for(var i=0;i<noticeList.length;i++){
          html.push('<div>'+noticeList[i].title+'</div>');
        }
        $(".J_notice").show().find(".J_noticecon").append(html.join(''));
        $(".J_notice marquee")[0].start();
      }
    },
    calnavHeight:function(){
      var w = $(".dx-home-childnav").width();
      $(".dx-home-childnav1").height(w/720*320);
      $(".dx-home-childnav2").height(w/720*430);
    }
  }
  $(".swiper-container").swiper({
    pagination: '.homeswiper-page'
  });
  dx_home.init();
})
