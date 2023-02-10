function checkEmail(rule, email, callback) {
    let reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/
    if(email.length == 0) {
        return callback(new Error('邮箱不能为空'));
    }
    if(!reg.test(email)) {
        return callback(new Error('邮箱格式不合法'));
    }
    callback()
}

function checkRepeatPassword(rule, password, password2,callback) {
    if(password != password2){
       callback(new Error('两次输入不一致！'));
    }
    callback()
}