//For avvideos.xyz

var importAll = new JavaImporter(java.util.regex, java.text, org.jsoup, org.apache.commons.lang, org.aquarius.util, org.aquarius.util.collection);

with(importAll) {

	function internalAnalyseDownloadUrl(dynamicUrl, referUrl) {

		var result = new Object();

		var pattern = /(\d+)(?!.*\d)/g;
		
		var arrays = dynamicUrl.match(pattern);


		if (arrays != null && arrays.length == 1) {
			var tid = arrays[0];
			dynamicUrl = "https://avvideos.xyz/app/video/play?tid=" + tid + "&from_lang=chinese&v=1.0.4"
		}else {
			result.errorMessage = "No tid founded";
			result.shouldRetry = false;
			return result;
		}

		var response = Jsoup.connect(dynamicUrl).ignoreContentType(true).method(Connection.Method.GET).execute();

		var document = response.parse();

		var jsonObject=JSON.parse(document.body().html());
		var downloadUrl="https://avvideos.xyz"+jsonObject.data.video.m3u8_url;

		var urlMap = new Object();

		urlMap["720P"] = downloadUrl;
		urlMap["1080P"] = downloadUrl;
		urlMap["480P"] = downloadUrl;
		urlMap[""] = downloadUrl;
		urlMap[" "] = downloadUrl;

		result.urlMap = urlMap;
		result.hls=true;

		return result;
	}

	function doAnalyseDownloadUrl(dynamicUrl, referUrl)
	{
		return JSON.stringify(internalAnalyseDownloadUrl(dynamicUrl, referUrl));
	}
}

//var url = "https://avvideos.xyz/video/embed/50369";
//doAnalyseDownloadUrl(url, url);