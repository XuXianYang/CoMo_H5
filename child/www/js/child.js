$(function(){
  var dx_child = {
    sw:null,
    termlist:null,
    lingUrl:dxutil.baseUrl+"/dx-svc/service/student/quiz/list",
    courseUrl:dxutil.baseUrl+"/dx-svc/service/student/course/schedule/list",
    init:function(){
      var that =this;
      var d = new Date().Format("yyyy-MM-dd");
      $("#J_qianziDate").val(d);
      that.renderPage();
      that.bindEvent();
      that.renderling();
    },
    renderling:function(){
      var that = this;
      $.ajax({
          url: that.lingUrl,
          data: dxutil.addSid({}),
          type: "GET",
          beforeSend:function(){

          },
          success: function(data) {
            if(data.code==0){
              var list = data.data;
              if(list.length>0){
                for(var i=0;i<list.length;i++){
                  if(list[i].quiz.daysAfterNow==0){
                    $(".icon-ling").html('<span class="grad"></span>');
                  }
                }
              }
            }
          }
      });
    },
    bindEvent:function(){
      var that = this;
      //切换顶部tab

      that.clickCourse();
      $(window).bind('hashchange', function(e) {
        that.renderPage();
      });

      $(".J_nowork").bind("click",function(){
        that.renderPage();
      });
      $("#J_qianziDate").calendar({
          maxDate:new Date().Format("yyyy-MM-dd"),
          onClose:function(p){
            that.renderQianzi();
          }
      });

    },
    renderPage:function(){

      var that = this;
      var hash = location.hash;
      if(hash=="#ling"){
        return;
      }
      $(".dx-childcontent").hide();
      $.showIndicator();

      if(hash=="#qianzi"){
        that.renderTab("qianzi");
        that.renderQianzi();
      }else if(hash=="#score"){

        that.renderTab("score");
        that.renderTerm();
      }else if(hash=="#ware"){

        that.renderTab("ware");
        that.renderWare();
      }else{
        that.renderTab("course");
        that.renderCourse();
      }
    },
    renderTab:function(tab){
      if(tab==""){
        tab = "course";
      }
      $(".dx-child-tabfuc li[data-val='"+tab+"']").addClass("active").siblings().removeClass("active");

    },
    renderCourse:function(){
      var that = this;

      $.ajax({
          url: that.courseUrl,
          data: { "sid":$.fn.cookie(dxutil.sid)},
          type: "GET",
          success: function(data) {
            if(data.code==0){
              $.hideIndicator();
              var courselist = data.data;
              var daycourselist = [];
              for(var i=0;i<courselist.length;i++){
                var course = courselist[i];
                var _day = course.dayOfWeek;
                if(!daycourselist[_day]){
                  daycourselist[_day] = [];
                }
                daycourselist[_day].push(course);
              }
              var html = [];
              for(var j in daycourselist){
                html.push(that.renderDayCourse(daycourselist[j]));
              }
              $(".J_allCourse").html(html.join(''));
              $(".J_allCourse .dx-home-timetable").eq(0).addClass("active");

              var weektime = parseInt(new Date().getDay(),10)-1;
              if(weektime>=5){
                weektime = 0;
              }
              $(".dx-course-tab .tab-item").eq(weektime).addClass("active").siblings().removeClass("active");
              $(".dx-home-timetable").removeClass("active").eq(weektime).addClass("active");

              $(".content[data-val='course']").show();
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

    //获取一天课程表html
    renderDayCourse:function(courselist){
      var html = [];
      var rescourse = new Array(9);
      var numtoChinese = ['零','一','二','三','四','五','六','七','八'];
      for(var i=0;i<courselist.length;i++){
        var _num = courselist[i].lessonOfDay;
        rescourse[_num] = courselist[i];
      }
      html.push('<div class="dx-home-timetable tab"><div class="home-timetable"><div class="timetable-con">')
      for(var j=1;j<rescourse.length;j++){
        var courseName = "--";
        var teacherName = "--";
        if(rescourse[j]){
          courseName = rescourse[j].course.name;
          teacherName = rescourse[j].teacher&&rescourse[j].teacher.realName?rescourse[j].teacher.realName:"--";
        }
        html.push('<div class="timetable-item '+(j>4?"afternoon":"")+'">');
        html.push('<span class="timeitem-num">第'+numtoChinese[j]+'节</span>');
        html.push('<span class="timeitem-type"><span>'+courseName+'</span></span>');
        html.push('<span class="timeitem-teacher"><span>'+teacherName.substring(0,10)+'</span></span>');
        html.push('</div>');
      }
      html.push('</div></div></div>')
      return html.join('');
    },
    //课件
    renderWare:function(){
      var that = this;
      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/student/course/material/list",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid({}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){

            $(".content[data-val='ware']").show();
            if(data.data.length>0){
              $(".content[data-val='ware'] .dx-no-content").hide();
              $(".content[data-val='ware'] .dx-courseware").show();
              $(".dx-courseware").html(that.renderWareHtml(data.data));


            }else{
              $(".content[data-val='ware'] .dx-courseware").hide();
              $(".content[data-val='ware'] .dx-no-content").show();
            }


          }else if(data.code=="4003"){
            dxutil.noLogin();
          }else{
            $.toast(data.message);
          }

        },
        error:function(){
          $.hideIndicator();
          $(".J_nowork").show();
        }
      });
    },
    renderWareHtml:function(list){
      var html = [];
      for(var i=0;i<list.length;i++){
        html.push('<div class="courseware-item">');
        html.push('<div class="courseware-top">');
        html.push('<span class="ware-type">'+list[i].course.name.substring(0,1)+'</span>');
        html.push('<span class="ware-title">'+list[i].name+'</span>');
        html.push('</div>');

        html.push('<div class="courseware-con">');
        html.push('<div class="baseinfo">');
        html.push('<span>'+list[i].teacher.realName?list[i].teacher.realName:""+'</span>');
        html.push('<span>'+new Date(list[i].createdAt).Format("MM月DD日")+'</span>');
        html.push('</div>');

        html.push('<div class="waredesc">'+list[i].description+'</div>');

        if(list[i].attachment){
          html.push('<div class="waremore">');
          html.push('<div class="downtitle">课件下载：</div>');
          html.push('<div class="downitem">');
          html.push('<a external href="download.html?downurl='+encodeURIComponent(list[i].attachment.url)+'">'+list[i].attachment.name+'</a>');
          html.push('</div>');
          html.push('</div>');
        }

        html.push('</div>');
        html.push('</div>');
      }
      return html.join('');
    },

    renderQianzi:function(){
      var that = this;
      var getdata = {};
      var studyDate = $("#J_qianziDate").val();
      if(studyDate){
        getdata.studyDate = studyDate;
      }
      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/student/homework/review/list",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid(getdata),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            var list = data.data;
            $(".content[data-val='qianzi']").show();
            if(list.courses&&list.courses.length>0){
              $(".content[data-val='qianzi'] .dx-no-content").hide();
              $(".content[data-val='qianzi'] .dx-booksswiper").show();
              $(".content[data-val='qianzi'] .J_qianziboxtip").show();
              that.renderQianziHtml(list);
              if(that.sw){
                that.sw.destroy();
              }
              that.sw = $(".swiper-container").swiper({
                slidesPerView:1.5,
                centeredSlides:true,
                paginationClickable:true,
                spaceBetween:20,
                pagination: '.swiper-pagination',
                paginationType: 'fraction',
                onSlideChangeEnd:function(swiper){
                  var index = swiper.snapIndex;
                  $(".dx-qianzi-tips").removeClass("active").eq(index).addClass("active");
                }
              });
            }else{
              $(".content[data-val='qianzi'] .dx-booksswiper").hide();
              $(".content[data-val='qianzi'] .dx-no-content").show();
              $(".content[data-val='qianzi'] .J_qianziboxtip").hide();
            }


          }else if(data.code=="4003"){
            dxutil.noLogin();
          }else{
            $.toast(data.message);
          }

        },
        error:function(){
          $.hideIndicator();
          $(".J_nowork").show();
        }
      });
    },
    renderQianziHtml:function(qianzilist){
      var getcourseid = dxutil.getQueryString("id");
      var that = this;
      var list = qianzilist.courses;
      if(list.length>0){
        var html = [];
        var tiphtml = [];
        for(var i=0;i<list.length;i++){
          var courseid=list[i].course.id;//todo
          if(getcourseid == courseid){
            that.qianziactivenum = i;
          }
          var disabled = list[i].reviewed?"disabled":"";
          var bookshowid = parseInt(list[i].course.id,10);
          bookshowid = bookshowid%5;

          html.push('<div class="swiper-slide">');
          html.push('<div class="dx-books book-'+bookshowid+'">');
          html.push('<div class="books-side"></div>');
          html.push('<div class="books-content">');
          html.push('<span class="book-title">'+list[i].course.name+'</span>');
          html.push('<div class="book-info">');
          html.push('<span>'+new Date(qianzilist.studyDate).Format("MM/DD")+'</span>');
          html.push('<span>'+(list[i].teacher.realName?list[i].teacher.realName:"")+'</span>');
          html.push('</div></div></div></div>');

          tiphtml.push('<div class="dx-qianzi-tips '+(i==0?"active":"")+'">');
          tiphtml.push('<div class="tip-logo">TIPS</div>');
          var homeworklist = list[i].homeworks;
          for(var z=0;z<homeworklist.length;z++){
            tiphtml.push('<div class="tip-item">'+homeworklist[z].description+'</div>');
          }
          tiphtml.push('</div>');

        }
        $(".J_qianzibox").html(html.join(''));
        $(".J_qianziboxtip").html(tiphtml.join(''));
        that.reviewHomeWork();
      }
    },


    reviewHomeWork:function(){
      $(".J_review").bind("click",function(){
        var that =$(this);
        var courseId = that.attr("data-homeid");
        if(that.hasClass("disabled")){
          return;
        }else{
          $.ajax({
            url: dxutil.baseUrl+"/dx-svc/service/parent/homework/review",
            dataType: 'json',
            type:"POST",
            data:dxutil.addSid({courseId:courseId}),
            success: function(data){
              $.hideIndicator();
              if(data.code=="0"){
                $.toast("您已完成签字!");
                that.addClass("disabled");

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
    },
    //渲染学期
    renderTerm:function(){
      var that = this;
      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/student/term/list",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid({}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            that.termlist = data.data.reverse();
            that.showTermPick();
            $(".content[data-val='score']").show();
            var lastterm = that.termlist[0];
            var html = lastterm.studyYear+"年第"+lastterm.studyTerm+"学期成绩";
            $(".J_pickhtml").html(html);
            $(".J_pick").attr("data-val",lastterm.studyYear+","+lastterm.studyTerm);
            that.renderScore();

            $(".J_scoreType").bind("click",function(){
              $(this).addClass('active').siblings().removeClass('active');
              that.renderScore();
            })

          }else if(data.code=="4003"){
            dxutil.noLogin();
          }else{
            $.toast(data.message);
          }

        },
        error:function(){
          $.hideIndicator();
          $(".J_nowork").show();
        }
      });

    },
    showTermPick:function(){
      var that = this;
      var values = [];
      var displayValues = [];
      for(var i =0;i<that.termlist.length;i++){
        var obj = that.termlist[i];
        values.push(obj.studyYear+","+obj.studyTerm);
        displayValues.push(obj.studyYear+"年第"+obj.studyTerm+"学期");
      }
      $(".J_pick").picker({
        toolbarTemplate: '<header class="bar bar-nav">\
          <button class="button button-link pull-right close-picker">确定</button>\
          <h1 class="title">请选择学期</h1>\
          </header>',
          cols: [
            {
              textAlign: 'center',
              values: values,
              displayValues:displayValues
            }
          ],
          formatValue:function(picker, value, displayValue){
            var html = displayValue+'成绩'
            $(".J_pickhtml").html(html);
            $(".J_pick").attr("data-val",value);
          },
          onClose:function(){
            that.renderScore();
          }
      })

    },


    renderScore:function(){
      var that = this;
      var term = $(".J_pick").attr("data-val").split(',');
      var studyYear = term[0];
      var studyTerm = term[1];
      var category = $(".J_scoreType.active").attr("data-val");
      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/student/quiz/score/summary",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid({studyYear:studyYear,studyTerm:studyTerm,category:category}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            var list = data.data.quizScores;
            $("#dx-scorechart").hide();
            if(list.length>0){
              $(".content[data-val='score'] .dx-no-content").hide();
              $(".content[data-val='score'] .J_scoreContent").show();
              that.renderScoreHtml(list);
            }else{
              $(".content[data-val='score'] .dx-no-content").show();
              $(".content[data-val='score'] .J_scoreContent").hide();
            }


          }else if(data.code=="4003"){
            dxutil.noLogin();
          }else{
            $.toast(data.message);
          }

        },
        error:function(){
          $.hideIndicator();
          $(".J_nowork").show();
        }
      });
    },
    renderScoreHtml:function(list){
      var html = [];
      var total = 0;
      for(var i=0;i<list.length;i=i+4){
        html.push('<div class="column">');
        for(var j=i;j<i+4;j++){
          var obj = {
            score:" ",
            courseId:"",
            courseName:"  "
          };
          if(list[j]){
            var score = list[j].score;
            if(score){
              total += parseInt(score,10);
            }
            obj.score=score?score:"--";
            obj.courseId = list[j].course.id;
            obj.courseName = list[j].course.name;
          }

          html.push('<div data-id="'+obj.courseId+'" class="cate J_cate">');
          html.push('<span class="num">'+obj.score+'</span>');
          html.push('<span class="lab">'+obj.courseName+'</span>')
          html.push('</div>');
        }
        html.push('</div>')
      }
      var avage = (total/list.length).toFixed(2);
      $(".J_total").text(total);
      $(".J_avage").text(avage);
      $(".J_scourlist").html(html.join(''));
    },
    clickCourse:function(){
      var that =this;
      $("body").on("click",".J_cate",function(){
        if($(this).attr("data-id")==""){
          return false;
        }
        $(".J_cate").removeClass('active');
        $(this).addClass("active");
        var term = $(".J_pick").attr("data-val").split(',');
        var studyYear = term[0];
        var studyTerm = term[1];
        var courseId = $(this).attr("data-id");
        var courseName = $(this).find(".lab").text();
        $.ajax({
          url: dxutil.baseUrl+"/dx-svc/service/student/course/score/trends",
          dataType: 'json',
          type:"GET",
          data:dxutil.addSid({studyYear:studyYear,courseId:courseId}),
          success: function(data){
            $.hideIndicator();
            if(data.code=="0"){
              var obj = {};
              var list = data.data;
              var score = [];
              var datelist = [];
              for(var i=0;i<list.length;i++){
                score.push(list[i].score);
                datelist.push(list[i].quiz.studyMonth+"月");

              }
              obj.score=score;
              obj.datelist=datelist;
              obj.coursename=courseName;
              that.renderChart(obj);


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
      });


    },

    renderChart:function(obj){
      $('#dx-scorechart').show();
      var myChart = echarts.init(document.getElementById('dx-scorechart'));

      var option = {
          backgroundColor:'#fff',
          tooltip : {
              trigger: 'axis',
              formatter:function(params,ticket,callback){
                return params[0].name+"："+params[0].value+"分";
              }
          },
          title:{
            text:obj.coursename+"     ",
            x:"right",
            y:"20px",
            textStyle:{
                fontSize: 14,
                fontWeight: 'bolder',
                color: '#333'
            }
          },
          legend: {
              show:false
          },
          toolbox: {
              show:false
          },
          grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
          },
          xAxis : [
              {
                  type : 'category',
                  boundaryGap : true,
                  axisLine:{
                    show:false
                  },
                  data : obj.datelist
              }
          ],
          yAxis : [
              {
                name:"分数",
                  max:100,
                  min:0,
                  type : 'value'
              }
          ],
          series : [
              {
                  name:obj.coursename,
                  type:'line',
                  smooth:true,
                  symbolSize:8,
                  lineStyle:{
                    normal: {

                    }
                  },
                  areaStyle: {normal: {
                    color:'#e3eefc',
                    opacity:0.3
                  }},
                  data:obj.score
              }
          ]
      };
      myChart.setOption(option);
    }
  }
  dx_child.init();


  $(".dx-course-tab .tab-item").unbind().bind("click",function(){
    var index = $(this).index();
    $(this).addClass("active").siblings().removeClass("active");
    $(".dx-home-timetable").removeClass("active").eq(index).addClass("active");
  })




})
