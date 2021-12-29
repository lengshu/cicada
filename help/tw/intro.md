## Cicada 功能介紹

Cicada是一個用來下載和管理電影的軟件，有點類似於Youtube-dl，只不過它是使用Java開發的一個Eclipse RCP，自帶GUI。



**它提供了以下功能：**



1.  可以將預先定義好的影視站點裡的所有電影或者指定的部分電影信息下載到本地，以瀏覽器或者表格的方式加以查看和查詢。
2.  對於喜歡的電影，可以直接下載到本地。
3.  目前支持普通的Http/Https下載，以及HLS（M3U8）流媒體的下載及合併（合併時需要Ffmpeg，但下載時無須Ffmpeg）。
4.  對於站點視頻信息和下載地址的解析，使用了瀏覽器中調用JavaScript的方式，所以允許任何對Javascript有所了解的人員自行定義如何解析一個站點並下載。
5.  提供了一個簡單的腳本開發和測試工具，方便用戶自行修改和擴展。
6.  程序使用了Eclipse插件技術，允許用戶自行擴展新功能。



作為一個視頻管理和下載軟件，它的使用是非常簡單的，接下來，你可以直接使用它，或者可以通過下面的詳細介紹深入了解其功能。 \



[\[主界面說明\]](content/ui.md)\
[\[配置說明\]](content/config.md)\
[\[擴展說明\]](content/extension.md)\
[\[技術說明\]](content/tech.md)\
[\[FAQ\]](content/faq.md)



這是一個使用 [Eclipse Public License2](https://www.eclipse.org/legal/epl-2.0/)
的開源軟件，你在遵守其協議的情況下可隨意使用和分發，並為自己的使用和分佈負責。


**備註：**由於個人時間和精力原因，所以這個軟件主要是在Windows下開發和測試的，雖然也在Linux下做了一下簡單的測試，可以正常的運行，但並沒有覆蓋到所有的功能，如果有問題，[可以點擊這裡提交反饋](https://github.com/aquariusStudio/cicada/issues)，我會嘗試解決。