/**
 * Created by XuWenHao on 8/2/2016.
 */
$(function () {
    $('#paging-container').paging({
        currentPage: parseInt($("#currentPage").val()),
        pageCount: parseInt($("#pageCount").val()),
        onPageChange: function (page) {
            console.log('current page:', page)
            location.href = Global.getWebUrl("sysadmin/school/list?pageNum=" + page);
        }
    });
});
