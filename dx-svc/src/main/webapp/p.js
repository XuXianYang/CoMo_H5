//p COoKPotEEDu7EpZnU3fFam9ETwx4EMR982FgQVhcAWFo9/6iajACOaIOkf8T5zTOxpHh1IwqNc0qCIL8stOLNJXUqIyZVKzx0A/U/pC1GNaEFxhSE8vqvzNujgE3adaKV4UWsnpX2RQ=
//s YCAc3mNFXHGVxYAA9UK/NG9ETwx4EMR982FgQVhcAWFo9/6iajACORKlrgRFInf0ceaS/puzrsKTn5zprFXd4dRxrRjOuRleNEHrg3eDsY7tE4KN7Ctb8CPDha6FMRkRC5Tzshwqmvc=
//t 4IZFrSQCaxFmfkzCylih1m9ETwx4EMR982FgQVhcAWFo9/6iajACObwU2I5G7zeZhuOZ2Nk+i3+RAtpMWeIMhmi2VenriLOpbi1DUvEYm6hjOrG9wGtQXg9AldgItILbm9H3zvccGNw=
// pg 04a1e46e-0361-4ae8-8df5-77aaf44292de
// sg e1288464-a636-413e-bcea-212da019878f

var demo = angular.module("demo", ["RongWebIMWidget"]);
demo.controller("main", ["$scope", "WebIMWidget", function ($scope, WebIMWidget) {
    console.log("controller:" + new Date());
    window.WebIMWidget = WebIMWidget;
    WebIMWidget.setUserInfoProvider(function (targetId, obj) {
        console.log('setUserInfoProvider: ' + targetId);
        $.ajax("service/im/user/info", { data: {"userId": 187, "imUserId": targetId}})
            .then(function(res) {
                obj.onSuccess({
                    name: res.data.imUserName,
                    userId: res.data.imUserId,
                    portraitUri: res.data.avatarUrl
                });
            });
    });

    WebIMWidget.onClose = function () {
        console.log("已关闭");
    };
}]);
function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}
$(document).ready(function() {
    $.ajax("service/im/user/conversation/info", { data: {"userId": 187, "groupId": 67}})
        .then(function(res) {
            if (0 !== res.code) {
                console.log(res.message);
            } else {
                WebIMWidget.init({
                    appkey: res.data.appKey,
                    token: res.data.token,
                    style: {
                        width: document.body.clientWidth,
                        height: document.body.clientHeight,
                        top: 0,
                        bottom: 0,
                        left: 0,
                        right: 0
                    },
                    displayConversationList:false,
                    onSuccess: function () {
                        WebIMWidget.show();
                        WebIMWidget.setConversation(3, res.data.imGroupId, res.data.groupName);
                    },
                    onError: function (error) {
                        console.log("error:" + error);
                    }
                });
            }
        });
});