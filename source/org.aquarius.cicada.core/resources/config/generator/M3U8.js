function doGenerateDownloadList(movieList, downloadFolder) {
	var resultArray = [];

	for (var i = 0; i < movieList.size(); i++) {

		var movie = movieList.get(i);

		var downloadInfoList = movie.getDownloadInfoList();

		for (var j = 0; j < downloadInfoList.size(); j++) {

			var downloadInfo = downloadInfoList.get(j);

			var links = downloadInfo.getDownloadLinks();

			for (var l = 0; l < links.size(); l++) {

				var link = links.get(l);

				resultArray.push(link.getRealUrl());
			}
		}
	}

	return resultArray.join("\r\n");
}