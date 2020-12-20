const AjaxUtil = {
    /**
     * 保证值不为空，如果为空则返回 alter
     * @param sour 被校验的值
     * @param alter sour为空时的替代品
     */
    alterEmpty: function (sour, alter) {
        if ($.isFunction(sour) || $.isPlainObject(sour)) {
            return sour;
        }
        if (sour == null || sour.length === 0) {
            return alter;
        }
        return sour;
    },
    /**
     * 带token请求头的ajax
     * @param option
     */
    ajaxWithToken: function (option) {
        option = option || {};
        option.async = AjaxUtil.alterEmpty(option.async, true);
        option.url = AjaxUtil.alterEmpty(option.url, "/");
        /*
         * 请求方式 ("POST" 或 "GET")， 默认为 "GET"。
         * 注意：其它 HTTP 请求方法，如 PUT 和 DELETE 也可以使用，但仅部分浏览器支持。
         */
        option.type = AjaxUtil.alterEmpty(option.type, "GET");
        /*
         * 要求为String类型的参数，当发送信息至服务器时，内容编码类型默认为"application/x-www-form-urlencoded"。
         * 该默认值适合大多数应用场合。
         */
        option.contentType = AjaxUtil.alterEmpty(option.contentType, "application/x-www-form-urlencoded");
        /*
         * 要求为String类型的参数，预期服务器返回的数据类型。如果不指定，JQuery将自动根据http包mime信息返回responseXML或responseText，并作为回调函数参数传递。可用的类型如下：
         *  xml：返回XML文档，可用JQuery处理。
         *  html：返回纯文本HTML信息；包含的script标签会在插入DOM时执行。
         *  script：返回纯文本JavaScript代码。不会自动缓存结果。除非设置了cache参数。注意在远程请求时（不在同一个域下），所有post请求都将转为get请求。
         *  json：返回JSON数据。
         *  jsonp：JSONP格式。使用SONP形式调用函数时，例如myurl?callback=?，JQuery将自动替换后一个“?”为正确的函数名，以执行回调函数。
         *  text：返回纯文本字符串。
         */
        option.dataType = AjaxUtil.alterEmpty(option.dataType, null);
        option.data = AjaxUtil.alterEmpty(option.data, {});
        /*
         * 要求为Boolean类型的参数，默认为true。
         * 默认情况下，发送的数据将被转换为对象（从技术角度来讲并非字符串）以配合默认内容类型"application/x-www-form-urlencoded"。
         * 如果要发送DOM树信息或者其他不希望转换的信息，请设置为false。
         */
        option.processData = AjaxUtil.alterEmpty(option.processData, true);
        /*
         * context: 这个对象用于设置 Ajax 相关回调函数的上下文。
         * 也就是说，让回调函数内 this 指向这个对象（如果不设定这个参数，那么 this 就指向调用本次 AJAX 请求时传递的 options 参数）。
         * 比如指定一个 DOM 元素作为 context 参数，这样就设置了 success 回调函数的上下文为这个 DOM 元素。
         */
        option.context = AjaxUtil.alterEmpty(option.context, null);
        option.timeout = AjaxUtil.alterEmpty(option.timeout, 60000);
        /*
         * 在发送请求之前调用，并且传入一个 XMLHttpRequest 作为参数。
         */
        option.beforeSend = AjaxUtil.alterEmpty(option.beforeSend, function (xhr) {
        });
        /*
         * 当请求完成之后调用这个函数，无论成功或失败。
         * 传入 XMLHttpRequest 对象，以及一个包含成功或错误代码的字符串。
         */
        option.complete = AjaxUtil.alterEmpty(option.complete, function (xhr, ts) {
        });
        /*
         * 当请求之后调用。传入返回后的数据，以及包含成功代码的字符串。
         */
        option.success = AjaxUtil.alterEmpty(option.success, function (data) {
        });
        /*
         * 在请求出错时调用。传入 XMLHttpRequest 对象，描述错误类型的字符串以及一个异常对象（如果有的话）
         */
        option.error = AjaxUtil.alterEmpty(option.error, function (req, msg, obj) {
        });

        $.ajax({
            async: option.async,
            url: option.url,
            type: option.type,
            data: option.data,
            contentType: option.contentType,
            dataType: option.dataType,
            timeout: option.timeout,
            processData: option.processData,
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

        option = AjaxUtil.alterEmpty(option, {});
        option.url = AjaxUtil.alterEmpty(option.url, "/");
        option.method = AjaxUtil.alterEmpty(option.method, 'POST');
        option.data = AjaxUtil.alterEmpty(option.data, {});

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
        option = AjaxUtil.alterEmpty(option, {});
        option.name = AjaxUtil.alterEmpty(option.name, '');
        option.url = '/downloadV2/' + option.name;

        option.url += '?token=' + localStorage.getItem('token');

        option.url += '&path=' + option.path;

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
        AjaxUtil.ajaxWithToken({
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

        AjaxUtil.ajaxAsyncPostJsonWithToken(url, objJson, successCallback, errorCallback);
    },
    /**
     * 分片上传
     * @param options
     */
    uploadFileSplitWithToken: function (options) {
        let conf = {
            /*
             * 必填：当前所在位置，绝对路径
             */
            position: options.position,
            /*
             * 必填：上传文件，单个
             */
            file: options.file,
            /*
             * 必填：上传成功时回调；
             */
            uploadFinish: AjaxUtil.alterEmpty(options.uploadFinish, function () {
                alert('传输完毕！');
            }),
            /*
             * 必填：上传失败时回调
             */
            uploadError: AjaxUtil.alterEmpty(options.uploadError, function () {
                alert('传输错误！');
            }),
            /*
             * 非必填：进度条对象，使用 processBar 进行初始化
             */
            processBarDom: options.processBarDom,
            skip: AjaxUtil.alterEmpty(options.skip, 0),
            blockSize: AjaxUtil.alterEmpty(options.blockSize, (1024 * 1024)),
            index: Number(AjaxUtil.alterEmpty(options.index, 0)) + 1
        };
        console.log('conf: ', conf);
        console.log('processBarDom: ', options.processBarDom);
        // 初始化一个FormData对象
        let formData = new FormData();
        // 读取到结束位置
        let nextSize = Math.min((conf.skip + 1) * conf.blockSize, conf.file.size);
        // 截取 部分文件 块
        let fileData = conf.file.slice(conf.skip * conf.blockSize, nextSize);
        // 将 部分文件 塞入FormData
        formData.append("skip", conf.skip);

        formData.append("uploadFile", fileData);
        // 保存位置
        formData.append("position", conf.position);
        // 保存文件名字
        formData.append("fileName", conf.file.name);
        // 是否为最后一个片段
        formData.append("isLastSnippet", String(conf.file.size <= nextSize));

        AjaxUtil.ajaxWithToken({
            url: "/uploadBySplit",
            type: "POST",
            data: formData,
            async: true,
            processData: false,  // 告诉jQuery不要去处理发送的数据
            contentType: false,   // 告诉jQuery不要去设置Content-Type请求头
            success: function (responseText) {
                if (responseText.statusCode === 200) {
                    if (conf.file.size <= nextSize) {//如果上传完成，则跳出继续上传
                        if (conf.processBarDom) {
                            conf.processBarDom.finish();
                        }
                        /*
                         * 回调上传完毕的处理方法
                         */
                        conf.uploadFinish(responseText);
                        return;
                    }

                    // console.log("已经上传了" + (conf.skip + 1) + "块文件: ", (conf.skip + 1) * conf.blockSize);
                    if (conf.processBarDom) {
                        conf.processBarDom.updateDelay((conf.skip + 1) * conf.blockSize);
                    }
                    conf.skip++;
                    // 递归调用上传
                    AjaxUtil.uploadFileSplitWithToken(conf);
                } else {
                    /*
                     * 回调上传失败的处理方法
                     */
                    conf.uploadError(responseText);
                }
            },
            error: function (req, msg, obj) {
                console.log('AjaxUtil.uploadFileSplitWithToken failed! req: ', req, ' msg: ', msg, ' obj: ', obj);
            }
        });
    },
    /**
     * 进度条工具
     */
    processBar: {
        oldDom: null,
        dom: null,
        timestamp: 0,
        conf: {
            delay: 1000, // ms
            domId: null,
            max: null,
            min: null,
            now: null,
        },
        /**
         * 初始化进度条
         * @param options 进度条配置
         * @returns {AjaxUtil.processBar}
         */
        initProcess: function (options) {
            this.conf.domId = options.dom;
            this.conf.max = AjaxUtil.alterEmpty(options.max, 100);
            this.conf.min = AjaxUtil.alterEmpty(options.min, 0);
            this.conf.now = AjaxUtil.alterEmpty(options.now, 0);

            let percent = this.conf.now * 1.0 * 100 / this.conf.max;

            this.oldDom = $('#' + this.conf.domId);

            let template = '<div class="progress" id="' + this.conf.domId + '">\n' +
                '                        <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="' + this.conf.now + '" aria-valuemin="0" aria-valuemax="' + this.conf.max + '" style="min-width: 3em;width: ' + percent + '%">\n' +
                '                            <span class="sr-only">' + percent + '% Complete</span>\n' +
                '                            <span>' + percent + '%</span>' +
                '                        </div>\n' +
                '                    </div>';

            $('#' + this.conf.domId).replaceWith(template);

            this.dom = $('#' + this.conf.domId);

            return this;
        },
        /**
         * 更新进度条（立即）
         * @param now 当前进度
         */
        update: function (now) {
            let percent = now * 100.0 / this.conf.max;
            percent = percent.toFixed(2);
            // console.log(percent);
            console.log('update this.dom: ', this.dom);
            let subDom = this.dom.find("div[role=progressbar]").eq(0);
            console.log('update subDom: ', subDom);
            // console.log(subDom);
            subDom.css('width', percent + "%");
            subDom.attr('aria-valuenow', now);
            subDom.find('span').eq(0).text(percent + "% Complete");
            subDom.find('span').eq(1).text(percent + '%');
        },
        /**
         * 标记完成
         */
        finish: function () {
            this.update(this.conf.max);
        },
        /**
         * 更新进度条（延迟）
         * @param now 当前进度
         */
        updateDelay: function (now) {
            let time = new Date().getTime();
            if (time - this.timestamp >= this.conf.delay) {
                this.timestamp = time;
                console.log('update process bar: ' + now);
                this.update(now);
            }
        },
        destroy: function () {
            this.dom.replaceWith(this.oldDom);
        }
    },
};