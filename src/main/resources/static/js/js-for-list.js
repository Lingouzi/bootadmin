$(function () {
    'use strict';
    /*获取到当前的url，然后展开左侧列表*/
    /* 得到所有的li */
    $(".sidebar-submenu li").each(function () {
        if ($(this).find("a").attr("href") === window.location.pathname) {
            $(this).find("a").addClass("active");
            $(this).addClass("active").parent().parent().show().parent().addClass("active");
        }
    });

    // Dropdown menu
    $(".sidebar-dropdown > a").click(function () {
        $(".sidebar-submenu").slideUp(200);
        if ($(this).parent().hasClass("active")) {
            $(".sidebar-dropdown").removeClass("active");
            $(this).parent().removeClass("active");
        } else {
            $(".sidebar-dropdown").removeClass("active");
            $(this).next(".sidebar-submenu").slideDown(200);
            $(this).parent().addClass("active");
        }

    });

    // close sidebar
    $("#close-sidebar").click(function () {
        $(".page-wrapper").removeClass("toggled");
    });

    //show sidebar
    $("#show-sidebar").click(function () {
        $(".page-wrapper").addClass("toggled");
    });

    /* 控制滚动条样式 */
    if (!/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
        $(".sidebar-content").mCustomScrollbar({
            axis: "y",
            autoHideScrollbar: true,
            scrollInertia: 300
        });
        $(".sidebar-content").addClass("desktop");
    }
    /* 控制窗口左侧是否显示 */
    minimizeMenu();
    /* 控制全局tooltip, 主要是底部的footer中弹出提示 */
    $('[data-toggle="tooltip"]').tooltip();
});

$(window).resize(function () {
    minimizeMenu();
});

function minimizeMenu() {
    if (!/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
        $(".page-wrapper").addClass("toggled");
    } else {
        $(".page-wrapper").removeClass("toggled");
    }
}