CKEDITOR.plugins.add('lightSave', {
    icons: 'lightSave', // 图标
    init: function (editor) {
        editor.addCommand('onlyLightSave', { // 创建插件，插件的名称会在启用插件的时候使用
            exec: function (editor) {
                let form = new FormData()
                form.append("content", editor.getData())
                form.append("morePath", decodeURI(window.location.search.split("?")[2].replaceAll('/', '\\')))
                form.append("projectId", window.location.search.split("?")[1])
                form.append("updateKind",0)
                axios.post('/version/updateText', form).then(results => {
                        alert(results.data.msg)
                })
            }
        });
        editor.ui.addButton('LightSave', { // 添加按钮，按钮的名称会在添加按钮的时候使用
            label: '轻量更新', // 鼠标悬浮在按钮上时显示的文字
            command: 'onlyLightSave', // 这里写插件的名称
            toolbar: 'tools' // 按钮的位置，也可以在启用的时候设置位置
        });
    }
});