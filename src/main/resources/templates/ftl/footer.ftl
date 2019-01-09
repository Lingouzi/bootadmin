<footer class="col mt-2 ">
    <div class="card text-center shadow">
        <div class="card-body">
            <h5 class="card-title">
                <a target="_blank" href=""><i class="fa fa-facebook fa-fw"></i></a>
                <a target="_blank" href="javascript:;" data-toggle="tooltip" title="@tuLongx"><i class="fa fa-weibo fa-fw"></i></a>
                <a target="_blank" href="javascript:;" data-toggle="tooltip" title="wx：ly19870316"><i class="fa fa-weixin fa-fw"></i></a>
                <a target="_blank" href="javascript:;" data-toggle="tooltip" title="QQ：664162337"><i class="fa fa-qq fa-fw"></i></a>
                <a target="_blank" href="javascript:;" data-toggle="tooltip" title="Email：664162337@qq.com"><i class="fa fa-envelope-open-o fa-fw"></i></a>
                <a target="_blank" href="https://gitlab.com/tulongx"><i class="fa fa-gitlab fa-fw"></i></a>
            </h5>
            <small>
                <i class="fa fa-flag"></i>BootAdmin 0.1 · 作者：<a target="_blank" href="https://weibo.com/tulongx/profile?rightmod=1&wvr=6&mod=personinfo">tulongx</a>
                <a target="_blank" href="http://opensource.org/licenses/mit-license.html">MIT协议</a>
                <a href="https://gitlab.com/tulongx/bootadmin.git">GitLab项目</a>
            </small>
        </div>
    </div>
</footer>
<!-- 在这里引入了jq, 其他list页面就不要重复引入了. -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.3.0/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script src="/static/plugins/lyModal/lyModal.js"></script>
<script>
    var username = '${bootAdminUser.name}';
    var sendMessage = null;
    var disConnect = null;

    connect();

    function connect() {
        var socket = new SockJS("http://localhost:8082/chat");
        var client = Stomp.over(socket);
        client.connect({
            username: username
        }, function (succ) {
            console.log('client connect success:', succ);

            // 主动订阅消息
            client.subscribe("/message/public", function (res) {
                console.log('收到消息---/message/public：', res);
                //
                let message = JSON.parse(res.body);
                let modalId = $.lyModal({
                    title: '新消息',
                    content: message.content,
                    hasConfirm: true,
                    confirmText:'知道了'
                });
            });

            // 订阅消息.
            client.subscribe("/user/notice/msg", function (res) {
                console.log('个人消息：', res)
            });
        }, function (error) {
            console.log('client connect error:', error);
        });
        sendMessage = function (destination, headers, body) {
            client.send(destination, headers, body)
        };
        disConnect = function () {
            client.disconnect();
            console.log('client connect break')
        }
    }

    function disconnect() {
        disConnect();
    }

    //发送聊天信息
    function send(roomId, ct) {
        var messageModel = {};
        messageModel.content = ct;
        messageModel.from = username;
        sendMessage("/app/hello/" + roomId, {}, JSON.stringify(messageModel));
    }

    /**
     * 测试发送一个消息，如果订阅了/sub/public的用户都会收到消息。
     */
    function send1() {
        var messageModel = {};
        messageModel.content = '你好,' + new Date().getTime();
        messageModel.from = username;
        sendMessage("/app/hello", {}, JSON.stringify(messageModel));
    }

    function send2() {
        var messageModel = {};
        messageModel.content = 'hello1,' + new Date().getTime();
        messageModel.from = username;
        sendMessage("/app/hello1", {}, JSON.stringify(messageModel));
    }

    /** 发送消息给个人，接收者 to **/
    function send3() {
        var messageModel = {};
        messageModel.to = 'jerry';
        messageModel.content = 'hello1,' + new Date().getTime();
        messageModel.from = username;
        sendMessage("/app/hello2", {}, JSON.stringify(messageModel));
    }
</script>