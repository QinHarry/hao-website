# hao-website

* 要跑起来首先需要安装MySQL，对应的数据库，用户和密码参见resources/application.yml
* 需要用到的table 参见tale.sql, 确保mysql 里面有这些table
* log 系统用的是lombak所集成的，所以idea需要安装lombak plugin, 并且确保lombak 的版本没有问题，我遇到过‘you are not using a compiler supported by lombok’ 的问题
* 如果第一次使用的时候mvn install 测试出问题，可以先 mvn install -Dmaven.test.skip=true 跳过测试，目前测试问题还很多。
* 目前不支持注册，而password=md5(username+password).比如当username=hao,password=1122, 存在数据库里就是username=hao,password=md5(hao+1122)=556130f11b0c11f41c9f9dfb7b777ce8
