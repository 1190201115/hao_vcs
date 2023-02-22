每一个文件由文件号（UUID)和版本号（递增ID）唯一标识，每一个文件记录下其上一个版本的版本号（考虑到3-》4，又回退到3，接着3——》5）。使用文件号-preVersion-selfVersion命名，来记录此文件与上一文件的区别。
原始文件命名为文件号-0。

数据库设计如下：
UUID , version, preVersion

需要一个数据库记录每一个文件的最新版本号,文件原始名称，最新的版本
UUID fileName latestVersion

需要一个数据库记录当前分支是否可删除（不存在出向量的分支）
UUID isSafeDeleted

文件的存储形式为：全量存储当前文件，同时记录文件与之关联的其他版本的差异
文件名为UUID+v+版本号。同一个文件有相同的UUID

项目需要与账户绑定
userID projectID

项目的基本信息
projectID projectName description privacy

项目在本地的存储形式
projectID->location(不存储数据库，用配置类保存规则）
