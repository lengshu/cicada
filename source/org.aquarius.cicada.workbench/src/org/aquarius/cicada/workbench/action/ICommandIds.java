package org.aquarius.cicada.workbench.action;

/**
 * Interface defining the application's command IDs. Key bindings can be defined
 * for specific commands. To associate an action with a command, use
 * IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

	public static final String CMD_LOAD_SITE = "org.aquarius.cicada.workbench.action.loadSite";

	public static final String CMD_HELP = "org.aquarius.cicada.workbench.action.help";

	public static final String CMD_COMPACT_DATABASE = "org.aquarius.cicada.workbench.action.compactDatabase";

	public static final String CMD_SHOW_BROWSER = "org.aquarius.cicada.workbench.action.showBrowser";

	public static final String CMD_PARSE_MOVIE = "org.aquarius.cicada.workbench.action.parseMovie";

	public static final String CMD_PARSER_CREATE = "org.aquarius.cicada.workbench.action.createParser";

	public static final String CMD_EDITOR_REMOVE_DUPLICATED_MOVIES = "org.aquarius.cicada.workbench.action.editor.removeDuplicatedMovies";

	public static final String CMD_EDITOR_RELOAD_BROWSER = "org.aquarius.cicada.workbench.action.editor.reloadBrowser";

	public static final String CMD_EDITOR_REFRESH_SITE = "org.aquarius.cicada.workbench.action.editor.refreshSite";

	public static final String CMD_EDITOR_REFRESH_MOVIE = "org.aquarius.cicada.workbench.action.editor.refreshMovie";

	public static final String CMD_EDITOR_OPEN_SITE_URL = "org.aquarius.cicada.workbench.action.editor.openSiteUrl";

	public static final String CMD_EDITOR_OPEN_MOVIE_URL = "org.aquarius.cicada.workbench.action.editor.openMovieUrl";

	public static final String CMD_EDITOR_PLAY_MOVIE = "org.aquarius.cicada.workbench.action.editor.playMovie";

	public static final String CMD_EDITOR_SHOW_IMAGE = "org.aquarius.cicada.workbench.action.editor.showImage";

	public static final String CMD_EDITOR_HOT_RELOAD = "org.aquarius.cicada.workbench.action.editor.hotReload";

	public static final String CMD_EDITOR_LOCK = "org.aquarius.cicada.workbench.action.editor.lock";

	public static final String CMD_EDITOR_ADD_CHANNEL = "org.aquarius.cicada.workbench.action.editor.addChannel";

	public static final String CMD_EDITOR_CLEAR_CACHE = "org.aquarius.cicada.workbench.action.editor.clearCache";

	public static final String CMD_EDITOR_DELETE = "org.aquarius.cicada.workbench.action.editor.delete";

	public static final String CMD_EDITOR_DOWNLOAD = "org.aquarius.cicada.workbench.action.editor.download";

	public static final String CMD_EDITOR_UPDATE_STATE = "org.aquarius.cicada.workbench.action.editor.updateState";

	public static final String CMD_EDITOR_GENERATE_DOWNLOAD_URL = "org.aquarius.cicada.workbench.action.editor.generateDownloadUrl";

	public static final String CMD_SITE_CONFIG_CREATE = "org.aquarius.cicada.workbench.action.createSiteConfig";

	public static final String CMD_SITE_CONFIG_EDIT = "org.aquarius.cicada.workbench.action.editSiteConfig";
}
