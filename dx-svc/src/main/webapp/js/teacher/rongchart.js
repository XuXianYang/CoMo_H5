String.prototype.Htmlencode = function () {
    return this.replace(/[<>&"]/g, function (c) {
        return {'<': '&lt;', '>': '&gt;', '&': '&amp;', '"': '&quot;'}[c];
    });
};

var dx_chat = {
    user: null,
    init: function (obj, user) {

        var that = this;
        that.user = user;
        that.rongyuninit(obj);
        that.renderHistoryMessage(obj);
        that.bindEvent(obj, user);
        that.addNewMessageTips();

    },
    addNewMessageTips: function () {
        $.each($(".has_new_group_message"), function (index, item) {
            var imGroupId = $(item).data("groupid");
            var key_for_new = "has_new_" + imGroupId;
            var has_news = localStorage.getItem(key_for_new);
            var blockStyle = "none";
            if ("true" == has_news) {
                blockStyle = "block";
            }

            $(item).css("display", blockStyle);
        });
    },
    renderHistoryMessage: function (data) {
        var that = this;
        var imGroupId = data.imGroupId;
        if (!imGroupId) return;
        var key = "ms_" + imGroupId;
        var key_for_new = "has_new_" + imGroupId;
        //标记为已读
        localStorage.setItem(key_for_new, false);
        var item = localStorage.getItem(key);
        if (!item) {
            item = [];
        } else {
            item = JSON.parse(item);
        }

        that.renderMessageList2Html(item);

    }, recordLocalMessage: function (message, is_new) {
        var that = this;
        var key = "ms_" + message.targetId;
        var key_for_new = "has_new_" + message.targetId;
        var item = localStorage.getItem(key);
        if (!item) {
            item = [];
        } else {
            item = JSON.parse(item);
        }

        item.push(message);

        that.resizeLocalMessageLength(100, item);

        localStorage.setItem(key, JSON.stringify(item));

        localStorage.setItem(key_for_new, is_new);
    },
    resizeLocalMessageLength: function (len, item) {
        var that = this;
        while (item.length > len) {
            item.shift();
        }
    },
    renderMessageList2Html: function (itemList) {
        var that = this;
        var htmllist = [];
        for (var i = 0; i < itemList.length; i++) {
            htmllist.push(that.getMessageHtml(itemList[i].content.content, itemList[i].content.extra));
        }

        var htmlmsg = htmllist.join('');
        $(".J_message").append($(htmlmsg).parseEmotion());
        $(".content")[0].scrollTop = $(".content")[0].scrollHeight;
    },
    rongyuninit: function (obj) {
        var that = this;
        // 初始化。
        RongIMClient.init(obj.appKey);

        // 设置连接监听状态 （ status 标识当前连接状态）
        // 连接状态监听器
        RongIMClient.setConnectionStatusListener({
            onChanged: function (status) {
                switch (status) {
                    //链接成功
                    case RongIMLib.ConnectionStatus.CONNECTED:

                        break;
                    //正在链接
                    case RongIMLib.ConnectionStatus.CONNECTING:

                        break;
                    //重新链接
                    case RongIMLib.ConnectionStatus.DISCONNECTED:
                        that.appendMsg("连接已经断开");
                        //$.toast('断开连接');
                        break;
                    //其他设备登录
                    case RongIMLib.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT:
                        that.appendMsg("其他设备登录");
                        //$.toast('其他设备登录');
                        break;
                    //网络不可用
                    case RongIMLib.ConnectionStatus.NETWORK_UNAVAILABLE:
                        that.appendMsg("当前网络不可用");
                        //$.toast('当前网络不可用');
                        break;
                }
            }});

        // 消息监听器
        RongIMClient.setOnReceiveMessageListener({
            // 接收到的消息
            onReceived: function (message) {
                var is_new = false;
                if (message.targetId != $("#J_imGgroupId").val()) {
                    is_new = true;
                }

                that.recordLocalMessage(message, is_new);
                that.addNewMessageTips();
                // 判断消息类型
                switch (message.messageType) {
                    case RongIMClient.MessageType.TextMessage:
                        // 发送的消息内容将会被打印
                        $(".J_message").append($(that.getMessageHtml(message.content.content, message.content.extra)).parseEmotion());
                        $(".content")[0].scrollTop = $(".content")[0].scrollHeight;
                        //console.log(message.content.content);
                        break;
                    case RongIMClient.MessageType.VoiceMessage:
                        // 对声音进行预加载
                        // message.content.content 格式为 AMR 格式的 base64 码
                        RongIMLib.RongIMVoice.preLoaded(message.content.content);
                        break;
                    case RongIMClient.MessageType.ImageMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.DiscussionNotificationMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.LocationMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.RichContentMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.DiscussionNotificationMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.InformationNotificationMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.ContactNotificationMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.ProfileNotificationMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.CommandNotificationMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.CommandMessage:
                        // do something...
                        break;
                    case RongIMClient.MessageType.UnknownMessage:
                        // do something...
                        break;
                    default:
                    // 自定义消息
                    // do something...
                }
            }
        });


        var token = obj.token;
        RongIMClient.connect(token, {
            onSuccess: function (userId) {
                console.log("connect success:" + userId);
                //todo 拉取消息
            },
            onTokenIncorrect: function () {
                alert('无效的用户');

            },
            onError: function (errorCode) {
                var info = '';
                switch (errorCode) {
                    case RongIMLib.ErrorCode.TIMEOUT:
                        info = '超时';
                        break;
                    case RongIMLib.ErrorCode.UNKNOWN_ERROR:
                        info = '未知错误';
                        break;
                    case RongIMLib.ErrorCode.UNACCEPTABLE_PaROTOCOL_VERSION:
                        info = '不可接受的协议版本';
                        break;
                    case RongIMLib.ErrorCode.IDENTIFIER_REJECTED:
                        info = 'appkey不正确';
                        break;
                    case RongIMLib.ErrorCode.SERVER_UNAVAILABLE:
                        info = '服务器不可用';
                        break;
                }
                alert('系统异常:' + info);
            }
        });
    },
    appendMsg: function (msg) {
        var html = '<div class="talktips">' + msg + '</div>';
        $(".J_message").append(html);
        $(".content")[0].scrollTop = $(".content")[0].scrollHeight;
    },
    bindEvent: function (obj, user) {
        var that = this;
        $(".J_send").bind("click", function () {
            var content = $("#textContent").val();
            if (content == "") {
                return;
            }
            var extra = user.id + "," + user.realName + "," + user.avatarUrl;
            var msg = new RongIMLib.TextMessage({content: content, extra: extra});
            var conversationtype = RongIMLib.ConversationType.GROUP; // 私聊
            var targetId = obj.imGroupId; // 目标 Id
            RongIMClient.getInstance().sendMessage(conversationtype, targetId, msg, {
                    // 发送消息成功
                    onSuccess: function (message) {
                        that.recordLocalMessage(message, false);
                        //message 为发送的消息对象并且包含服务器返回的消息唯一Id和发送消息时间戳
                        $(".J_message").append($(that.getMessageHtml(content, extra)).parseEmotion());
                        $(".content")[0].scrollTop = $(".content")[0].scrollHeight;
                    },
                    onError: function (errorCode, message) {
                        var info = '';
                        switch (errorCode) {
                            case RongIMLib.ErrorCode.TIMEOUT:
                                info = '超时';
                                break;
                            case RongIMLib.ErrorCode.UNKNOWN_ERROR:
                                info = '未知错误';
                                break;
                            case RongIMLib.ErrorCode.REJECTED_BY_BLACKLIST:
                                info = '在黑名单中，无法向对方发送消息';
                                break;
                            case RongIMLib.ErrorCode.NOT_IN_DISCUSSION:
                                info = '不在讨论组中';
                                break;
                            case RongIMLib.ErrorCode.NOT_IN_GROUP:
                                info = '不在群组中';
                                break;
                            case RongIMLib.ErrorCode.NOT_IN_CHATROOM:
                                info = '不在聊天室中';
                                break;
                            default :
                                info = '未知异常';
                                break;
                        }
                        alert('发送失败:' + info);
                    }
                }
            );
            $("#textContent").val("");
            emoji.emojiBox.hide();
        })
    },
    getMessageHtml: function (content, extra) {
        if(typeof extra  == "undefined" || extra == null || extra.length == 0 ) return;
        var userinfo = extra.split(',');
        var that = this;
        var isme = (userinfo[0] == that.user.id) ? "talkitemme" : "";

        var html = [];
        html.push('<div class="talkitem ' + isme + '">');
        html.push('<div class="headicon">');
        html.push('<img src="' + (userinfo[2] ? userinfo[2] : "/dx-svc/img/nohead.png") + '" />');
        html.push('</div>');
        html.push('<div class="talkinfo">');
        html.push('<span class="talkname">' + (userinfo[1] ? userinfo[1] : "路人") + '</span>');
        html.push('<div class="talkout clearfix">')
        html.push('<span class="talkmessage">' + content.Htmlencode() + '</span>');
        html.push('</div></div></div>');
        return html.join('');

    }
}

$(function () {
    var groupid = $(".classitem.sel").attr("data-id");

    var data = {};
    if (!groupid) {
//        return;
    } else {
        $(".talkallbox").show();
        data = {groupId: groupid};
    }
    $.ajax({
        url: "/dx-svc/service/im/user/conversation/info",
        dataType: 'json',
        type: "GET",
        data: data,
        success: function (data) {
            if (data.code == "0") {
                emoji.emojiInit();
                $(".J_groupname").text(data.data.groupName);
                $("#J_imGgroupId").val(data.data.imGroupId);
                var uobj = {
                    id: data.data.userId,
                    realName: data.data.imUserName || "",
                    avatarUrl: data.data.avatarUrl || ""
                };
                dx_chat.init(data.data, uobj);


            }
        },
        error: function () {
            alert("系统异常，稍后再试！");
        }
    });


    //
    //
})
