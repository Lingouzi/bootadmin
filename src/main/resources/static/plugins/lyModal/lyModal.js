;(function ($, window, document, undefined) {
    "use strict";
    var Beautifier = function (ele, opt) {
        this.$element = ele,
            this.defaults = {
                /**标题*/
                title: '提示',
                /**内容*/
                content: '',
                /** 是否有关闭按钮 */
                hasCancel: false,
                cancelText: '关闭',
                /** 确认按钮 */
                hasConfirm: false,
                confirmText: '确定',
                /** 按下确认按钮 */
                onConfirm: null,
                /** 是否剧中显示弹窗 */
                dialogCenter: true,
                /** 框的大小,0:默认,1:大号,2:小号: */
                size: 2,
                /** 点击遮罩是否关闭窗口, true,false或者static:显示灰色遮罩 */
                backdrop: 'static',
                /** 按下esc时关闭动态视窗。 */
                keyboard: false,
                /** 远程调用内容 */
                remote: '',
                /** iframe时生效 内容区域的高度,不包含标题,标题默认1em */
                height: 400,
                /** 宽度,可以自己设定,也可以用默认的size,两个互斥. */
                width: 0,
                /** 成功展示弹窗之后的操作 */
                onShow: null,
                /** modal销毁的时候回调 */
                onDestroy:null,
                /** 提示框3s后自动关闭 */
                time: 3,
                /** 提示框是否自动关闭 */
                autoClose: true
            },
            this.options = $.extend({}, this.defaults, opt);
    };
    Beautifier.prototype = {
        beautify: function () {
            let id = 'ly-Modal-' + new Date().getTime();
            let c = this.options.onConfirm;
            let s = this.options.onShow;
            let onDestroy = this.options.onDestroy;
            let w = this.options.width;
            let h = this.options.height;
            let hcc = this.options.hasCancel;
            let hcm = this.options.hasConfirm;
            let time = this.options.time;
            let autoClose = this.options.autoClose;
            let html;
            let ctx;
            /** 1. 是否是远程调用的*/
            if (this.options.remote) {
                /** 远程调用的是不包含按钮和中间底部内容的,会在content中添加一个iframe. */
                ctx = '<iframe scrolling="auto" style="width:100%;' + (h ? 'height:' + h + 'px;' : '') + 'border-bottom-left-radius: .3rem;border-bottom-right-radius: .3rem;" frameborder="0" allowtransparency="true" src="' + this.options.remote + '"></iframe>';
            } else if (hcc || hcm) {
                let cancelBtn = '<button type="button" class="btn btn-sm btn-secondary" data-dismiss="modal">' + this.options.cancelText + '</button>';
                let confirmBtn = '<button type="button" class="btn btn-sm btn-primary modal-btn-ok-' + id + '" >' + this.options.confirmText + '</button>';
                ctx = '<div class="modal-body">' + this.options.content + '</div>' +
                    '<div class="modal-footer">' + (hcc ? cancelBtn : '') + (hcm ? confirmBtn : '') + '</div>';
            }else{
                ctx = '<div class="modal-body">' + this.options.content + '</div>';
            }

            /** 是否自定义宽度了, 如果size和width同时存在,那么以width为准,size置空 */
            let p = (this.options.size === 0 ? '' : (this.options.size === 1 ? 'modal-lg' : (this.options.size === 2 ? 'modal-sm' : '')));
            let p1 = ((w && w > 0) ? ('style="max-width:' + w + 'px"') : '');
            if (p1) {
                p = '';
            }
            html = '<div class="modal fade" id="' + id + '" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">' +
                '  <div class="modal-dialog ' + p + ' ' + (this.options.dialogCenter ? 'modal-dialog-centered' : '') + '" ' + p1 + ' role="document">' +
                '    <div class="modal-content">' +
                '      <div class="modal-header">' +
                '        <h5 class="modal-title" id="exampleModalLabel">' + this.options.title + '</h5>' +
                '        <button type="button" class="close" data-dismiss="modal" aria-label="Close">' +
                '          <span aria-hidden="true">&times;</span>' +
                '        </button>' +
                '      </div>' + ctx +
                '    </div>' +
                '  </div>' +
                '</div>';
            $("body").append(html);
            let $modal = $("#" + id);
            $modal.modal({
                keyboard: this.options.keyboard,
                backdrop: this.options.backdrop
            }).on('shown.bs.modal', function (e) {
                if (s && typeof s === 'function') {
                    /** 如果有成功回调那么就进行回调处理 */
                    s(e);
                }
                if (!hcm && !hcc) {
                    /** 如果没有显示确认按钮那么就是临时提示,过一定时间自动关闭此modal */
                    if(autoClose){
                        setTimeout(function () {
                            $modal.modal('hide');
                        }, time * 1000);
                    }
                }
            }).on('hidden.bs.modal', function (e) {
                /** 关闭之后,删除此窗口 */
                if(onDestroy && typeof onDestroy === 'function'){
                    onDestroy(e);
                }
                $modal.remove();
            });
            $('.modal-btn-ok-' + id).unbind('click').click(function (e) {
                /** 传递点击事件到回调函数. */
                if (c && typeof c === 'function') {
                    c(e);
                }
                $modal.modal('hide');
            });
            return id;
        }
    };
    $.lyModal = function (options) {
        let beautifier = new Beautifier(this, options);
        return beautifier.beautify();
    }
})(jQuery, window, document);