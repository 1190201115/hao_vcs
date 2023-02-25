function sortProject(rule, projectList, callback) {
    if(rule === 'publicFirst'){
        projectList.sort(publicFirst)
        return projectList
    }else if(rule === 'privateFirst'){
        projectList.sort(privateFirst)
        return projectList
    }else if(rule === 'latestFirst'){
        projectList.sort(latestFirst)
        return projectList
    }else if(rule === 'reverse'){
        projectList.reverse()
        return projectList
    }
}

function sortVersionList(list){
    list.sort((p1,p2)=>{
        return p1.saveTime > p2.saveTime ? 1 : -1
    })
    return list

}

function publicFirst(p1,p2){
    return p2.privacy - p1.privacy;
}

function privateFirst(p1, p2){
    return p1.privacy - p2.privacy;
}

function latestFirst(p1, p2){
     return p1.createTime > p2.createTime ? 1 : -1
}
