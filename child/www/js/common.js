window["WEB_XHR_POLLING"] = true;
document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
    if(window.StatusBar){
        window.StatusBar.hide();
    }
}

$.ajaxSettings.beforeSend = function () {
    $.showIndicator();
}

$.ajaxSettings.timeout = 10000;
String.prototype.Htmlencode = function () {
    return this.replace(/[<>&"]/g, function (c) {
        return {'<': '&lt;', '>': '&gt;', '&': '&amp;', '"': '&quot;'}[c];
    });
}

Date.prototype.Format = function (formatStr) {
    var str = formatStr;
    var Week = ['日', '一', '二', '三', '四', '五', '六'];

    str = str.replace(/yyyy|YYYY/, this.getFullYear());
    str = str.replace(/yy|YY/, (this.getYear() % 100) > 9 ? (this.getYear() % 100).toString() : '0' + (this.getYear() % 100));

    var month = this.getMonth() + 1;
    str = str.replace(/MM/, month > 9 ? month.toString() : '0' + month);
    str = str.replace(/M/g, month);

    str = str.replace(/w|W/g, Week[this.getDay()]);

    str = str.replace(/dd|DD/, this.getDate() > 9 ? this.getDate().toString() : '0' + this.getDate());
    str = str.replace(/d|D/g, this.getDate());

    str = str.replace(/hh|HH/, this.getHours() > 9 ? this.getHours().toString() : '0' + this.getHours());
    str = str.replace(/h|H/g, this.getHours());
    str = str.replace(/mm/, this.getMinutes() > 9 ? this.getMinutes().toString() : '0' + this.getMinutes());
    str = str.replace(/m/g, this.getMinutes());

    str = str.replace(/ss|SS/, this.getSeconds() > 9 ? this.getSeconds().toString() : '0' + this.getSeconds());
    str = str.replace(/s|S/g, this.getSeconds());

    return str;
}

var dxutil = {
    baseUrl: "http://opus.comoclass.com",
    wsUrl: "ws://120.25.79.115:3000",
    sid: "dx_csid",
    addSid: function (data) {
        data.sid = $.fn.cookie(dxutil.sid);
        return data;
    },
    noLogin: function () {
        alert("请重新登录");
        location.replace("index.html#login");
    },
    getQueryString:function(name) {
      var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
      var r = window.location.search.substr(1).match(reg);
      if (r != null) return unescape(r[2]); return null;
    }
}

$(function () {


    $(".J_logout").bind("click", function () {
        $.fn.cookie(dxutil.sid, '', { expires: -1, path: "/" });
        location.replace("index.html");
    });

    if ($(".J_showsafe").length > 0) {
        $.ajax({
            url: dxutil.baseUrl + "/dx-svc/service/student/sos/msg/getWarningTime",
            dataType: 'json',
            type: "GET",
            data: dxutil.addSid({}),
            beforeSend: function () {

            },
            success: function (data) {

                if (data.code == "0") {
                    if (data.data && data.data.expectTime) {
                        $(".J_exceptarivetime").text(data.data.expectTime);
                    }
                }
            }
        });
    }


    (function () {
        //是否需要唤起安全警报设置窗口
        if ("true" == window.localStorage.getItem("need_toggle_safe_window")) {
            var safeSettingWindow = $(".J_showsafe");
            //如果当前页面包含安全设置按钮，则唤起
            if (safeSettingWindow.length > 0) {
                window.localStorage.setItem("need_toggle_safe_window", "false");
                $(".page").append('<div class="dx-overlay"></div>');
                $(".J_showsafe").hide();
                $(".J_safeupbox").show();
            }
        }

        //在进入设置页面之前，先本地记录是否需要唤起的标志，mock成native app的行为
        $(document).on("click", ".sefe-setting", function () {
            window.localStorage.setItem("need_toggle_safe_window", "true");
        });
    })();


    $(document).on("click", ".J_showsafe", function () {
        $(".page").append('<div class="dx-overlay"></div>');
        $(".J_showsafe").hide();
        $(".J_safeupbox").show();
    });

    $(document).on("click", ".J_hidesafe", function () {
        $(".dx-overlay").remove();
        $(".J_showsafe").show();
        $(".J_safeupbox").hide();
    });

    $(document).on("click", ".safe-tabitem", function () {
        $(".safe-tabitem").removeClass('active');
        $(this).addClass("active");
        var index = $(this).index();
        $(".safe-danger").hide();
        if (index == 0) {
            $(".J_danger").show();
        } else {
            $(".J_msg").show();
        }
    });

    $(document).on("click", ".J_send_safehome", function () {
        $.ajax({
            url: dxutil.baseUrl + "/dx-svc/service/student/safe/msg/send",
            dataType: 'json',
            type: "POST",
            data: dxutil.addSid({"content": "我到家了"}),
            success: function (data) {
                $.hideIndicator();
                $.closeModal();
                if (data.code == "0") {
                    $.toast("我到家了");
                    $(".dx-overlay").remove();
                    $(".J_showsafe").show();
                    $(".J_safeupbox").hide();
                } else {
                    $.toast(data.message);
                }
            },
            error: function () {
                $.hideIndicator();
            }
        });
    })


    $(document).on("click", ".J_send_dangerbtn", function () {
        if ($(".J_dangercontent").val().length > 50) {
            $.toast("发送内容不能超过50字！");
            return false;
        }
        $.ajax({
            url: dxutil.baseUrl + "/dx-svc/service/student/sos/msg/send",
            dataType: 'json',
            type: "POST",
            data: dxutil.addSid({"content": $(".J_dangercontent").val()}),
            success: function (data) {
                $.hideIndicator();
                if (data.code == "0") {
                    $.toast("发送成功");
                    $(".safecontent").val("");
                    $(".dx-overlay").remove();
                    $(".J_showsafe").show();
                    $(".J_safeupbox").hide();
                } else {
                    $.toast(data.message);
                }
            },
            error: function () {
                $.hideIndicator();
            }
        });
    });


    $(document).on("click", ".J_send_msgbtn", function () {
        if ($(".J_msgcontent").val().length > 50) {
            $.toast("发送内容不能超过50字！");
            return false;
        }
        $.ajax({
            url: dxutil.baseUrl + "/dx-svc/service/student/sneaking/msg/send",
            dataType: 'json',
            type: "POST",
            data: dxutil.addSid({"content": $(".J_msgcontent").val()}),
            success: function (data) {
                $.hideIndicator();
                if (data.code == "0") {
                    $.toast("发送成功");
                    $(".safecontent").val("");
                    $(".dx-overlay").remove();
                    $(".J_showsafe").show();
                    $(".J_safeupbox").hide();
                } else {
                    $.toast(data.message);
                }
            },
            error: function () {
                $.hideIndicator();
            }
        });
    });

    $(document).on("click", ".safecontent", function () {
        var innerHeight = window.innerHeight;
        if (innerHeight > 400) innerHeight = innerHeight / 2;
//        $("body").height(innerHeight);
        window.scrollTo(0, document.body.scrollHeight);
    });

    $(".safecontent").on('blur', function () {
//        $("body").height(window.innerHeight);
        window.scrollTo(0, document.body.scrollHeight);
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
