$(function(){
  var dx_help = {

    init:function(){
      var that = this;
      that.getData();
    },
      markLastMessageAsRead:function(list){
          if(list.length>0){
              var innerList=list[0].sosMessageDtoList;
              if(innerList.length>0){
                  localStorage.setItem("last_read_warning_message_id", innerList[0].id);
              }
          }
      },
    getData:function(){
      var that = this;
      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/parent/sos/msg/list",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid({}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            $(".J_daylist").html(that.renderHtml(data.data))
              that.markLastMessageAsRead(data.data);
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
    renderHtml:function(list){
      var html = [];
      for(var i=0;i<list.length;i++){
        var dayobj = list[i];
        var dayhtml = "";
        if(dayobj.dayDistance==0){
          dayhtml = "今天"
        }
        if(dayobj.dayDistance==1){
          dayhtml = "昨天"
        }
        if(dayobj.dayDistance>1){
          dayhtml = dayobj.dayDistance+"天前"
        }
        html.push('<div class="day">');
        html.push('<div class="day-date">'+dayhtml+'</div>');
        html.push('<div class="day-content">')
        var daylist = dayobj.sosMessageDtoList;
        for(var j=0;j<daylist.length;j++){
          var qdobj = daylist[j];
          if(qdobj.messageType==1||qdobj.messageType==2){
            var time = new Date(qdobj.createdAt).Format('HH:mm');
            html.push('<div class="day-time">');
            html.push('<span class="day-time-item">'+time+'</span>');
            html.push('<span class="day-type-icon">');
            if(qdobj.messageType==1){
              html.push('<i class="i-danger"></i>');
            }
            if(qdobj.messageType==2){
              html.push('<i class="i-qiandao"></i>');
            }
            html.push('</span>');
            html.push('<div class="day-item-detail">');
            if(qdobj.messageType==1){
              html.push('<div class="danger"><span class="color-danger">有危险！</span></div>');
              html.push('<div class="color-gray danger-tip">'+qdobj.content+'</div>');
            }
            if(qdobj.messageType==2){
              html.push('<div>到家签到！</div>');
            }
            html.push('</div></div>');

          }
        }
        html.push('</div></div>');



      }
      return html.join('');
    }
  }
  dx_help.init();
})
