let FileInput;
$(function () {
    'use strict';
    /**
     * 文件上传
     * @constructor
     */
    FileInput = function () {
        let oFile = {};
        //初始化fileinput控件（第一次初始化）
        oFile.Init = function (ctrlName, uploadUrl) {
            let control = $('#' + ctrlName);
            //初始化上传控件的样式
            control.fileinput({
                language: 'zh', //设置语言
                uploadUrl: uploadUrl, //上传的地址
                allowedFileExtensions: ['jpg', 'gif', 'png'],//接收的文件后缀
                showUpload: true, //是否显示上传按钮
                showCaption: false,//是否显示标题
                browseClass: "btn btn-sm btn-primary", //按钮样式
                //dropZoneEnabled: false,//是否显示拖拽区域
                //minImageWidth: 50, //图片的最小宽度
                //minImageHeight: 50,//图片的最小高度
                //maxImageWidth: 1000,//图片的最大宽度
                uploadAsync: true,//采用同步上传,否则是无序的异步上传，依据需求修改
                removeFromPreviewOnError: true,//当文件不符合规则，就不显示预览
                //maxImageHeight: 1000,//图片的最大高度
                maxFileSize: 1024 * 3,//单位为kb，如果为0表示不限制文件大小
                //minFileCount: 0,
                maxFileCount: 10, //表示允许同时上传的最大文件个数
                enctype: 'multipart/form-data',
                validateInitialCount: true,
                previewFileIcon: "<i class='fa fa-search'></i>",
                msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
                uploadExtraData: function (previewId, index) {   //额外参数 返回json数组
                    return {
                        'test': 'test'
                    };
                }
            }).on("fileuploaded", function (event, data) {	//当上传成功回调函数
                var response = data.response;
            });
        };
        return oFile;
    };

    /** 控制form数据格式化为json格式,方便传递参数 */
    $.fn.serializeObject = function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [ o[this.name] ];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    }
});

/**
 参数解释：
 @param title           标题
 @param url             请求的url
 @param isFullScreen    弹出全屏
 @param w       弹出层宽度（缺省调默认值）
 @param h       弹出层高度（缺省调默认值）
 */
function openPage(title, url, isFullScreen, w, h) {
    /* 判断是否是手机 */
    if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
        isFullScreen = true;
    }
    if (title == null || title === '') {
        title = false;
    }
    if (url == null || url === '') {
        url = "404.html";
    }
    if (w == null) {
        w = 800;
    } else if (typeof  w === 'string') {
        w = parseInt(w);
    }
    if (h == null) {
        h = ($(window).height() - 50);
    } else if (typeof h === 'string') {
        h = parseInt(h);
    }
    var index = layer.open({
        type: 2,
        skin: 'layui-layer-rim',
        fix: true, /*不固定*/
        shadeClose: true, /*点击遮罩关闭页面*/
        area: [w + 'px', h + 'px'],
        maxmin: false, /* 是否允许最小化 */
        shade: 0.4,
        title: title,
        content: url
    });
    if (isFullScreen) {
        layer.full(index);
    }
}

/**关闭弹出框口*/
function closePage(msg) {
    if (msg != null) {
        parent.layui.layer.msg(msg);
    }
    let index = parent.layui.layer.getFrameIndex(window.name);
    parent.layui.layer.close(index);
}

/**
 * 统一接口调用
 * @param api
 * @param params
 * @param callback
 */
function request(api, params, callback) {
    $.post(api, params, function (d) {
        if (callback !== null && typeof callback === 'function') {
            callback(d);
        } else {
            layer.msg(d.msg);
        }
    }, "json");
}

/**
 * 修改单个字段
 * @param uri      请求地址
 * @param id       编辑的对象的 id
 * @param field    编辑的字段
 * @param value    字段的值, 传递到后台会进行url编码
 * @param callback 回调函数
 *
 */
function edit(uri, id, field, value, callback) {
    $.post(uri, {id: id, field: field.trim(), value: (value === null ? null : encodeURI(value))}, function (d) {
        /* 如果最后一个参数是自定义的回调, 就回调callback, 否则执行默认的 方法.*/
        if (callback !== null && typeof callback === 'function') {
            callback(d);
        } else {
            if (d.code === 0) {
                layer.msg(d.msg);
            } else {
                layer.msg('success');
            }
        }
    }, "json");
}