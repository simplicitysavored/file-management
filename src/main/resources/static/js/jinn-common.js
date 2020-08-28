const YJ_COMM = {
    /**
     * 默认Ajax配置
     */
    defaultAjaxOption: {
        url: null,
        async: true,
        type: 'POST',
        dataType: 'json', // 响应数据格式
        data: null, // 请求参数
        timeout: 30000, // 超时时间（毫秒）
        successFnc: function (data) {
            // data: 成功响应信息
        },
        errorFnc: function (req, msg, obj) {
            /*
             * req: XMLHttpRequest 对象
             * msg: 错误信息
             * obj: 捕获的异常对象
             */
        }

    },
    /**
     * 异步Ajax请求
     * @param option
     */
    ajaxAsyncPostJson: function (option) {
        option = option ? option : YJ_COMM.defaultAjaxOption;
        $.ajax({
            async: option.async ? option.async : YJ_COMM.defaultAjaxOption.async,
            url: option.url ? option.url : YJ_COMM.defaultAjaxOption.url,
            type: option.type ? option.type : YJ_COMM.defaultAjaxOption.type,
            dataType: option.dataType ? option.dataType : YJ_COMM.defaultAjaxOption.dataType,
            data: option.data ? option.data : YJ_COMM.option.defaultOption,
            timeout: option.timeout ? option.timeout : YJ_COMM.defaultAjaxOption.timeout,
            success: function (data) {
                return option.successFnc ? option.successFnc(data) : YJ_COMM.defaultAjaxOption.successFnc(data);
            },
            error: function (req, msg, obj) {
                return option.errorFnc ? option.errorFnc(req, msg, obj) : YJ_COMM.defaultAjaxOption.errorFnc(req, msg, obj);
            }
        })
    },

    uploadFile: function (option) {
        option = option ? option : {
            input: '#input',
            url: '/upload',
            file: null,
            position: 'position',
            success: function (data) {

            }
        };

        let formData = new FormData();

        let myData;
        if (option.file) {
            myData = option.file;
        } else {
            myData = $('#' + option.input)[0].files[0];
        }
        formData.set("position", option.position);
        formData.append("uploadFile", myData);
        $.ajax({
            async: true,
            url: option.url,
            type: "POST",
            data: formData,
            processData: false, // 告诉jQuery不要去处理发送的数据
            contentType: false, // 告诉jQuery不要去设置Content-Type请求头
            success: function (data) {
                option.success(data);
            }
        });

    },
    /**
     * 分片上传
     * @param options
     */
    uploadFileSplit: function (options) {
        let conf = {
            fileName: options.fileName,
            position: options.position,
            file: options.file,
            skip: options.skip ? options.skip : 0,
            blockSize: options.blockSize ? options.blockSize : (1024 * 1024),
            processBarDom: options.processBarDom,
            completed: options.completed ? options.completed : function () {
                console.log('传输完毕！');
            }
        };
        let formData = new FormData();//初始化一个FormData对象
        let nextSize = Math.min((conf.skip + 1) * conf.blockSize, conf.file.size);//读取到结束位置
        let fileData = conf.file.slice(conf.skip * conf.blockSize, nextSize);//截取 部分文件 块
        formData.append("uploadFile", fileData);//将 部分文件 塞入FormData
        formData.append("position", conf.position);//保存位置
        formData.append("fileName", conf.file.name);//保存文件名字

        $.ajax({
            url: "/uploadBySplit",
            type: "POST",
            data: formData,
            async: true,
            processData: false,  // 告诉jQuery不要去处理发送的数据
            contentType: false,   // 告诉jQuery不要去设置Content-Type请求头
            beforeSend: function(xhr) {
                xhr.setRequestHeader('token', localStorage.getItem('token'));
            },
            success: function (responseText) {
                console.log('ajax option', this);
                if (conf.file.size <= nextSize) {//如果上传完成，则跳出继续上传
                    if (conf.processBarDom) {
                        conf.processBarDom.finish();
                    }
                    conf.completed();
                    return;
                }

                console.log("已经上传了" + (conf.skip + 1) + "块文件: ", (conf.skip + 1) * conf.blockSize);
                if (conf.processBarDom) {
                    conf.processBarDom.update((conf.skip + 1) * conf.blockSize);
                }
                conf.skip++;
                YJ_COMM.uploadFileSplit(conf);//递归调用
            },
            error: function (mes) {
                alert("error");
            }
        });
    },
    /**
     * 进度条工具
     */
    processBar: {
        dom: null,
        timestamp: 0,
        conf: {
            delay: 100, // ms
            domId: null,
            max: null,
            min: null,
            now: null,
        },
        initProcess: function (options) {
            this.conf.domId = options.dom;
            this.conf.max = options.max ? options.max : 100;
            this.conf.min = options.min ? options.min : 0;
            this.conf.now = options.now ? options.now : 0;

            let percent = this.conf.now * 1.0 * 100 / this.conf.max;

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
        update: function (now) {
            let percent = now * 100.0 / this.conf.max;
            percent = percent.toFixed(2);
            console.log(percent);
            let subDom = this.dom.find("div[role=progressbar]").eq(0);
            console.log(subDom);
            subDom.css('width', percent + "%");
            subDom.attr('aria-valuenow', now);
            subDom.find('span').eq(0).text(percent + "% Complete");
            subDom.find('span').eq(1).text(percent + '%');
        },
        finish: function () {
            this.update(this.conf.max);
        },
        updateDelay: function (now) {
            let time = new Date().getTime();
            if (time - this.timestamp >= this.conf.delay) {
                this.timestamp = time;
                this.update(now);
            }
        }
    },
    /**
     * 转换单位
     * @param bytes 字节数
     * @returns {string} 结果
     */
    transUnitBytes: function (bytes) {
        let factor = 1024.0;
        if (bytes > factor * factor * factor) {
            return (bytes / factor / factor / factor).toFixed(2) + "GB";
        } else if (bytes > factor * factor) {
            return (bytes / factor / factor).toFixed(2) + "MB";
        } else if (bytes > factor) {
            return (bytes / factor).toFixed(2) + "KB";
        } else {
            return bytes + "B";
        }
    }
};