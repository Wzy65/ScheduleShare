# SchedulingShare
## 关于本APP
SchedulingShare旨在分享和记录行程，起到一个提醒和共享的作用。<br>
用户可以在我的我的界面添加个人的行程信息，记录将保存在手机本地；
也可在右上角将其分享出去，分享的内容上传网络并且出现在发现界面，其他用户可以查看和评论。<br>
* 作为个人，可以将自己接下来的行程保存和分享，期待与看到它的人来一次偶然的相遇；<br>
* 作为组织，可以将接下来的一些重要活动详情共享出去，让更多人的知道。例如某场粉丝见面会，某场同学重聚会，某场爱好社见面会……<br>
* 作为官方，也可以将重要的事情公布于此，例如X月X日举行招聘会、在线笔试；X月X日是第几届中央重要会议的举行日；X月X日是国际某官方活动的颁布会……<br>
该APP乃本人作为学习和兴趣所研发而出（包括但不限于完成毕设……），其中有可能有技术不成熟的地方，大家可以多给些建议，谢谢！<br>

## 下载Demo（仅供学习交流，切勿作他用）
[下载地址](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/app-debug.apk)<br>
* 扫描二维码（特地弄成了APP的主题颜色，嘿嘿）<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/demo%E4%B8%8B%E8%BD%BD.png)

## 实现
* 主要分为五大模块：登录注册、好友管理、个人行程、分享行程（发现）、其他设置<br>
* 项目使用了MVP框架模式，因为学习和使用过这个模式，有利于解耦以及将各自工作的内容分开<br>
* 主界面采用了Viewpager和Fragment的方式，下面是TabLayout，可以实现滑动浏览不同的内容：主页、好友、发现、我的<br>
* 项目中的列表（包括横向）都是使用的RecycleView，因为比较强大，但也有一些坑，有些许部分还不是很完美需要处理ing<br>
* 使用到的框架：Glide 、XRichText、Takephoto、SQLite、SmartRefresh、ButterKnife、Bmob以及TencentCloud都使用的OKHttp<br>
* 为了快速的集中在移动端的开发，特地使用了Bmob云服务，一个远端的服务器-数据库，其中数据的上传和处理都是使用Bmob实现，包括登录注册、好友管理等、各信息的查询<br>
* 在编辑界面可以添加富文本信息，这实现依赖于XRichText框架，其中的本地信息图片地址和内容保存在SQLite数据库中，而分享的信息则会先将图片上传到腾讯云-对象存储上（因为Bmob的上传文件服务需要域名和服务器，或者需要钱，所以就放弃了），再通过返回的图片url保存在信息内容中，最后上传到Bmob服务器上<br>
* 启动页解决了冷启动白屏的现象，并且初始化加载一些数据，一举两得。 <br>
* 感谢github开源社区各个框架的使用文档和demo，给了我很大的帮助<br>
* APP上的好看的图片得益于Pexels网站的免费版权图片<br>

         
## 使用效果
> 登录注册 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%88%86%E4%BA%AB.gif) <br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%88%86%E4%BA%AB.gif)

> 各个界面 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%90%84%E4%B8%AA%E7%95%8C%E9%9D%A2.gif)<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%90%84%E4%B8%AA%E7%95%8C%E9%9D%A2.gif)

> 添加好友 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E6%B7%BB%E5%8A%A0%E5%A5%BD%E5%8F%8B.gif)<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E6%B7%BB%E5%8A%A0%E5%A5%BD%E5%8F%8B.gif)

> 同意添加 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%90%8C%E6%84%8F%E6%B7%BB%E5%8A%A0.gif)<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%90%8C%E6%84%8F%E6%B7%BB%E5%8A%A0.gif)

> 添加新的行程 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E6%B7%BB%E5%8A%A0.gif)<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E6%B7%BB%E5%8A%A0.gif)

> 分享 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%88%86%E4%BA%AB.gif)<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E5%88%86%E4%BA%AB.gif)

> 评论 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E8%AF%84%E8%AE%BA.gif)<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E8%AF%84%E8%AE%BA.gif)

> 收藏/关注 [点击查看原图](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E6%94%B6%E8%97%8F.gif)<br>
![](https://download-1301419202.cos.ap-guangzhou.myqcloud.com/%E6%94%B6%E8%97%8F.gif)



