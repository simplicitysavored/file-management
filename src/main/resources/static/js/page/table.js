let CURRENT_POSITION = null;
let queryParams;

let $DOM_TABLE, // 表格
    $DOM_TIP_MESSAGE, // 提示框
    $DOM_DRIVER_DROPDOWN; // 驱动器下拉框

let $MODAL_CREATE // 弹窗-新增文件夹
    , $MODAL_UPLOAD
;

$(function () {
    $DOM_TIP_MESSAGE = $('#tipMessage');
    $DOM_TABLE = $('#table');
    $DOM_DRIVER_DROPDOWN = $('#driverRoot');
    $MODAL_CREATE = $('#MODAL_CREATE');
    $MODAL_UPLOAD = $('#MODAL_UPLOAD');

    reLoadDriverDropdownByGetListenList();


    $DOM_DRIVER_DROPDOWN.change(function () {
        reloadDataList($(this).val());
    })
}); // $(function(){}) end

function initialTable() {
    $DOM_TABLE.bootstrapTable({
        toolbar: '#toolbar',
        url: "/table/list",
        method: 'post',
        showColumns: true,
        pagination: true,
        sidePagination: 'client',
        pageSize: 20,
        pageList: [20, 50, 100],
        totalField: 'total',
        dataField: 'data',
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        queryParamsType: '',
        classes: 'table table-bordered table-hover',
        responseHandler: responseHandler(),
        onLoadSuccess: function (data) {
            /*设置当前所在位置*/
            CURRENT_POSITION = data.extendInfo;
            $('input[name=position]').val(data.extendInfo);
        },
        onLoadError: function (data) {
            //window.location.reload();
        },
        ajaxOptions: {
            headers: {
                'token': localStorage.getItem('token')
            }
        },
        queryParams: function (params) {
            if (!queryParams) {
                queryParams = params;
                queryParams.queryObj = {
                    path: $DOM_DRIVER_DROPDOWN.val(),
                    dotBack: true
                };
            }
            return queryParams;
        },
        onClickRow: function (row, tr) {
            console.log('onClickRow-row', row);
        },
        /*点击选择行*/
        clickToSelect: true,
        columns: [
            // {
            //     field: 'number',
            //     title: '序号',
            //     width: '5%',
            //     align: 'center',
            //     switchable: false,
            //     formatter: function (value, row, index) {
            //         return '<input type="checkbox" name="selectCheckBox" value="' + index + '" aria-label="多选框"/>';
            //     }
            // },
            {
                checkbox: true,
                width: '5%',
                align: 'center',
                // checkboxEnabled: false
                formatter: function (fieldValue, rowData, rowIndex, fieldName) {
                    if (rowData.name === '..') {
                        return {disabled: true};
                    }
                    return {disabled: false};
                }
            },
            {
                field: 'name',
                title: '名称',
                formatter: function (fieldValue, rowData, rowIndex, fieldName) {
                    let html = '';
                    if (rowData.folder) {
                        html += '<a href="javascript:reloadDataList(\'' + encodeURI(rowData.absolutePathEncode) + '\')">' + fieldValue + '</a>';
                    } else {
                        html += fieldValue;
                    }
                    if (rowData.protect) {
                        html += '&nbsp;&nbsp;<span class="label label-warning">保护</span>';
                    }
                    return html;
                }
            }, {
                field: 'byteSizeDesc',
                title: '大小'
            }, /*{
                    field: 'price',
                    title: '操作',
                    formatter: function (fieldValue, rowData, rowIndex, fieldName) {
                        return [
                            '<button type="button" class="btn btn-default btn-sm" onclick="deleteFile(' + rowData + ')">删除</button>'
                        ].join("\n");
                    }
                }*/
        ]
    }); // end bootstrapTable

}

/**
 * 加载监听的驱动
 */
function reLoadDriverDropdownByGetListenList() {
    AjaxUtil.ajaxAsyncPostJsonWithToken('/table/getListenList', {}, function (data) {
        if (data.statusCode === 200) {
            if (data.data) {
                $DOM_DRIVER_DROPDOWN.empty();
                for (let i = 0; i < data.data.length; i++) {
                    let item = data.data[i];
                    $DOM_DRIVER_DROPDOWN.append('<option value="' + item.value + '">' + item.desc + '</option>');
                }
            }
        }

        initialTable();
    })
}

function responseHandler() {
    console.log('responseHandler')
}


/**
 * 加载表格数据
 */
function reloadDataList(path) {
    path = path ? path : $DOM_DRIVER_DROPDOWN.val();
    queryParams.queryObj = {
        path: path,
        dotBack: true
    };
    $DOM_TABLE.bootstrapTable('refresh', queryParams);

}

function refreshCurrentPositionList() {
    reloadDataList(CURRENT_POSITION);
}

