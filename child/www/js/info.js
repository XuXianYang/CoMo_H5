$(function(){
  var dx_info = {
    jcrop_api:null,
    imagerealW:null,
    imageshowW:null,
    init:function(){
      var that = this;
      var hash = location.hash;
      if(hash=="#headimg"){
        $.router.back();
      }

      that.getData();
      that.bindEvent();
    },
    bindEvent:function(){
      var that = this;
      $("#J_file").bind("change",function(evt){
        $(".upimgbox").html('<img style="width:100%;" src="" id="J_upimg" />');
        var file = evt.dataTransfer !== undefined ? evt.dataTransfer.files[0] : evt.target.files[0];

        var reader = new FileReader();

        $.router.load("#headimg");
        reader.onload = function(e){
          var realbounds;
          $("#J_upimg")[0].src = e.target.result;
          var image = new Image();
          image.onload = function(){
            that.imagerealW = image.width;
          }
          image.src = e.target.result;
          $("#J_upimg")[0].onload = function(){
            that.imageshowW = this.width;
            jQuery('#J_upimg').Jcrop({
              bgColor: 'black',
              allowSelect:false,
              allowResize:true,
              aspectRatio:1,
              sideHandles:false,
              bgOpacity: .6
            },function(){
              that.jcrop_api = this;
              realbounds = this.getWidgetSize();
              that.jcrop_api.setSelect(getselRange());

            });

            function getselRange(){
              var _w = realbounds[0];
              var _h = realbounds[1];
              var _dis = 100;
              if(_w<_dis||_h<_dis){
                _dis = _w>_h?_h:_w;
              }
              var _x1 = 0;
              var _y1 = 0;
              var _x2 = 100;
              var _y2 = 100;
              if(_w>_dis){
                _x1 = _w/2-_dis/2;
                _x2 = _w/2 + _dis/2;
              }else{
                _x1 = 0;
                _x2 = _w;
              }
              if(_h>_dis){
                _y1 = _h/2-_dis/2;
                _y2 = _h/2+_dis/2;
              }else{
                _y1 = 0;
                _y2 = _h;
              }
              var arr = [];
              arr.push(_x1,_y1,_x2,_y2);
              return arr;

            }


          };

        }
        var image = new Image();
        image.onload = function(){
          that.imagerealW = image.width;
        }
        reader.readAsDataURL(file);

      })

      $("#J_update").bind("click",function(){
        var realName = $.trim($("#J_realname").val());
        if(realName==""){
          $.toast("请输入密码",3000);
          return false;
        }
        $.ajax({
          url:dxutil.baseUrl+"/dx-svc/service/user/info/update",
          dataType: 'json',
          type:"POST",
          data:dxutil.addSid({realName:realName}),
          success: function(data){
            $.hideIndicator();
            if(data.code=="0"){
              $.toast("保存成功",1500);

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

      $(".J_save").bind("click",function(){
        var pos = that.jcrop_api.tellSelect();

        var rate = that.imagerealW/that.imageshowW;
        var headForm = new FormData();
        headForm.append("file",$("#J_file")[0].files[0]);
        headForm.append("topX",(parseInt(pos.x,10)*rate).toFixed(0));
        headForm.append("topY",(parseInt(pos.y,10)*rate).toFixed(0));
        headForm.append("width",(parseInt(pos.w,10)*rate).toFixed(0));
        headForm.append("height",(parseInt(pos.h,10)*rate).toFixed(0));
        $.ajax({
          url:dxutil.baseUrl+"/dx-svc/service/user/avatar/upload?sid="+encodeURIComponent($.fn.cookie(dxutil.sid)),
          dataType: 'json',
          type:"POST",
          data:headForm,
          processData: false,
          contentType: false,
          success: function(data){
            $.hideIndicator();
            if(data.code=="0"){
              $.toast("保存成功！");
              $.router.back();
              that.getData();
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
    },
    getData:function(){
      $.ajax({
        url:dxutil.baseUrl+"/dx-svc/service/user/info",
        dataType: 'json',
        type:"GET",
        data:dxutil.addSid({}),
        success: function(data){
          $.hideIndicator();
          if(data.code=="0"){
            var realname = data.data.realName||"";
            if(data.data.studentInfo){
              $(".J_school").text(data.data.studentInfo.schoolName||"--");
              $(".J_class").text(data.data.studentInfo.className||"--");
              $(".J_year").text(data.data.studentInfo.enrolYear?(data.data.studentInfo.enrolYear+"年"):"--");
              $(".J_parentscode").text(data.data.studentInfo.code);
              $("#J_parentscode").text(data.data.studentInfo.code);
            }
            $(".J_name").text(realname);
            $("#J_realname").val(realname);

            if(data.data.avatarUrl){
              $(".J_avatar").attr("src",data.data.avatarUrl);
              $("#J_headimg img").attr("src",data.data.avatarUrl);
            }
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

    }
  }
  dx_info.init();
})
