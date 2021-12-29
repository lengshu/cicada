### 擴展說明

本軟件主要提供了視頻站點分析（導入視頻列表和下載視頻）功能，接下來會結合圖來詳細說明如何擴展。

為了用戶自己開發擴展，本軟件提供了一個編輯器，可以通過"操作->創建視頻解析器"這個入口創建一個全新的視頻解析器來支持新的視頻站點，對於已經存在的視頻站點，則可以通過"操作->編輯視頻解析器"這個入口來編輯。

編輯器有三個基本的標籤頁，分別是基本信息/視頻列表導入/視頻下載分析，下面依次講解。

#### 基本信息：

1.  支持多集：是很多視頻網站把連續劇當作一個視頻，因此下載時，並不只是一個文件，而是多個文件。
2.  等待時間：很多視頻網站為了避免爬蟲或者攻擊，會限制在指定時間內，比如1秒內只能訪問多少次，由於有圖片等資源，所以一般把這個等待時間設置為5秒。如果用戶確認某網站不存在這個限制，可以設置為2-3秒（不建議低於2秒，因為瀏覽器也需要較長時間來加載資源和執行腳本）。
3.  動態鏈接：很多視頻網站為了避免下載，或者為了讓觀眾觀看廣告，使用了動態鏈接，即鏈接只在指定時間內對指定地址有效（或者指定用戶），超過指定時間後，需要重新解析。這個時間每個站點都不同，但正常下載，3個小時足夠下載完一部片子，所以默認值設為180分鐘。
4.  支持分頁：有些網站，如Youtube，它是使用動態加載，所以會在單一頁面加載完所有視頻列表，但有些網站則是採用分頁的方式，請根據實際情況選擇。
5.  頻道管理：它並非必要，如果你只是要單個下載視頻，可以忽略它。但如果你想將視頻導入本地，進行查詢或者管理，此項就是必須的。一個頻道由一個名稱和一個鏈接組成。名稱是用來給用戶自己看的，所以可以隨便命名，不重複即可。而鏈接則比較複雜，它可能類似於[https://www.youtube.com/playlist?list=PLBWhfR\_7BXduiTdH-9sM5c6z7p3n8hll8](https://www.youtube.com/playlist?list=PLBWhfR_7BXduiTdH-9sM5c6z7p3n8hll8)（不支持分頁），或者[https://www.pornhub.com/channels/vixen/videos?o=da&page={0](https://www.pornhub.com/channels/vixen/videos?o=da&page={0)}（支持分頁）
6.  Cookie：普通用戶沒有必要設置這個，它可能涉及到語言選擇或者登錄等。普通用戶如果有這類需求，建議保存完，直接使用菜單"更新瀏覽器->指定站點"這個入口，它會直接彈出一個瀏覽器，讓用戶進行登錄或者選擇語言。高級用戶甚至可以直接在自己的JS代碼裡做各種設置。

![](https://github.com/aquariusStudio/cicada/blob/main/help/images/editSiteParser.png)

#### 導入列表：

1.  中間的瀏覽器：在開發時，應當先在瀏覽器打開你要測試的視頻列表網站，當在左側完成腳本後，可以直接點擊"執行"按鈕，左側的腳本會在當前的瀏覽器中執行，並將結果顯示在右側的數據視圖中。
2.  左側的腳本：是用來查詢當前頁面的視頻列表，它如果被正確執行，會返回一個JSON的數據結構體，這個數據結構參見下面的JSON示例。
3.  右側的數據視圖：用來展示腳本的執行結果，如果返回值是一個合法的JSON數據結構，會以樹的結構展示出來，方便查看。如果返回值不是一個合法的JSON數據結構，會將錯誤信息顯示在文本框中。
4.  由於個人能力實在有限，沒法提供代碼的調試及開發時的提示功能，建議先在Chrome等瀏覽器中測試腳本成功後，直接複製過來。程序默認使用Chromium內核執行JS，但允許用戶關閉默認的瀏覽器內核，而使用SWT自帶的瀏覽器（Windows上為IE，Linux上為WebKit），所以請關注JS語法上的區別（如果可以，建議兼容）。


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
