#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Static file servlet
 *
 * @author "Azamshul Azizy"
 */
public class WebServlet extends HttpServlet {
	private static final long serialVersionUID = 2483441926147739498L;
	private static final String DEFAULT_PATH_SEPARATOR = "/";
	private static final String DEFAULT_WEB_PATH = "web";
	private static final String DEFAULT_CACHE_CONTROL = "no-transform,public,max-age=3600,s-maxage=7200";
	private static final Set<String> DEFAULT_INDEX_NAMES = new HashSet<String>(
			Arrays.asList("index.html", "index.htm"));
	private static final Set<String> DEFAULT_METHODS = new HashSet<String>(
			Arrays.asList("get"));
	private static final Set<String> ALLOWED_METHODS = new HashSet<String>(
			Arrays.asList("get", "put", "post", "delete"));
	private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String HEADER_LAST_MODIFIED = "Last-Modified";
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String RFC1123_DATEFORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	private static final String INITPARAMKEY_CACHECONTROL = "cacheControl";
	private static final String INITPARAMKEY_INDEXNAMES = "indexNames";
	private static final String INITPARAMKEY_METHODS = "methods";
	private static final String INITPARAMKEY_WEBPATH = "webPath";
	private String webPath = DEFAULT_WEB_PATH;
	private String cacheControl = DEFAULT_CACHE_CONTROL;
	private Set<String> indexNames = DEFAULT_INDEX_NAMES;
	private Set<String> methods = DEFAULT_METHODS;
	private MimetypesFileTypeMap mimetypes = new MimetypesFileTypeMap();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WebServlet() {
		super();
	}

	/**
	 * @see HttpServlet#init(ServletConfig cfg)
	 */
	@Override
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		String paramWebPath = cfg.getInitParameter(INITPARAMKEY_WEBPATH);
		if (paramWebPath != null && !paramWebPath.isEmpty()) {
			this.webPath = paramWebPath;
		}
		String paramCacheControl = cfg
				.getInitParameter(INITPARAMKEY_CACHECONTROL);
		if (paramCacheControl != null && !paramCacheControl.isEmpty()) {
			this.cacheControl = paramCacheControl;
		}
		String paramIndexNames = cfg.getInitParameter(INITPARAMKEY_INDEXNAMES);
		if (paramIndexNames != null) {
			String[] names = paramIndexNames.split(",");
			Set<String> set = new HashSet<String>();
			for (String s : names) {
				if (s != null) {
					s = s.replaceFirst("^[\\s//]+", ""); // left trim
					s = s.replaceFirst("[\\s//]+$", ""); // right trim
					if (!s.isEmpty()) {
						set.add(s);
					}
				}
			}
			this.indexNames = set;
		}
		String paramMethods = cfg.getInitParameter(INITPARAMKEY_METHODS);
		if (paramMethods != null) {
			String[] methods = paramMethods.split(",");
			Set<String> set = new HashSet<String>();
			for (String s : methods) {
				if (s != null) {
					s = s.replaceFirst("^[\\s//]+", ""); // left trim
					s = s.replaceFirst("[\\s//]+$", ""); // right trim
					s = s.toLowerCase(Locale.ENGLISH);
					if (!s.isEmpty() && ALLOWED_METHODS.contains(s)) {
						set.add(s);
					}
				}
			}
			this.methods = set;
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest req, HttpServletResponse res)
	 *      throws IOException, ServletException
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		if (this.methods.contains("get")) {
			doCommon(req, res);
		} else {
			super.doGet(req, res);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest req, HttpServletResponse res)
	 *      throws IOException, ServletException
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		if (this.methods.contains("post")) {
			doCommon(req, res);
		} else {
			super.doPost(req, res);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest req, HttpServletResponse
	 *      res) throws IOException, ServletException
	 */
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		if (this.methods.contains("put")) {
			doCommon(req, res);
		} else {
			super.doPut(req, res);
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest req, HttpServletResponse
	 *      res) throws IOException, ServletException
	 */
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		if (this.methods.contains("delete")) {
			doCommon(req, res);
		} else {
			super.doDelete(req, res);
		}
	}

	/**
	 * Common service for doGet, doPost, doPut and doDelete
	 *
	 * @param req
	 *            Http servlet request
	 * @param res
	 *            Http servlet response
	 * @throws IOException
	 *             IO Exception
	 * @throws ServletException
	 *             Servlet Exception
	 */
	protected void doCommon(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		URL fileUrl = this.getClass().getResource(
				DEFAULT_PATH_SEPARATOR + this.webPath + req.getPathInfo());
		if (fileUrl == null) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
			return;
		}
		File f = null;
		try {
			f = new File(fileUrl.toURI());
			if (f != null && f.isDirectory()) {
				for (String indexName : this.indexNames) {
					fileUrl = this.getClass().getResource(
							DEFAULT_PATH_SEPARATOR + this.webPath
									+ req.getPathInfo()
									+ DEFAULT_PATH_SEPARATOR + indexName);
					if (fileUrl != null) {
						f = new File(fileUrl.toURI());
						break;
					}
				}
			}
		} catch (URISyntaxException e) {
			super.log(e.getMessage());
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Internal server error");
			return;
		}
		if (f == null || !f.isFile() || !f.canRead()) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
			return;
		}
		try {
			// Get req modified since date
			String ifModifiedSince = req.getHeader(HEADER_IF_MODIFIED_SINCE);
			SimpleDateFormat df0 = new SimpleDateFormat(RFC1123_DATEFORMAT,
					Locale.US);
			df0.setTimeZone(TimeZone.getTimeZone("GMT"));
			long since = -1;
			if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
				try {
					since = df0.parse(ifModifiedSince).getTime();
				} catch (ParseException e) {
				}
			}
			// Get file last modified date
			long mod = f.lastModified();
			if (mod <= since) {
				// Return 304 if not modified
				res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			// Set last modified in RFC 1123 date
			SimpleDateFormat df = new SimpleDateFormat(RFC1123_DATEFORMAT,
					Locale.US);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			String lastModified = df.format(new Date(mod));
			res.setHeader(HEADER_LAST_MODIFIED, lastModified);
			// Set cache control
			boolean noCache = false;
			String reqCacheControl = req.getHeader(HEADER_CACHE_CONTROL);
			if (reqCacheControl != null && !reqCacheControl.isEmpty()) {
				noCache = reqCacheControl.indexOf("no-cache") >= 0;
			}
			// String reqPragma = req.getHeader("Pragma");
			// if(reqPragma != null && !reqPragma.isEmpty()) {
			// noCache = reqPragma.indexOf("no-cache") >= 0;
			// }
			if (noCache) {
				res.setHeader(HEADER_CACHE_CONTROL, "no-cache");
			} else {
				res.setHeader(HEADER_CACHE_CONTROL, this.cacheControl);
			}
			// Set content type
			String contentType = getContentType(fileUrl.getFile());
			res.setHeader(HEADER_CONTENT_TYPE, contentType);
			// Set status
			res.setStatus(HttpServletResponse.SC_OK);
			// Copy file to response stream
			Files.copy(f.toPath(), res.getOutputStream());
			return;
		} catch (IOException e) {
			super.log(e.getMessage());
		}
		res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Internal server error");
		return;
	}

	/**
	 * Detect content-type from file name
	 *
	 * @param filename
	 *            File name to detect content-type
	 * @return MIME-type detected
	 */
	private String getContentType(String filename) {
		return this.mimetypes.getContentType(filename);
	}
}
