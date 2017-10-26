/**
 * Created by XuWenHao on 8/2/2016.
 */
Global.getServiceUrl = function(url) {
    return Global.contextPath + "/service/" + url;
};
Global.getWebUrl = function(url) {
    return Global.contextPath + Global.servletPath + "/" + url;
};

(function ($) {
    var JST = Global.JST = {};
    JST["paging"] = function anonymous(it
                                       /**/) {
        var out='<ul class="ld-paging"> ';if(it.showLastPage){out+=' <li class="ld-paging-item ld-paging-item-last';if(it.lastPageDisabled){out+=' ld-paging-item-disabled';}out+='"> <a href="#" class="ld-paging-last-page"><b class="ld-icon ld-icon-chevron-left"></b></a> </li> ';}out+=' ';var arr1=it.pageItems;if(arr1){var item,i1=-1,l1=arr1.length-1;while(i1<l1){item=arr1[i1+=1];out+=' ';if(item.type=='page'){out+=' <li class="ld-paging-item';if(item.current){out+=' ld-paging-item-current';}out+='"><a href="#" class="ld-paging-page" data-page="'+( item.page)+'">'+( item.text)+'</a></li> ';}else{out+=' <li class="ld-paging-item ld-paging-more">•••</li> ';}out+=' ';} } out+=' ';if(it.showNextPage){out+=' <li class="ld-paging-item ld-paging-item-next';if(it.nextPageDisabled){out+=' ld-paging-item-disabled';}out+='"> <a href="#" class="ld-paging-next-page"><b class="ld-icon ld-icon-chevron-right"></b></a> </li> ';}out+='</ul>';return out;
    };
    function inArray(value, arr) {
        var i;
        for (i = 0; i < arr.length; i++) {
            if (value == arr[i]) {
                return true
            }
        }
        return false
    }
    function forEach(arr, iterator) {
        var i;
        for (i = 0; i < arr.length; i++) {
            iterator(arr[i], i)
        }
    }
    function Paging(container, options) {
        this.options = options
        this.$container = $(container)
        this.state = {
            currentPage: options.currentPage
        }
        this.render()
        this.bindEvents()
    }
    var proto = Paging.prototype

    proto._getRenderData = function(currentPage, pageCount) {
        var pages = []
        var i
        if (pageCount > 5) {
            pages.push(currentPage)
            pages.push(currentPage - 1)
            pages.push(currentPage - 2)
            pages.push(currentPage + 1)
            pages.push(currentPage + 2)

            if (currentPage <= 2) {
                pages.push(4)
                pages.push(5)
            }

            if (currentPage  > pageCount - 2) {
                pages.push(pageCount - 3)
                pages.push(pageCount - 4)
            }


            if (this.options.hideEndPage) {
                pages.push(1, 2)
            } else {
                pages.push(1, 2, pageCount)
            }

            var nextPages = []

            $.each(pages, function(index, page) {
                if (page > 0 && page <= pageCount) {
                    nextPages.push(page)
                }
            })

            pages = nextPages
            nextPages = []

            forEach(pages, function(page){
                if(!inArray(page, nextPages)) {
                    nextPages.push(page)
                }
            })

            pages = nextPages.sort(function (prev, next) {
                if (prev > next) {
                    return 1
                } else if (prev < next) {
                    return -1
                } else {
                    return 0
                }
            })

            nextPages = []

            forEach(pages, function (page, index) {
                if (index > 0 && pages[index - 1] !== '...' && page > pages[index - 1] + 1) {
                    nextPages.push('...', page)
                } else {
                    nextPages.push(page)
                }
            })

            pages = nextPages

            // 如果不显示最后一页，且没翻到最后那几页，就补充一个省略号
            if (this.options.hideEndPage && pages[pages.length - 1] != pageCount) {
                pages.push('...')
            }

        } else if (pageCount > 0){
            for (i = 0; i < pageCount; i++) {
                pages.push(i + 1)
            }
        }
        return pages
    }

    proto.getRenderData = function () {
        var renderData = {
            showLastPage: true,
            showNextPage: true,
            pageItems: []
        }
        var i
        var currentPage = this.state.currentPage || 1
        var pageCount = this.options.pageCount || 0

        if (currentPage < 0) {
            currentPage  = 1
        }
        if (currentPage > pageCount) {
            currentPage = pageCount
        }

        if (currentPage == 1) {
            renderData.lastPageDisabled = true
        }
        if (currentPage == pageCount) {
            renderData.nextPageDisabled = true
        }
        if (pageCount <=1) {
            renderData.showLastPage = false
            renderData.showNextPage = false
        }

        var pages = this._getRenderData(currentPage, pageCount)
        var page
        for (i = 0; i < pages.length; i++) {
            page = pages[i]
            if (page == '...') {
                renderData.pageItems.push({type: 'more'})
            } else if (page == currentPage) {
                renderData.pageItems.push({
                    type: 'page',
                    page: page,
                    text: page,
                    current: true
                })
            } else {
                renderData.pageItems.push({
                    type: 'page',
                    page: page,
                    text: page
                })
            }
        }
        return renderData
    }

    proto.render = function () {
        this.$container.html(Global.JST['paging'](this.getRenderData()))
    }

    proto.bindEvents = function () {
        var self = this
        this.$container.on('click.ldPaging', '.ld-paging-next-page', function (evt) {
            evt.preventDefault()
            self.nextPage()
        })
        this.$container.on('click.ldPaging', '.ld-paging-last-page', function (evt) {
            evt.preventDefault()
            self.lastPage()
        })
        this.$container.on('click.ldPaging', '.ld-paging-page', function (evt) {
            evt.preventDefault()
            self.setPage(parseInt($(this).attr('data-page'), 10))
        })
    }

    proto.nextPage = function () {
        if (this.state.currentPage + 1 <= this.options.pageCount) {
            this.state.currentPage += 1
            this.options.onPageChange(this.state.currentPage)
            this.render()
        }
    }

    proto.lastPage  = function () {
        if (this.state.currentPage - 1 > 0) {
            this.state.currentPage -= 1
            this.options.onPageChange(this.state.currentPage)
            this.render()
        }
    }

    proto.setPage  = function (page) {
        if (0 < page && page <= this.options.pageCount) {
            this.state.currentPage = page
            this.options.onPageChange(this.state.currentPage)
            this.render()
        }
    }

    $.fn.paging = function (options) {
        var args = Array.prototype.slice.call(arguments)
        var method = args.shift() || {}
        options = $.extend({
            currentPage: 1,
            pageCount: 5,
            // 就算是1页也显示分页
            alwaysShow: false,
            hideEndPage: false,
            onPageChange: function () {}
        }, options || {})

        options.currentPage = parseInt(options.currentPage, 10)
        options.pageCount = parseInt(options.pageCount, 10)

        // 如果总页数小于等于1页，默认不显示分页
        if (options.pageCount <= 1 && !options.alwaysShow) {
            return
        }

        return this.each(function () {
            var $this = $(this)
            var data = $this.data('ld.paging')
            if (!data) {
                $this.data('ld.paging', (data = new Paging(this, options)))
            }
            if (typeof method == 'string') {
                data[method].apply(data, args)
            }
        })
    }
})(jQuery);
