<!-- ###########  start::控制隐藏侧边栏  ########### -->
<a id="show-sidebar" class="btn btn-sm btn-dark" href="#">
    <i class="fa fa-bars fa-fw"></i>
</a>
<!-- ###########  end::控制隐藏侧边栏  ########### -->
<!-- ###########  start::左侧栏  ########### -->
<nav id="sidebar" class="sidebar-wrapper">
    <div class="sidebar-content">
        <div class="sidebar-brand">
            <a href="#">BootAdmin</a>
            <div id="close-sidebar">
                <i class="fa fa-times fa-fw"></i>
            </div>
        </div>
        <div class="sidebar-header">
            <div class="user-pic">
                <img class="img-responsive img-rounded" src="/static/img/user.jpg" alt="User picture">
            </div>
            <div class="user-info">
                <span class="user-name">${bootAdminUser.nick}</span>
                <span class="user-role">管理员</span>
                <span class="user-status">
                        <i class="fa fa-circle fa-fw"></i>
                        <span>Online</span>
                    </span>
            </div>
        </div>
        <div class="sidebar-menu">
            <ul>
                <li class="sidebar-dropdown">
                    <a href="#">
                        <i class="fa fa-cog fa-fw"></i>
                        <span>系统设置</span>
                    </a>
                    <div class="sidebar-submenu">
                        <ul>
                            <@shiro.hasPermission name="/sysuser/list">
                            <li>
                                <a href="/sysuser/list">用户管理</a>
                            </li>
                            </@shiro.hasPermission>
                            <@shiro.hasPermission name="/sysrole/list">
                            <li>
                                <a href="/sysrole/list">角色管理</a>
                            </li>
                            </@shiro.hasPermission>
                            <@shiro.hasPermission name="/privilege/list">
                            <li>
                                <a href="/privilege/list">权限管理</a>
                            </li>
                            </@shiro.hasPermission>
                        </ul>
                    </div>
                </li>
            </ul>
        </div>
    </div>
    <div class="sidebar-footer">
        <div>
            <a href="/logout">
                <i class="fa fa-power-off"></i>
            </a>
        </div>
    </div>
</nav>