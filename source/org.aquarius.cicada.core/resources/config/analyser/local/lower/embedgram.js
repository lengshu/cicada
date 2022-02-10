//For embedgram.com
var importAll = new JavaImporter(java.util.regex, java.text, org.jsoup, org.apache.commons.lang, org.aquarius.util, org.aquarius.util.collection);

with(importAll) {

	function internalAnalyseDownloadUrl(dynamicUrl, referUrl) {
		var result = new Object();
		var response = Jsoup.connect(dynamicUrl).method(Connection.Method.GET).execute();

		var cookie = response.cookies();
		var document = response.parse();
		var elements = document.select("source[src]");

		var urlMap = new Object();

		for (var i = 0; i < elements.length; i++) {
			var element = elements[i];
			urlMap[element.attr("title").toUpperCase()] = element.absUrl("src");
		}

		result.urlMap = urlMap;

		var cookie = response.cookies();
		var cookieString = CollectionUtil.toCookieString(cookie);

		var headers = new Object();
		headers["cookie"] = cookieString;
		result.headers = headers;

		return result;
	}

	function doAnalyseDownloadUrl(dynamicUrl, referUrl)
	{
		return JSON.stringify(internalAnalyseDownloadUrl(dynamicUrl, referUrl));
	}
}

//var url="https://embedgram.com/v/61q27so74n5or";
//doAnalyseDownloadUrl(url,url);