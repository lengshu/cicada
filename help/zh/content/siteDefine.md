### 如何自定义视频展示界面 

这里会详细讲述，用户如何自定义视频展示界面。  
首先解释一下，程序中是如何用浏览器展示视频列表的。

1.  程序会把程序所在目录下的workspace/config/site/index.html文件加载到浏览器中。
2.  在加载index.html文件前，会自动向浏览器中注入一系列的函数，这些函数会在浏览器中以JS-Java桥接的方式存在，即可以通过JS脚本调用Java服务（具体的服务在下面以表格的方式给出）。
3.  当Html加载完成后，会通过JavaScript调用Java服务的形式，动态生成Html代码以展示视频信息。

所以任何对当前浏览器所展示的视频列表界面不满意，或者期望有更多的功能，都可以修改程序所在目录下的workspace/config/site/index.html，然后在浏览器界面里点"重新加载浏览器"菜单查看效果（需要确保浏览器里使用的不是浏览器自带菜单，可以在首选项里修改）。

| 名称及参数 | 功能说明 | 示例 |
| :-: | :-: | :-: |
| int queryMovieCountFunction() | 查询当前站点下的视频数量（如有过滤条件，则是过滤后的结果） | |
| JSON queryMovieListFunction(int currentPage,int pageCount) | 查询当前站点下指定分页里的视频内容，结果以JSON形式返回，参见后面的内容 | queryMovieListFunction(10,40) 每页包含40条记录的情况下，第10页有哪些数据 |
| void downloadMovieListFunction(String movieIdList) | 下载指定ID号的视频，参数为字符串，是ID号的合并，以","或者"\|"进行分隔 | queryMovieListFunction("11456,23768")下载ID号为11456和23768的两部视频 |
| void selectMovieFunction(int movieId) | 相当于一个消息事件，通知程序，用户选中了一个视频，这个视频的信息可以显示在属性视图中。 | selectMovieFunction(12345) 通知程序ID号12345的视频被选中了 |

```json
[
{
"albumCount": 1,
"category": "Cat1,Cat2",
"channel": "Channel1",
"detailed": true,
"duration": "00:12:01",
"id": 514899,
"imageUrl": "https://...",
"outOfValidTime": true,
"pageUrl": "https://...",
"publishDate": "2021-12-06",
"score": 0,
"site": "xxx",
"state": 8,
"tag": "tag1,tag2",
"title": "my Movie"
}
]
```
