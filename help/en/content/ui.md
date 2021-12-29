### Interface description

The entire world is divided into several large sections. The top one is the operation area, namely the menu and the toolbar, and the middle is the data area, which is used to display the video information of the specified site. It should be emphasized that it can be in the browser and the table. You can switch between the modes at will, and the query conditions of the two are also different. The specific operation will be described in detail later. The bottom is the download, error, task and other views, which is convenient for user management.

When opening a site for the first time, it will generally prompt that the video list information in the site is empty, and will prompt the user whether to import from the site now. Sometimes the import will be very slow, maybe because the website has a large number of videos, the import is naturally slow; and in order to block crawlers, many websites will stipulate how long the visit will exceed the specified number of times, and the visit will be denied, so the frequency of visiting the webpage by default is increased Restrictions. But don’t worry, importing is a work done silently in the background, and every time several pieces of video information are imported, the interface will be automatically refreshed and displayed, and the operation will not be affected. Although the program uses background threads to load web pages as much as possible, it still has a certain impact on the interface operation speed of the entire program due to the use of a browser to access it, so if possible, consider importing videos in free time.

**Use a browser to view video information:**

1. In the browser interface, you can find your favorite videos at any time through the interface on the right, including keywords/actors/categories and other information.
2. The top of the browser interface is the paging function, and you can quickly jump to the previous or next page through the left and right keyboards.
3. In the browser interface, you can click the mouse to select or deselect the specified video. For the selected video, you can perform various operations through the buttons on the toolbar.
4. By default, the browser provides the function menu. If you want to use the browser's own menu, you can set it in the preferences and it will take effect after reopening.

![](https://github.com/aquariusStudio/cicada/blob/main/help/images/browserView.png)

**Use the form to view video information:**

1. The table is more suitable for displaying large amounts of data. Considering that the scale of general video websites is not exaggerated, all video information will be displayed in the list. Due to the use of NatTable, it takes about 3 seconds to load from the database, which is slightly slower. The rest of the time can be operated smoothly.
2. In order to facilitate the query, the next row of the table header row is used to support the query of each column, including the duration.
3. The table supports multiple selections, and you can use Shift/Ctrl and other keys to make multiple selections.
4. Click the header of the table to sort.
5. Click the right mouse button on the title bar of the table to customize the display status of each column.
6. The table supports the editing function, but it is closed by default to prevent misoperation. If you confirm the need for editing functions, you can click "Unlock" on the toolbar, and then you can edit the table. For the modified content, you can write the modified data back to the database through the "save" operation. Similarly, if you click "Lock" to close the editing function, the data will be automatically written back to the database.
7. When the user selects a row of data, the detailed information of the video will be displayed in the "Properties" view.
8. You can use Tooltip in the table to get detailed information and pictures by hovering the mouse on a piece of data for 1-2 seconds. This function is turned off by default and can be used after setting in the preferences, and it will take effect automatically.

![](https://github.com/aquariusStudio/cicada/blob/main/help/images/tableView.png)