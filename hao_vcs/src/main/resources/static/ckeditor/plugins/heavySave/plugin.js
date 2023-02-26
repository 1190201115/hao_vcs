CKEDITOR.plugins.add('heavySave', {
    icons: 'heavySave', // 图标
    init: function (editor) {
        editor.addCommand('heavySave', {
            exec: function (editor) {
                let log = prompt("请输入更新日志");
                if(log || log === ""){
                    let form = new FormData()
                    form.append("content", editor.getData())
                    form.append("morePath", decodeURI(window.location.search.split("?")[2].replaceAll('/', '\\')))
                    form.append("projectId", window.location.search.split("?")[1])
                    form.append("updateKind",2)
                    form.append("log", log)
                    axios.post('/version/updateText', form).then(results => {
                        alert(results.data.msg)
                    })
                }
            }
        });
        editor.ui.addButton('HeavySave', { // 添加按钮，按钮的名称会在添加按钮的时候使用
            label: '大量更新', // 鼠标悬浮在按钮上时显示的文字
            command: 'heavySave', // 这里写插件的名称
            toolbar: 'tools' // 按钮的位置，也可以在启用的时候设置位置
        });
    }
});