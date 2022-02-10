/**
 *
 */
package org.aquarius.ui.json;

import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Use fastjson model to build a tree.<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class JSonTreeBuilder {

	/**
	 *
	 */
	private JSonTreeBuilder() {
		// No instance needed.
	}

	/**
	 * build a tree based on the json tree model.<BR>
	 *
	 * @param parentNode
	 * @param object
	 */
	private static void buildTree(DefaultMutableTreeNode parentNode, Object object) {
		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;
			Map<String, Object> childrenElements = jsonObject.getInnerMap();

			for (Map.Entry<String, Object> entry : childrenElements.entrySet()) {
				buildTree(parentNode, entry);
			}

			return;
		}

		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) object;

			for (int i = 0; i < jsonArray.size(); i++) {
				Object childObject = jsonArray.get(i);

				DefaultMutableTreeNode node = new DefaultMutableTreeNode(i + "");
				parentNode.add(node);

				buildTree(node, childObject);
			}

			return;
		}

		if (object instanceof Map.Entry) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
			String size = null;

			if (entry.getValue() instanceof JSONObject) {

				size = ((JSONObject) entry.getValue()).size() + "";

			}

			if (entry.getValue() instanceof JSONArray) {
				size = ((JSONArray) entry.getValue()).size() + "";
			}

			if (null != size) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey() + " : " + size);
				parentNode.add(node);

				buildTree(node, entry.getValue());
				return;
			}

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey() + " = " + entry.getValue());
			parentNode.add(node);

			return;

		}
	}

	/**
	 * build a tree based on the json tree model.<BR>
	 *
	 * @param object
	 * @return
	 */
	public static DefaultMutableTreeNode buildRootTree(Object object) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");

		buildTree(rootNode, object);

		return rootNode;

	}

}
