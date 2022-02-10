
function internalParseMovieDetailInfo() {
	//The following script is to return the json result  object
	var imgPrivateContainerElement = document.querySelector("div#imgPrivateContainer");

	if (null != imgPrivateContainerElement) {
		var result = new Object();
		result.errorMessage = "Private Video";
		result.shouleRetry=false;
		return result;
	}


	var xhr = new XMLHttpRequest();

	var movie = new Object();
	var downloadInfoList = [];
	movie.downloadInfoList = downloadInfoList;


	var metaElement = document.querySelector("meta[data-site]");
	movie.tag = metaElement.getAttribute("data-context-tag");
	movie.category = metaElement.getAttribute("data-context-category");
	movie.actor = metaElement.getAttribute("data-context-pornstar");

	var imageElement = document.querySelector("#videoElementPoster");
	if (null != imageElement) {
		movie.imageUrl = imageElement.src;
	}

	var titleElement = document.querySelector("meta[property='og:title']");
	movie.title = titleElement.content;

	var json = document.querySelector("script[type='application/ld+json']").innerHTML;
	var jsonObject = JSON.parse(json);
	movie.publishDate = jsonObject.uploadDate;

	var vargNames = Object.keys(window);

	var links = [];
	for (var i = 0; i < vargNames.length; i++) {
		var vargName = vargNames[i];
		if ((null != vargName) && (vargName.substring(0, 10) == ("flashvars_"))) {


			var mediaDefinitionElements = window[vargName].mediaDefinitions;

			for (var j = 0; j < mediaDefinitionElements.length; j++) {
				var mediaDefinitionElement = mediaDefinitionElements[j];

				if (mediaDefinitionElement.format.toLowerCase() == ("mp4")) {

					xhr.open('GET', mediaDefinitionElement.videoUrl, false);

					xhr.send();
					if (xhr.status == 200) {
						var jsonResult = JSON.parse(xhr.response);

						for (var num = 0; num < jsonResult.length; num++) {
							var qualityElement = jsonResult[num];

							if (qualityElement.quality == "720") {
								var link = new Object();

								link.sourceUrl = qualityElement.videoUrl;
								link.selected = true;

								links[0] = link;
							}
						}
					}
				}
			}
		}
	}

	downloadInfoList[0] = new Object();
	downloadInfoList[0].downloadLinks = links;

	var result = new Object();
	result.movie = movie;

	return result;

}

function doParseMovieDetailInfo() {
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();