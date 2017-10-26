var dx_chat = {
  user:null,
  socket:null,
  init:function(){
    var that = this;
    var roomid = location.hash.substring(1);

    $.ajax({
      url:dxutil.baseUrl+"/dx-svc/service/user/info",
      dataType: 'json',
      type:"GET",
      data:dxutil.addSid({}),
      success: function(data){
        $.hideIndicator();
        if(data.code=="0"){
          $(".J_groupname").text(data.data.studentInfo.className);
          that.user = {
            id:data.data.id,
            realName:data.data.realName,
            avatarUrl:data.data.avatarUrl
          }
          that.socket = io(dxutil.wsUrl,{
            query:'roomid='+roomid
          });
          that.socket.emit('login', that.user);
          that.addMessage();
          that.bindEvent();

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
  bindEvent:function(){
    var that = this;
    $(".J_send").bind("click",function(){
      var content = $("#textContent").val();
      if(content==""){
        return;
      }
      var obj = {
        id:that.user.id,
        realName:that.user.realName,
        avatarUrl:that.user.avatarUrl,
        content:content
      }
      that.socket.emit('message', obj);
      $("#textContent").val("");
      emoji.emojiBox.hide();
      $(".content").css({"bottom":"3rem"});
    })
  },
  addMessage:function(){
    var that = this;

    that.socket.on('message', function(obj){
      $(".J_message").append($(that.getMessageHtml(obj)).parseEmotion() );
      $(".content")[0].scrollTop = $(".content")[0].scrollHeight;
    });

    that.socket.on('login', function(obj){
      var htmllist = [];
      for(var i=0;i<obj.length;i++){
        htmllist.push(that.getMessageHtml(obj[i]));
      }
      var htmlmsg = htmllist.join('');
      $(".J_message").append($(htmlmsg).parseEmotion());
      //$(".J_message").parseEmotion();
      $(".content")[0].scrollTop = $(".content")[0].scrollHeight;
    });

  },
  getMessageHtml:function(obj){
    var that = this;
    var isme = (obj.id == that.user.id) ? "talkitemme" : "";

    var html = [];
    html.push('<div class="talkitem '+isme+'">');
    html.push('<div class="headicon">');
    html.push('<img src="'+(obj.avatarUrl?obj.avatarUrl:"./img/nohead.png")+'" />');
    html.push('</div>');
    html.push('<div class="talkinfo">');
    html.push('<span class="talkname">'+(obj.realName?obj.realName:"路人")+'</span>');
    html.push('<div class="talkout clearfix">')
    html.push('<span class="talkmessage">'+obj.content.Htmlencode()+'</span>');
    html.push('</div></div></div>');
    return html.join('');

  }


}

$(function(){
  emoji.emojiInit();
  dx_chat.init();
})
