$(function(){
    $("#J_save").bind("click",function(){
        $.ajax({
            url: "/dx-svc/service/user/info/update",
            dataType: 'json',
            type: "POST",
            data: {"realName":$("#J_realname").val()},
            success: function (data) {
                if(data.code=="0"){
                    global_dx.message("提示",'保存成功！',1,function(){
                        location.reload();
                    })
                }
            }
        });



    })
})
