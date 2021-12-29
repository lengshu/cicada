### Technical Description

This is a document written to technicians, and ordinary users can ignore it.

The design idea of ​​the entire program is actually very simple, divided into the following steps:

1. Each site corresponds to a designated directory, which is composed by loading a configuration file (including basic information/channels, etc.) in the directory and the corresponding parsing script (JS).
2. When accessing a channel corresponding to a site, a browser will be launched (not visible by default), open the page corresponding to the site, and then call the list extraction script to obtain the corresponding video list and store it in the database.
3. When extracting the video list, the list page needs to display the video list in time update order, because the program will use the video link as the unique identification key. When it encounters the data that already exists in the database, it will stop the current traversal.
4. If the user needs to view specific information or download a video, it will start a browser, open the page corresponding to the video, and then call the information extraction script to get the corresponding download address, and then hand it over to the downloader for download.
5. In fact, when analyzing videos, many websites jump through dynamic links in multiple layers, so the system also provides an Analyser function, which can also be defined by JS scripts.
6. The entire software is an Eclipse-based RCP program, so new functions can also be provided through the Eclipse Plugin. Several extension points are also provided in the program, and interested students can also try to add new functions. If it is really necessary, I will add more technical documents in detail.