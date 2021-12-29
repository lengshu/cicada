
### 扩展说明

本软件主要提供了视频站点分析（导入视频列表和下载视频）功能，接下来会结合图来详细说明如何扩展。

为了用户自己开发扩展，本软件提供了一个编辑器，可以通过"操作->创建视频解析器"这个入口创建一个全新的视频解析器来支持新的视频站点，对于已经存在的视频站点，则可以通过"操作->编辑视频解析器"这个入口来编辑。

编辑器有三个基本的标签页，分别是基本信息/视频列表导入/视频下载分析，下面依次讲解。

#### 基本信息：

1.  支持多集：是很多视频网站把连续剧当作一个视频，因此下载时，并不只是一个文件，而是多个文件。
2.  等待时间：很多视频网站为了避免爬虫或者攻击，会限制在指定时间内，比如1秒内只能访问多少次，由于有图片等资源，所以一般把这个等待时间设置为5秒。如果用户确认某网站不存在这个限制，可以设置为2-3秒（不建议低于2秒，因为浏览器也需要较长时间来加载资源和执行脚本）。
3.  动态链接：很多视频网站为了避免下载，或者为了让观众观看广告，使用了动态链接，即链接只在指定时间内对指定地址有效（或者指定用户），超过指定时间后，需要重新解析。这个时间每个站点都不同，但正常下载，3个小时足够下载完一部片子，所以默认值设为180分钟。
4.  支持分页：有些网站，如Youtube，它是使用动态加载，所以会在单一页面加载完所有视频列表，但有些网站则是采用分页的方式，请根据实际情况选择。
5.  频道管理：它并非必要，如果你只是要单个下载视频，可以忽略它。但如果你想将视频导入本地，进行查询或者管理，此项就是必须的。一个频道由一个名称和一个链接组成。名称是用来给用户自己看的，所以可以随便命名，不重复即可。而链接则比较复杂，它可能类似于[https://www.youtube.com/playlist?list=PLBWhfR\_7BXduiTdH-9sM5c6z7p3n8hll8](https://www.youtube.com/playlist?list=PLBWhfR_7BXduiTdH-9sM5c6z7p3n8hll8)（不支持分页），或者[https://www.pornhub.com/channels/vixen/videos?o=da&page={0](https://www.pornhub.com/channels/vixen/videos?o=da&page={0)}（支持分页）
6.  Cookie：普通用户没有必要设置这个，它可能涉及到语言选择或者登录等。普通用户如果有这类需求，建议保存完，直接使用菜单"更新浏览器->指定站点"这个入口，它会直接弹出一个浏览器，让用户进行登录或者选择语言。高级用户甚至可以直接在自己的JS代码里做各种设置。

![](https://github.com/aquariusStudio/cicada/blob/main/help/images/editSiteParser.png)

#### 导入列表：

1.  中间的浏览器：在开发时，应当先在浏览器打开你要测试的视频列表网站，当在左侧完成脚本后，可以直接点击"执行"按钮，左侧的脚本会在当前的浏览器中执行，并将结果显示在右侧的数据视图中。
2.  左侧的脚本：是用来查询当前页面的视频列表，它如果被正确执行，会返回一个JSON的数据结构体，这个数据结构参见下面的JSON示例。
3.  右侧的数据视图：用来展示脚本的执行结果，如果返回值是一个合法的JSON数据结构，会以树的结构展示出来，方便查看。如果返回值不是一个合法的JSON数据结构，会将错误信息显示在文本框中。
4.  由于个人能力实在有限，没法提供代码的调试及开发时的提示功能，建议先在Chrome等浏览器中测试脚本成功后，直接复制过来。程序默认使用Chromium内核执行JS，但允许用户关闭默认的浏览器内核，而使用SWT自带的浏览器（Windows上为IE，Linux上为WebKit），所以请关注JS语法上的区别（如果可以，建议兼容）。


```json
{
    "lastPageUrl": "https://www.pornhub.com/channels/vixen/videos?page=10",
    "nextPageUrl": "https://www.pornhub.com/channels/vixen/videos?page=3",
    "previousPageUrl": "https://www.pornhub.com/channels/vixen/videos",
    "totalPageCount": 10,
    "totalMovieCount": "344",
    "movieList": [
        {
            "duration": "00:12:35",
            "imageUrl": "https://di.phncdn.com/videos/202103/29/385839441/original/(m=eafTGgaaaa)(mh=tJZIlUcJe1Us4N4e)12.jpg",
            "pageUrl": "https://www.pornhub.com/view_video.php?viewkey=ph60617f5f88f3c",
            "title": "VIXEN Alberto has two gorgeous mistresses Emelie & Eveline"
        },
        {
            "duration": "00:12:38",
            "imageUrl": "https://di.phncdn.com/videos/202103/19/385357081/original/(m=eafTGgaaaa)(mh=cCztdW7mG_FFzyx4)11.jpg",
            "pageUrl": "https://www.pornhub.com/view_video.php?viewkey=ph60546afb3605f",
            "title": "VIXEN Seductive chess-pro Vanna has all the winning moves"
        }
    ]
}
```

![](https://github.com/aquariusStudio/cicada/blob/main/help/images/editSiteParserList.png)

#### 视频信息提取：

该功能与上面的"导入列表"类似，最大的区别就是返回的JSON数据结构体不同，参见下面的JSON示例。

```json
{
    "movie": {
        "actor": "Alberto-Blanco,Eveline-Dellai,Emelie-Ekstrom",
        "imageUrl": "https://ei.phncdn.com/videos/202103/29/385839441/original/(m=eafTGgaaaa)(mh=tJZIlUcJe1Us4N4e)12.jpg?cache=2021120302",
        "publishDate": "2021-03-29T07:21:20+00:00",
        "downloadInfoList": [
            {
                "downloadLinks": [],
                "mp4File": "VIXEN Alberto has two gorgeous mistresses Emelie & Eveline.mp4"
            }
        ],
        "tag": "vixen,threesome,brunette,blowjob,riding,reverse-cowgirl,missionary,doggystyle,facial,cum-in-mouth,cum-swapping,deepthroat,double-blowjob,3some",
        "category": "Blowjob,Brunette,FFM,HD-Porn,Pornstar,Threesome",
        "title": "VIXEN Alberto has two gorgeous mistresses Emelie & Eveline"
    }
}
```

#### 视频链接提取：

这一步并非必须的，因为某些网站的下载链接并不能通过视频信息页面直接提取，所以才又加入这一步。如果在视频信息页面可以直接提取下载信息，就无须这一步。系统如果发现存在这一步（即里面的JavaScript代码不为空），那么就会将信息提取页面返回下载链接中的内容直接在浏览器中打开，然后执行相应的JavaScript，以返回真正的下载链接。数据结构体比较简单，参见下面的JSON示例。其中需要注意的是，有很多网站，为了防止下载，会在请求头里加入一些限制，比如Referer值需要为指定内容，如果有这种情况，请正确设置requestHeaders里的值，这些值会随着下载请求，一并发给网站。

```json
{
    "requestHeaders": {
        "Referer": "https://dood.sh/"
    },
    "downloadUrl": "https://fi141l.dood.video/u5kj72sgqtglsdgge5teejyrjuqlvldhxe6d4krrbishghud4jbz4ppwsqpq/c3x8z5aj7n~PPSjnmrJCv?token=ima4toomuenj50d6jhlenqwd&expiry=1640261894020"
}
```
