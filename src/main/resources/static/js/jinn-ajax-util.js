const AjaxUtil = {
    /**
     * 异步Ajax请求
     * @param url 请求路径
     * @param params 请求参数
     * @param successCallback 成功响应回调方法
     * @param errorCallback 异常处理
     */
    ajaxAsyncPostJson: function (url, params, successCallback, errorCallback) {
        $.ajax({
            async: true,
            url: url,
            type: 'POST',
            dataType: 'json',
            data: params,
            timeout: 60000,
            success: function (data) {
                // data: 成功响应信息
                if (successCallback) {
                    successCallback(data);
                }
            },
            error: function (req, msg, obj) {
                /*
                 * req: XMLHttpRequest 对象
                 * msg: 错误信息
                 * obj: 捕获的异常对象
                 */
                if (errorCallback) {
                    errorCallback(req, msg, obj);
                }
            }
        })
    },
    postForm: function (url, formId, successCallback, errorCallback) {
        let $FORM = $('#'+formId);
        const formArray = $FORM.serializeArray();
        let objJson = {};
        for (let i = 0; i < formArray.length; i++) {
            let formObj = formArray[i];
            const $DOM_INP = $FORM.find('[name="'+formObj.name+'"]');
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

        this.ajaxAsyncPostJson(url, objJson, successCallback, errorCallback);
    }
};