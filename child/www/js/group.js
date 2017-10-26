$(function () {
    var dx_group = {

        init: function () {
            var that = this;
            that.getData();
        },
        getData: function () {
            var that = this;
            $.ajax({
                url: dxutil.baseUrl + "/dx-svc/service/im/user/group/list",
                dataType: 'json',
                type: "GET",
                data: dxutil.addSid({}),
                success: function (data) {
                    $.hideIndicator();
                    if (data.code == "0") {
                        that.renderGrouplist(data.data);
                        that.addNewMessageTips(data.data);
                        that.syncGroupMessage(data.data);
                    } else if (data.code == "4003") {
                        dxutil.noLogin();
                    } else {
                        $.toast(data.message);
                    }

                },
                error: function () {
                    $.hideIndicator();
                }
            });

        },
        renderGrouplist: function (list) {
            var that = this;
            var html = [];
            for (var i = 0; i < list.length; i++) {
                html.push('<li>');
                html.push('<a href="talk.html?#' + list[i].id + '" external >');
                html.push('<div class="item-content">');
                html.push('<div class="item-media"><img src="./img/groupicon.png" style="width: 2.2rem;">');
                html.push('<span class="has_new_group_message" id="' + list[i].imGroupId + '">.</span>');
                html.push('</div>');
                html.push('<div class="item-inner">');
                html.push('<div class="item-title-row">');
                html.push('<div class="item-title">' + list[i].name + '</div>');
                html.push('</div>');
                html.push('<div class="item-subtitle"></div>');
                html.push('</div>');
                html.push('</div>');
                html.push('</a>');
                html.push('</li>');

            }
            $(".J_group").html(html.join(''));
        }, /**
         * 标记
         * @param list
         */
        addNewMessageTips: function (list) {
            for (var i = 0; i < list.length; i++) {
                var key_for_new = "has_new_" + list[i].imGroupId;
                var blockStyle = "none";
                if ("true" == localStorage.getItem(key_for_new))
                    blockStyle = "block";

                $("#" + list[i].imGroupId).css("display", blockStyle);
            }
        },
        recordLocalMessage: function (message) {
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

            localStorage.setItem(key_for_new, true);
        },
        resizeLocalMessageLength: function (len, item) {
            var that = this;
            while (item.length > len) {
                item.shift();
            }
        },
        syncGroupMessage: function (list) {
            var that = this;
            $.ajax({
                url: dxutil.baseUrl + "/dx-svc/service/im/user/conversation/info",
                dataType: 'json',
                type: "GET",
                data: dxutil.addSid({}),
                success: function (data) {
                    $.hideIndicator();
                    if (data.code == "0") {
                        var uobj = {
                            id: data.data.userId,
                            realName: data.data.imUserName || "",
                            avatarUrl: data.data.avatarUrl || ""
                        };
                        RongIMClient.init(data.data.appKey);

                        RongIMClient.setConnectionStatusListener({
                            onChanged: function (status) {
                                console.log(status);
                            }});

                        // 消息监听器
                        RongIMClient.setOnReceiveMessageListener({
                            // 接收到的消息
                            onReceived: function (message) {
                                // 判断消息类型
                                that.recordLocalMessage(message);

                                that.addNewMessageTips(list);
                            }
                        });
                        RongIMClient.connect(data.data.token, {
                            onSuccess: function (userId) {
                                console.log("connect success. current user is : " + userId);
                            },
                            onTokenIncorrect: function () {
                                console.log('无效的用户');
                            },
                            onError: function (errorCode) {
                                console.log("connect error:" + errorCode);
                            }
                        });

                    }

                },
                error: function () {
                    $.hideIndicator();
                    console.log("error in get user info");
                }
            });


        }

    };
    dx_group.init();
});
