$(function(){
    $(".J_changeClass").bind("change",function(){
        var classid = $(this).val();
        location.href = "safe?classid="+classid;
    })
})