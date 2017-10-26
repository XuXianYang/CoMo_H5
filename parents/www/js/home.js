
$(function(){
  var dx_home = {
    dataUrl:dxutil.baseUrl+"/dx-svc/service/student/home/info",
    init:function(){
      var that = this;
      that.getData();

    },
      detectHasUnreadWarningMessage:function(){
          $.ajax({
              url:dxutil.baseUrl+"/dx-svc/service/parent/sos/msg/list",
              dataType: 'json',
              type:"GET",
              data:dxutil.addSid({}),
              success: function(data){
                  $.hideIndicator();
                  if(data.code=="0"){
                      var list = data.data;
                      if(list.length>0){
                          var innerList=list[0].sosMessageDtoList;
                          if(innerList.length>0){
                              if(innerList[0].id != localStorage.getItem("last_read_warning_message_id")){
                                  //has new message
                                  $(".has_warning_message").show();
                                  console.log("has new message");
                              }else{
                                  $(".has_warning_message").hide();
                                  console.log("no new message");
                              }
                          }
                      }
                  }

              },
              error:function(){
                  $.hideIndicator();
              }
          });
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
            that.renderStudent(data.data.user);
            that.renderNotice(data.data.latestAnnouncement);
            //that.renderQianzi(data.data.homeworkReviewStatus);

            that.renderCourse(data.data.courseSchedule);
            that.renderSystem(data.data.sysInfo);
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
      that.detectHasUnreadWarningMessage();
    },
    renderSystem:function(sys){
      var date = new Date(sys.currentDate);
      var mon = date.getMonth()+1;
      mon = mon>9?mon:"0"+mon;
      var day = date.getDate();
      day = day>9?day:"0"+day;
      var weekChinese = ["日","一","二","三","四","五","六"];
      var week = weekChinese[date.getDay()];
      var html = [];
      html.push('<span class="top-l-str">今日课表</span>');
      html.push('<span class="date">'+mon+'</span> 月 <span class="date">'+day+'</span> 日 <span class="date">周'+week+'</span>');
      $(".J_sourceDay").html(html.join(''));
    },
    //渲染学生相关
    renderStudent:function(data){
      $(".J_stuname").text(data.username);
      $(".J_school").text(data.studentInfo.schoolName + " " + data.studentInfo.className);
      if(data.avatarUrl){
        $(".J_avatar").html("<img src='"+data.avatarUrl+"'/>");
      }
    },
    //渲染公告列表
    renderNotice:function(noticeList){
      if(noticeList.length>0){
        var html = [];
        for(var i=0;i<noticeList.length;i++){
          html.push('<div>'+noticeList[i].title+'</div>')
        }
        $(".J_notice").show().find(".J_noticecon").append(html.join(''));
        $(".J_notice marquee")[0].start();
      }
    },
    //渲染签字本
    renderQianzi:function(qianzilist){
      var qianzibox = $(".J_qianzibox");
      var list = qianzilist.courses;
      var len = list?list.length:0;
      var that= this;
      if(len>0){
        var html = [];
        html.push('<div class="swiper-wrapper">');
        for(var i=0;i<len;i+=3){
          html.push('<div class="qianzi-wrap swiper-slide">');
          for(var j=i;j<i+3;j++){

            if(j<len){
              var bookshowid = parseInt(list[j].course.id,10);
              bookshowid = bookshowid%5;

              html.push('<div class="qiznzi-item">');
              html.push('<a external href="child.html?id='+list[j].course.id+'#qianzi">')
              html.push('<div class="dx-books book-'+bookshowid+'">');
              html.push('<div class="books-side"></div>');
              html.push('<div class="books-content">');
              html.push('<span class="book-title">'+list[j].course.name+'</span>');
              html.push('<div class="book-info"><span>'+list[j].teacher.realName+'</span></div>');
              html.push('</div></div></a></div>');
            }else{
              html.push('<div class="qiznzi-item noitem">');
              html.push('</div>');
            }
          }
          html.push('</div>')
        }
        html.push('</div>')
        qianzibox.append(html.join(''));
        if(len>3){
          $(".qianziswiper-page").show();
          $(".qianzi-list").swiper({
            pagination: '.qianziswiper-page'
          });
        }
      }else{
        qianzibox.append('<div class="no-content">暂无内容</div>');
      }
    },

    //渲染课程表
    renderCourse:function(courselist){
      if(courselist.length>0){
        var html = [];
        var rescourse = new Array(9);
        var numtoChinese = ['零','一','二','三','四','五','六','七','八'];
        for(var i=0;i<courselist.length;i++){
          var _num = courselist[i].lessonOfDay;
          rescourse[_num] = courselist[i];
        }
        for(var j=1;j<rescourse.length;j++){
          var courseName = "--";
          var teacherName = "--";
          if(rescourse[j]){
            courseName = rescourse[j].course.name;
            teacherName = rescourse[j].teacher&&rescourse[j].teacher.realName?rescourse[j].teacher.realName.substring(0,10):"--";
          }
          html.push('<div class="timetable-item '+(j>4?"afternoon":"")+'">');
          html.push('<span class="timeitem-num">第'+numtoChinese[j]+'节</span>');
          html.push('<span class="timeitem-type"><span>'+courseName+'</span></span>');
          html.push('<span class="timeitem-teacher"><span>'+teacherName+'</span></span>');
          html.push('</div>');
        }
        $('.J_sourcelist').html(html.join(''));
      }
    }

  }
  $(".swiper-container").swiper({
    pagination: '.homeswiper-page'
  });
  dx_home.init();
})
