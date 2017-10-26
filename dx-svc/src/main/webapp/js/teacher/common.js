'use strict';

$(function () {
    $('.dx-slide .left-menu li').each(function () {
        var $this = $(this);
        if (window.currentPage === $this.attr('page-name')) {
            $this.addClass("active")
        } else {
            $this.removeClass("active")
        }
    });

});

var global_dx = {
    //通用提示方法
    message: function (title, content, icon, callback) {
        layer.alert(content, {
            title: title,
            skin: 'layui-layer-molv',
            icon: icon,
            closeBtn: 0,
            yes: callback
        });
    }

};

//check if current teacher got new unread message
(function () {
    $.ajax({
        url:"/dx-svc/service/im/user/conversation/info",
        dataType: 'json',
        type:"GET",
        success: function(data){
            if(data.code=="0"){
                var uobj = {
                    id: data.data.userId,
                    realName: data.data.imUserName||"",
                    avatarUrl: data.data.avatarUrl||""
                };

                RongIMClient.init(data.data.appKey);

                //每隔一定时间检查是否有新消息
                setInterval(function () {
                    //如果已经有新消息，不重复检查
                    if ($(".has_new_message").css("display") == "block") return;

                    RongIMClient.getInstance().hasRemoteUnreadMessages(data.data.token, {
                        onSuccess: function (hasMsg) {
                            //hasMsg为true表示有未读消息，为false没有未读消息
                            console.log("has new message: " + hasMsg);
                            if (hasMsg)
                                $(".has_new_message").css("display", "block");
                        },
                        onError: function (error) {
                            console.log("hasRemoteUnreadMessages, errorcode:" + error);
                        }
                    });
                }, 3000);
            }
        },
        error:function(){
            alert("系统异常，稍后再试！");
        }
    });
})();