function openCreateModal() {
    $MODAL_CREATE.modal({
        show: true,
        backdrop: 'static',
        keyboard: false
    });
    setTimeout(function () {
        $MODAL_CREATE.find('input[name=name]').focus();
    }, 500);
    $MODAL_CREATE.find('input[name=name]').bind('keypress', function (e) {
        if (event.keyCode === 13) {
            createFolder();
        }
    })

}

/**
 * 创建文件夹
 */
function createFolder() {
    let url = '/table/create';
    let formId = 'FORM_CREATE_FOLDER';
    AjaxUtil.postFormWithToken(url, formId,
        function (data) {
            if (data.statusCode === 200) {
                $('#MODAL_CREATE').modal('hide');
                reloadDataList($('#FORM_CREATE_FOLDER').find('input[name=position]').val());
            } else {
                alert(data.message);
            }
        }
    );
}

/**
 * 获取选中行（一行）
 */
function getSelectedRowOnlyOne() {
    let rows = getSelectedMultiRow();

    if (rows.length > 1) {
        swal('只能选择一行进行操作！');
        return false;
    }
    return rows[0];
}

/**
 * 获取选中行（多行）
 */
function getSelectedMultiRow() {
    let protectNum = 0;

    let rows = $DOM_TABLE.bootstrapTable('getSelections');
    if (rows == null || rows.length === 0) {
        swal("请先选择一行进行操作！");
        return false;
    }

    for (let index in rows) {
        if (rows[index].protect) {
            protectNum++;
        }
    }
    if (protectNum > 0) {
        swal('存在受保护文件个数:' + protectNum);
        return false;
    }
    return rows;
}

/**
 * 删除文件
 */
function deleteFile() {
    // let row = getSelectedRowOnlyOne();
    let rows = getSelectedMultiRow();
    if (rows) {
        let row = rows[0];
        swal({
            title: '删除后不可恢复',
            text: '已选中' + rows.length + '个文件',
            icon: 'warning',
            buttons: {
                cancel: {
                    text: "取消",
                    value: false,
                    visible: true,
                    className: "",
                    closeModal: true,
                },
                confirm: {
                    text: "确认",
                    value: true,
                    visible: true,
                    className: "",
                    closeModal: true
                },
                // ... 支持自定义按钮
            }
        }).then(btnValue => {
            if (btnValue) {
                let pathList = [];
                $.each(rows, function (index, item) {
                    pathList.push(item.absolutePath);
                });
                let params = {
                    path: row.absolutePath,
                    pathList: pathList
                };
                AjaxUtil.ajaxAsyncPostJsonWithToken(
                    "/table/delete",
                    params,
                    function (res) {
                        if (res.statusCode === 200) {
                            swal({
                                icon: "success",
                            });
                            refreshCurrentPositionList();
                        } else {
                            swal({
                                text: res.message,
                                icon: "error",
                            })
                        }
                    }
                )
            } else {
                console.log("点击了取消");
            }
            return "no.1 then end";
        }).then(value => {
            // 这个是前一个 then 返回的值
            console.log("no.2 then receive:", value);
        });

    }

}

function downloadFileV2() {

    let rows = $DOM_TABLE.bootstrapTable('getSelections');
    if (rows.length !== 1) {
        alert("一次只能下载一个");
        return false;
    }

    //alert(rows[0].absolutePath);

    //window.open("/downloadV2?token="+localStorage.getItem('token')+"&path="+rows[0].absolutePathEncode, "_blank")

    // AjaxUtil.downloadWithToken("/downloadV2?path=" + rows[0].absolutePathEncode);
    AjaxUtil.downloadWithToken({
        name: rows[0].name,
        path: rows[0].absolutePathEncode
    })

}

function openUploadModal() {
    $MODAL_UPLOAD.modal({
        show: true,
        backdrop: 'static',
        keyboard: false
    });
}

function uploadFileToServer(btn) {
    // AjaxUtil.postFormWithToken('/uploadBySplit', 'FORM_UPLOAD', function (data) {
    //
    // });

    $(btn).button('loading');

    let files = $('#uploadFile')[0].files;

    let myData = files[0];

    // 初始化进度条
    let myBar = AjaxUtil.processBar.initProcess({
        dom: 'MyProcessBar',
        max: myData.size,
        min: 0,
        now: 0
    });

    let DOM_CURRENT_POSITION_FOLDER = $MODAL_UPLOAD.find('input[name=position]').eq(0);

    AjaxUtil.uploadFileSplitWithToken({
        processBarDom: myBar,
        position: DOM_CURRENT_POSITION_FOLDER.val(),
        file: myData,
        blockSize: 1024 * 1024 * 10,
        uploadFinish: function (responseText) {
            $(btn).button('reset');
            $MODAL_UPLOAD.find('form')[0].reset();
            $MODAL_UPLOAD.modal('hide');
            myBar.destroy();
            reloadDataList(DOM_CURRENT_POSITION_FOLDER.val());
        },
        uploadError: function (responseText) {
            $(btn).button('reset');
            alert(responseText.message);
        }
    });
}

function logout() {
    localStorage.setItem("token", "");
    AjaxUtil.jumpPageWithToken("/login");
}
