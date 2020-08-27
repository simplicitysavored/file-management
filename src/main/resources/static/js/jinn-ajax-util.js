const AjaxUtil = {
    /**
     * 带token请求头的ajax
     * @param option
     */
    ajaxWithToken: function (option) {
        option = option || {};
        option.async = option.async || true;
        option.url = option.url || "";
        /*
         * 请求方式 ("POST" 或 "GET")， 默认为 "GET"。
         * 注意：其它 HTTP 请求方法，如 PUT 和 DELETE 也可以使用，但仅部分浏览器支持。
         */
        option.type = option.type || "GET";
        option.contentType = option.contentType || "application/x-www-form-urlencoded";
        option.dataType = option.dataType || 'json';
        option.data = option.data || {};
        /*
         * context: 这个对象用于设置 Ajax 相关回调函数的上下文。
         * 也就是说，让回调函数内 this 指向这个对象（如果不设定这个参数，那么 this 就指向调用本次 AJAX 请求时传递的 options 参数）。
         * 比如指定一个 DOM 元素作为 context 参数，这样就设置了 success 回调函数的上下文为这个 DOM 元素。
         */
        option.context = option.context || null;
        option.timeout = option.timeout || 60000;
        /*
         * 在发送请求之前调用，并且传入一个 XMLHttpRequest 作为参数。
         */
        option.beforeSend = option.beforeSend || function (xhr) {
        };
        /*
         * 当请求完成之后调用这个函数，无论成功或失败。
         * 传入 XMLHttpRequest 对象，以及一个包含成功或错误代码的字符串。
         */
        option.complete = option.complete || function (xhr, ts) {
        };
        /*
         * 当请求之后调用。传入返回后的数据，以及包含成功代码的字符串。
         */
        option.success = option.success || function (data) {
        };
        /*
         * 在请求出错时调用。传入 XMLHttpRequest 对象，描述错误类型的字符串以及一个异常对象（如果有的话）
         */
        option.error = option.error || function (req, msg, obj) {
        };

        $.ajax({
            async: option.async,
            url: option.url,
            type: option.type,
            dataType: option.dataType,
            data: option.data,
            timeout: option.timeout,
            beforeSend: function (xhr) {
                xhr.setRequestHeader('token', localStorage.getItem('token'));
                option.beforeSend(xhr);
            },
            complete: function (xhr, ts) {
                option.complete(xhr, ts);
            },
            success: function (data) {
                option.success(data);
            },
            error: function (req, msg, obj) {
                option.error(req, msg, obj);
            }
        })
    },
    /**
     * 跳转页面
     * @param option
     */
    jumpPageWithToken: function (option) {
        if (!$.isPlainObject(option)) {
            let url = option;
            option = {url: url};
        }

        option = option || {};
        option.url = option.url || "/";
        option.method = option.method || "get";
        option.data = option.data || {};

        let jumpFormHtml = '<form id="jumpPageForm" action="' + option.url + '" method="' + option.method + '" style="display: none;">\n';
        jumpFormHtml += '<input type="hidden" name="token" value="' + localStorage.getItem('token') + '" />\n';
        for (let dataKey in option.data) {
            jumpFormHtml += '<input type="hidden" name="' + dataKey + '" value="' + option.data[dataKey] + '" />\n';
        }
        jumpFormHtml += '</form>';

        $('body').append(jumpFormHtml);
        $('#jumpPageForm').submit();
    },
    /**
     *
     * @param option
     * {
     *     "name": "" // 文件名
     *     "path": "" // 绝对路径
     * }
     */
    downloadWithToken: function (option) {
        option = option || {};
        option.name = option.name || '';
        option.url = '/downloadV2/' + option.name;

        option.url += '?token=' + localStorage.getItem('token');

        option.url += '&path='+option.path;

        window.open(option.url, "_blank");
    },
    /**
     * 异步Ajax请求
     * @param url 请求路径
     * @param params 请求参数
     * @param successCallback 成功响应回调方法
     * @param errorCallback 异常处理
     */
    ajaxAsyncPostJsonWithToken: function (url, params, successCallback, errorCallback) {
        this.ajaxWithToken({
            async: true,
            url: url,
            type: 'POST',
            dataType: 'json',
            data: params,
            success: function (data) {
                if (successCallback) {
                    successCallback(data);
                }
            },
            error: function (req, msg, obj) {
                if (errorCallback) {
                    errorCallback(req, msg, obj);
                }
            }
        });
    },
    postFormWithToken: function (url, formId, successCallback, errorCallback) {
        let $FORM = $('#' + formId);
        const formArray = $FORM.serializeArray();
        let objJson = {};
        for (let i = 0; i < formArray.length; i++) {
            let formObj = formArray[i];
            const $DOM_INP = $FORM.find('[name="' + formObj.name + '"]');
            let domLength = $DOM_INP.length;
            if (domLength === 1 || $DOM_INP.attr('type') === 'radio') {
                objJson[formObj.name] = formObj.value;
            } else {
                if (objJson[formObj.name]) {
                    objJson[formObj.name].push(formObj.value);
                } else {
                    objJson[formObj.name] = [formObj.value];
                }
            }
        }

        this.ajaxAsyncPostJsonWithToken(url, objJson, successCallback, errorCallback);
    }
};