### 如何自定義視頻展示界面 

這裡會詳細講述，用戶如何自定義視頻展示界面。
首先解釋一下，程序中是如何用瀏覽器展示視頻列表的。

1.  程序會把程序所在目錄下的workspace/config/site/index.html文件加載到瀏覽器中。
2.  在加載index.html文件前，會自動向瀏覽器中註入一系列的函數，這些函數會在瀏覽器中以JS-Java橋接的方式存在，即可以通過JS腳本調用Java服務（具體的服務在下面以表格的方式給出）。
3.  當Html加載完成後，會通過JavaScript調用Java服務的形式，動態生成Html代碼以展示視頻信息。

所以任何對當前瀏覽器所展示的視頻列表界面不滿意，或者期望有更多的功能，都可以修改程序所在目錄下的workspace/config/site/index.html，然後在瀏覽器界面裡點"重新加載瀏覽器"菜單查看效果（需要確保瀏覽器裡使用的不是瀏覽器自帶菜單，可以在首選項裡修改）。

| 名稱及參數 | 功能說明 | 示例 |
| :-: | :-: | :-: |
| int queryMovieCountFunction() | 查詢當前站點下的視頻數量（如有過濾條件，則是過濾後的結果） | |
| JSON queryMovieListFunction(int currentPage,int pageCount) | 查詢當前站點下指定分頁裡的視頻內容，結果以JSON形式返回，參見後面的內容 | queryMovieListFunction(10,40) 每頁包含40條記錄的情況下，第10頁有哪些數據 |
| void downloadMovieListFunction(String movieIdList) | 下載指定ID號的視頻，參數為字符串，是ID號的合併，以","或者"\|"進行分隔 | queryMovieListFunction("11456,23768")下載ID號為11456和23768的兩部視頻 |
| void selectMovieFunction(int movieId) | 相當於一個消息事件，通知程序，用戶選中了一個視頻，這個視頻的信息可以顯示在屬性視圖中。 | selectMovieFunction(12345) 通知程序ID號12345的視頻被選中了 |

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