window["WEB_XHR_POLLING"] = true;
document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
    if(window.StatusBar){
        window.StatusBar.hide();
    }
}

$.ajaxSettings.beforeSend = function(){
  $.showIndicator();
}

$.ajaxSettings.timeout = 10000;

String.prototype.Htmlencode = function(){
  return this.replace(/[<>&"]/g,function(c){return {'<':'&lt;','>':'&gt;','&':'&amp;','"':'&quot;'}[c];});
}

Date.prototype.Format = function(formatStr)
{
    var str = formatStr;
    var Week = ['日','一','二','三','四','五','六'];

    str=str.replace(/yyyy|YYYY/,this.getFullYear());
    str=str.replace(/yy|YY/,(this.getYear() % 100)>9?(this.getYear() % 100).toString():'0' + (this.getYear() % 100));

    var month = this.getMonth()+1;
    str=str.replace(/MM/,month>9?month.toString():'0' + month);
    str=str.replace(/M/g,month);

    str=str.replace(/w|W/g,Week[this.getDay()]);

    str=str.replace(/dd|DD/,this.getDate()>9?this.getDate().toString():'0' + this.getDate());
    str=str.replace(/d|D/g,this.getDate());

    str=str.replace(/hh|HH/,this.getHours()>9?this.getHours().toString():'0' + this.getHours());
    str=str.replace(/h|H/g,this.getHours());
    str=str.replace(/mm/,this.getMinutes()>9?this.getMinutes().toString():'0' + this.getMinutes());
    str=str.replace(/m/g,this.getMinutes());

    str=str.replace(/ss|SS/,this.getSeconds()>9?this.getSeconds().toString():'0' + this.getSeconds());
    str=str.replace(/s|S/g,this.getSeconds());

    return str;
}

var dxutil = {
  baseUrl:"http://opus.comoclass.com",
  //baseUrl:"http://139.224.30.227:8080",
  wsUrl:"ws://120.25.79.115:3000",
  sid:"dx_sid",
  addSid:function(data){
    data.sid = $.fn.cookie(dxutil.sid);
    return data;
  },
  noLogin:function(){
      alert("请重新登录");
      location.replace("index.html#login");
  },
  getQueryString:function(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
  }
}

$(function(){
  $(".J_logout").bind("click",function(){
    $.fn.cookie(dxutil.sid, '', { expires:-1,path:"/" });
      location.replace("index.html");
  });
});


(function () {
    if ($(".has_new_message").length == 0) {
        //不需要检查是否有新消息
        return;
    }
    $.ajax({
        url: dxutil.baseUrl + "/dx-svc/service/im/user/conversation/info",
        dataType: 'json',
        type: "GET",
        data: dxutil.addSid({}),
        success: function (data) {
            if (data.code == "0") {
                var uobj = {
                    id: data.data.userId,
                    realName: data.data.imUserName || "",
                    avatarUrl: data.data.avatarUrl || ""
                };
                RongIMClient.init(data.data.appKey);

                //每隔一定时间检查是否有新消息
                setInterval(function () {
                    //如果已经有新消息，不重复检查
                    if ($(".has_new_message").css("display") == "block") return;

                    RongIMClient.getInstance().hasRemoteUnreadMessages(data.data.token, {
                        onSuccess: function (hasMsg) {
                            //hasMsg为true表示有未读消息，为false没有未读消息
                            if (hasMsg)
                                $(".has_new_message").css("display", "block");
                        },
                        onError: function (error) {
                            console.log("hasRemoteUnreadMessages,errorcode:" + error);
                        }
                    });
                }, 3000);

            }

        },
        error: function () {
//            $.hideIndicator();
        }
    });

})();
