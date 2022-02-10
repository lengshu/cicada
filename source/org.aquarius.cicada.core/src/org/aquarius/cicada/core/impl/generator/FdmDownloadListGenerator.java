/**
 * #protocol
 */
package org.aquarius.cicada.core.impl.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.aquarius.cicada.core.model.DownloadInfo;
import org.aquarius.cicada.core.model.Link;
import org.aquarius.cicada.core.model.Movie;
import org.aquarius.cicada.core.spi.AbstractDownloadListGenerator;
import org.aquarius.log.LogUtil;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Download list generator for Free download manager 3.X<BR>
 *
 * @author aquarius.github@gmail.com
 *
 */
public class FdmDownloadListGenerator extends AbstractDownloadListGenerator {

	/**
	 *
	 */
	public static final String ID = "FDM3";

	private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	// 2.根据解析器工厂创建解析器
	private DocumentBuilder documentBuilder;
	// 3.根据xml文件生成Document

	private Logger logger = LogUtil.getLogger(this.getClass());

	/**
	 * @throws ParserConfigurationException
	 *
	 */
	public FdmDownloadListGenerator() {
		super();

		try {
			this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			this.logger.error("create fdm download generator", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.aquarius.porn.core.spi.AbstractDownloadListGenerator#getName()
	 */
	@Override
	public String getName() {
		return ID;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public String generateDownloadList(List<Movie> movieList, String downloadFolder) {
		try {
			return doGenerate(movieList, downloadFolder);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param movies
	 * @param folderPrefix
	 * @return
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
	private String doGenerate(List<Movie> movies, String folderPrefix)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {

		Document doc = this.documentBuilder.newDocument();

		Element rootElement = doc.createElement("FDM_Downloads_Info_List");
		doc.appendChild(rootElement);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		for (Movie movie : movies) {
			List<DownloadInfo> downloadInfoList = movie.getDownloadInfoList();

			for (DownloadInfo downloadInfo : downloadInfoList) {

				List<Link> links = downloadInfo.getDownloadLinks();

				for (Link link : links) {

					if (link.isSelected()) {
						Element downloadElement = doc.createElement("download");

						Element urlElement = doc.createElement("urlPattern");
						Element groupElement = doc.createElement("group");
						Element commentElement = doc.createElement("comment");

						urlElement.setTextContent(getDownloadUrl(link));

						groupElement.setTextContent("video");
						commentElement.setTextContent(downloadInfo.getMp4File());

						downloadElement.appendChild(urlElement);
						downloadElement.appendChild(groupElement);
						downloadElement.appendChild(commentElement);

						rootElement.appendChild(downloadElement);

						break;
					}
				}
			}

		}

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		transformer.transform(new DOMSource(doc), new StreamResult(byteOutputStream));

		return IOUtils.toString(byteOutputStream.toByteArray(), "UTF-8");
	}

}
