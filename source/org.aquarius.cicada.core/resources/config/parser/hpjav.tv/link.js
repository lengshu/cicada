function doParseDownloadLink() {
	var result = new Object();

	var urlString = document.getElementById("down_url").href;
	
	result.sourceUrl = urlString;

	return (result);
}

return JSON.stringify(doParseDownloadLink());