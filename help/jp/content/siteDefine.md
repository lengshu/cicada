###ビデオディスプレイインターフェイスをカスタマイズする方法

これは、ユーザーがビデオディスプレイインターフェイスをカスタマイズする方法の詳細な説明です。
まず、ブラウザで動画一覧を表示する方法を説明します。

1.プログラムは、プログラムが配置されているディレクトリにあるworkspace / config / site /index.htmlファイルをブラウザにロードします。
2. index.htmlファイルをロードする前に、一連の関数がブラウザに自動的に挿入されます。これらの関数は、JS-Javaブリッジとしてブラウザに存在します。つまり、Javaサービス（特定のサービス）はJSを介して呼び出すことができます。スクリプト（以下の表の形式で提供されます）。
3. Htmlがロードされると、JavaScriptを使用してJavaサービスを呼び出し、ビデオ情報を表示するHtmlコードを動的に生成します。

したがって、現在のブラウザで表示されるビデオリストインターフェイスに満足できない場合、またはより多くの機能を期待している場合は、プログラムが配置されているディレクトリのworkspace / config / site / index.htmlを変更して、[更新]をクリックしてください。ブラウザインターフェイスブラウザの「」メニューをロードして効果を表示します（ブラウザがブラウザ独自のメニューを使用していないことを確認する必要があります。これは設定で変更できます）。

|名前とパラメーター|関数の説明|例|
|：-：|：-：|：-：|
| int queryMovieCountFunction（）|現在のサイトの下にあるビデオの数をクエリします（フィルター条件がある場合は、フィルター処理された結果です）| |
| JSON queryMovieListFunction（int currentPage、int pageCount）|現在のサイトの指定されたページのビデオコンテンツをクエリすると、結果がJSON形式で返されます。次のコンテンツを参照してください| queryMovieListFunction（10,40）各ページに40レコードが含まれている場合、最初のデータは10ページにあります|
| void downloadMovieListFunction（String movieIdList）|指定されたID番号でビデオをダウンロードします。パラメーターは、「、」または「\ |」で区切られたID番号の組み合わせである文字列です| queryMovieListFunction（ "11456,23768"）ダウンロードID番号は11456と23768の2つのビデオです|
| void selectMovieFunction（int movieId）|これは、ユーザーがビデオを選択したことをプログラムに通知するメッセージイベントに相当し、このビデオの情報をプロパティビューに表示できます。 | selectMovieFunction（12345）プログラムID番号12345のビデオが選択されたことを通知します|

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
