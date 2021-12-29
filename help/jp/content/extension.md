###詳細な説明

このソフトウェアは、主にビデオサイト分析（ビデオリストのインポートとビデオのダウンロード）の機能を提供します。次に、図と併せて詳細に拡張する方法を説明します。

ユーザーが独自の拡張機能を開発するために、このソフトウェアはエディターを提供します。[操作]-> [ビデオパーサーの作成]エントリを使用して新しいビデオパーサーを作成し、新しいビデオサイトをサポートできます。既存のビデオサイトの場合は、次の方法で編集できます。エントリ「操作->ビデオパーサーの編集」。

エディターには3つの基本的なタブがあります。つまり、基本情報/ビデオリストのインポート/ビデオのダウンロード分析です。これらについては、以下で順番に説明します。

＃＃＃＃ 基本情報：

1.複数のエピソードのサポート：多くのビデオサイトでは、シリアルを1つのビデオとして扱っているため、ダウンロードするときは、1つのファイルだけでなく、複数のファイルになります。
2.待機時間：クロールや攻撃を回避するために、多くのビデオWebサイトでは、1秒間にアクセスできる回数など、指定された時間内の訪問数を制限します。写真などのリソースがあるため、待機時間通常は5秒に設定されています。特定のWebサイトにこの制限がないことをユーザーが確認した場合は、2〜3秒に設定できます（ブラウザーはリソースのロードとスクリプトの実行にも長い時間がかかるため、2秒未満はお勧めしません）。
3.動的リンク：多くのビデオWebサイトは、ダウンロードを回避したり、視聴者が広告を視聴できるようにするために動的リンクを使用します。つまり、リンクは、指定された時間内に指定されたアドレス（または指定されたユーザー）に対してのみ有効です。時間、それは再解析する必要があります。この時間はサイトごとに異なりますが、通常のダウンロードでは、映画をダウンロードするには3時間で十分なので、デフォルト値は180分に設定されています。
4.ページ付けのサポート：Youtubeなどの一部のWebサイトは動的読み込みを使用するため、すべてのビデオリストが1つのページに読み込まれますが、一部のWebサイトはページ付けを使用します。実際の状況に応じて選択してください。
5.チャネル管理：ビデオを個別にダウンロードするだけの場合は無視できます。ただし、クエリまたは管理のためにビデオをローカルにインポートする場合は、このアイテムが必要です。チャネルは、名前とリンクで構成されます。名前はユーザーが見られるようにするため、繰り返さずに好きな名前を付けることができます。リンクはもっと複雑で、[https://www.youtube.com/playlist?list=PLBWhfR\_7BXduiTdH-9sM5c6z7p3n8hll8]（https://www.youtube.com/playlist?list=PLBWhfR_7BXduiTdH-9sM5c6ll7p3n8 ）（ページネーションはサポートされていません）、または[https://www.pornhub.com/channels/vixen/videos?o=da&page={0](https://www.pornhub.com/channels/vixen/videos？ o = da＆page = {0）}（ページングをサポート）
6. Cookie：通常のユーザーがこれを設定する必要はありません。言語の選択やログインが必要になる場合があります。通常のユーザーがそのようなニーズを持っている場合は、それらを保存して、[ブラウザの更新]-> [指定されたサイト]メニューを直接使用することをお勧めします。ユーザーがログインまたは言語を選択するためのブラウザが直接ポップアップ表示されます。上級ユーザーは、自分のJSコードで直接さまざまな設定を行うこともできます。

！[]（https://github.com/aquariusStudio/cicada/blob/main/help/images/editSiteParser.png）

####インポートリスト：

1.真ん中のブラウザ：開発中は、まずブラウザでテストしたいビデオリストのウェブサイトを開く必要があります。左側のスクリプトが完了したら、[実行]ボタンを直接クリックし、左側のスクリプトをクリックします。現在のブラウザに表示されます。デバイスで実行し、右側のデータビューに結果を表示します。
2.左側のスクリプト：現在のページのビデオリストをクエリするために使用されます。正しく実行されると、JSONデータ構造が返されます。このデータ構造については、以下のJSONの例を参照してください。
3.右側のデータビュー：スクリプトの実行結果を表示するために使用されます。戻り値が正当なJSONデータ構造である場合、見やすいようにツリー構造で表示されます。戻り値が有効なJSONデータ構造でない場合、エラーメッセージがテキストボックスに表示されます。
4.個人の能力が限られているため、開発中にコードのデバッグおよびプロンプト機能を提供することはできません。Chromeなどのブラウザでスクリプトを正常にテストした直後にスクリプトをコピーすることをお勧めします。プログラムはデフォルトでChromiumカーネルを使用してJSを実行しますが、ユーザーはデフォルトのブラウザーカーネルを閉じて、SWT独自のブラウザー（WindowsではIE、LinuxではWebKit）を使用できるため、JS構文の違いに注意してください（可能な場合は、互換性があることをお勧めします）。


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
