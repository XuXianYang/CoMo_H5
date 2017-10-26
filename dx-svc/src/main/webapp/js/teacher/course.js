$(function(){
    var dxcourse = {
        courseList:null,
        classid:null,
        init:function(){
            var that = this;
            that.initSelClass();
            if($(".classitem.sel").length>0){
                $(".hasclass").show();
                that.classid = $(".classitem.sel").attr("data-id");
                that.getData();
                that.getALLcourse();
            }else{
                $(".noclass").show();
            }


        },
        initSelClass:function(){
            if($(".classitem.sel").length==0 && $(".classitem").length>0){
                $(".classitem").eq(0).addClass("sel");
            }
        },
        getData:function(){
            var that =this;
            $.ajax({
                url: "/dx-svc/service/teacher/class/course/schedule",
                dataType: 'json',
                type: "GET",
                data: {"classId":that.classid},
                success: function (data) {
                    if(data.code=="0"){
                        for(var i=0;i<data.data.length;i++){
                            var obj = data.data[i];
                            var day = obj.dayOfWeek;
                            var lesson = obj.lessonOfDay;
                            var courseid = obj.course.id;
                            var name = obj.course.name;
                            var html ='<div class="J_courseitem" data-id="'+courseid+'"><span class="J_coursename">'+name+'</span></div>';
                            $("td[data-val='"+lesson+"_"+day+"']").html(html);
                        }
                        that.bindEvent();

                    }
                }
            });

        },
        getALLcourse:function(){
            var that =this;
            $.ajax({
                url: "/dx-svc/service/teacher/course/all",
                dataType: 'json',
                type: "GET",
                success: function (data) {
                    if(data.code=="0"){
                        that.courseList = data.data;

                    }
                }
            });
        },
        getAllcourseHtml:function(){
            var that = this;
            if(that.courseList){
                var html = [];
                html.push('<ul class="allcourselist">');
                for(var i=0;i<that.courseList.length;i++){
                    var course = that.courseList[i];
                    var rh = "";
                    if(i%2==1){
                        rh = 'class="noright"';
                    }
                    html.push('<li '+rh+' data-v="'+course.id+'">'+course.name+'</li>');
                }
                html.push('<li data-v="" class="removecourse">清空</li>');
                html.push("</ul>");
                return html.join('');
            }else{
                return "";
            }
        },
        getResultList:function(){
            var courselist = $(".editing");
            var jsonlist = [];
            for(var i=0;i<courselist.length;i++){
                var course = $(courselist[i]);
                var lessonday = course.attr("data-val").split("_");
                var courseId = course.find(".J_courseitem").attr("data-id");
                var obj = {
                    "courseId":courseId,
                    "dayOfWeek":lessonday[1],
                    "lessonOfDay":lessonday[0]
                }
                if(courseId){
                    jsonlist.push(obj);
                }

            }
            return jsonlist;

        },
        bindEvent:function(){
            var that =this;
            $(".J_startedit").bind("click",function(){
                $(this).hide();
                $(".J_startsave").show();
                $(".coursetable-tr td").addClass("editing");
                $(".J_courseitem").append('<span class="coursearrow Hui-iconfont Hui-iconfont-arrow2-bottom"></span>');

            })

            $(".J_startsave").bind("click",function(){
                $.ajax({
                    url: "/dx-svc/service/teacher/class/course/schedule/set",
                    dataType: 'json',
                    type: "POST",
                    data:{
                        "classId":that.classid,
                        "courseSchedules":JSON.stringify(that.getResultList())
                    },
                    success: function (data) {

                        if(data.code=="0"){
                            global_dx.message("提示",'保存并更新成功！',1,function(){
                                location.reload();
                            })
                        }
                    }
                });

                //alert(that.getResultList().toString());
            })

            $(".coursetable-tr").on("click",".editing",function(){
                resetdown();
                var curcourseid = $(this).find(".J_courseitem").attr("data-id");
                $(this).find(".J_courseitem").addClass("downitem");
                $(this).append(that.getAllcourseHtml());
                $(".allcourselist li[data-v='"+curcourseid+"']").addClass("seld");


            })
            $(".coursetable-tr td").on("click",".allcourselist li",function(e){

                resetdown();
                var delegatedom = $(e.delegateTarget);
                var courseid= $(this).attr("data-v");
                var coursename = $(this).text();
                if(courseid==""){
                    coursename="　";
                }
                delegatedom.find(".J_courseitem").attr("data-id",courseid);
                delegatedom.find(".J_coursename").text(coursename);

            })
            function resetdown(){
                $(".downitem").removeClass("downitem");
                $(".allcourselist").remove();
            }
        }
    }

    dxcourse.init();
})