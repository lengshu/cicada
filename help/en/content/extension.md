### Extended description

This software mainly provides the function of video site analysis (import video list and download video). Next, we will explain how to expand in detail in conjunction with the figure.

In order for users to develop their own extensions, this software provides an editor. You can create a brand new video parser through the entry "Operation -> Create Video Parser" to support new video sites. For existing video sites, you can Edit through the entry "Operation -> Edit Video Parser".

The editor has three basic tabs, namely basic information/video list import/video download analysis, which will be explained in sequence below.

#### Basic Information:

1. Support for multiple episodes: Many video sites treat serials as one video, so when downloading, it is not just one file, but multiple files.
2. Waiting time: In order to avoid crawling or attacks, many video websites will limit the number of visits within a specified time, such as how many times can be accessed within 1 second. Because there are resources such as pictures, the waiting time is generally set to 5 seconds. If the user confirms that a certain website does not have this limit, it can be set to 2-3 seconds (less than 2 seconds is not recommended, because the browser also takes a long time to load resources and execute scripts).
3. Dynamic links: Many video websites use dynamic links in order to avoid downloading or to allow viewers to watch advertisements. That is, the link is only valid for the specified address (or the specified user) within a specified time. After the specified time, it needs to be re-parsed. This time is different for each site, but for normal downloads, 3 hours is enough to download a movie, so the default value is set to 180 minutes.
4. Support pagination: some websites, such as Youtube, use dynamic loading, so all video lists will be loaded on a single page, but some websites use pagination, please choose according to the actual situation.
5. Channel management: it is not necessary, if you just want to download a video individually, you can ignore it. But if you want to import the video locally for query or management, this item is necessary. A channel consists of a name and a link. The name is for the user to see, so you can name it whatever you want without repeating it. The link is more complicated, it may be similar to [https://www.youtube.com/playlist?list=PLBWhfR\_7BXduiTdH-9sM5c6z7p3n8hll8](https://www.youtube.com/playlist?list=PLBWhfR_7BXduiTdH-9sM5c6ll7p3n8) (Pagination is not supported), or [https://www.pornhub.com/channels/vixen/videos?o=da&page={0](https://www.pornhub.com/channels/vixen/videos?o= da&page={0)} (support paging)
6. Cookie: It is not necessary for ordinary users to set this, it may involve language selection or login. If ordinary users have such needs, it is recommended to save them and use the menu "Update Browser->Specified Sites" directly. It will directly pop up a browser for the user to log in or select a language. Advanced users can even make various settings directly in their own JS code.

![](https://github.com/aquariusStudio/cicada/blob/main/help/images/editSiteParser.png)

#### Import list:

1. The middle browser: During development, you should first open the video list website you want to test in the browser. When the script is completed on the left, you can directly click the "Execute" button, and the script on the left will be displayed in the current browser. Execute in the device, and display the result in the data view on the right.
2. The script on the left: is used to query the video list of the current page. If it is executed correctly, it will return a JSON data structure. For this data structure, see the JSON example below.
3. The data view on the right: used to display the execution result of the script. If the return value is a legal JSON data structure, it will be displayed in a tree structure for easy viewing. If the return value is not a valid JSON data structure, the error message will be displayed in the text box.
4. Due to the limited personal ability, it is impossible to provide code debugging and prompting functions during development. It is recommended to copy the script directly after successfully testing the script in a browser such as Chrome. The program uses the Chromium kernel to execute JS by default, but allows users to close the default browser kernel and use SWT's own browser (IE on Windows, WebKit on Linux), so please pay attention to the difference in JS syntax (if possible, It is recommended to be compatible).


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
