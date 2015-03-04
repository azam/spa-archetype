package io.azam.spa;

import java.io.File;
import java.io.IOException;
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
import javax.servlet.ServletContext;
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
	private static final String DEFAULT_WEB_PATH = "www";
	private static final String DEFAULT_CACHE_CONTROL = "no-transform,public,max-age=3600,s-maxage=7200";
	private static final Set<String> DEFAULT_INDEX_NAMES = new HashSet<String>(
			Arrays.asList("index.html", "index.htm"));
	private String webPath = DEFAULT_WEB_PATH;
	private String cacheControl = DEFAULT_CACHE_CONTROL;
	private Set<String> indexNames = DEFAULT_INDEX_NAMES;

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
		String paramWebPath = cfg.getInitParameter("webPath");
		if (paramWebPath != null && !paramWebPath.isEmpty()) {
			this.webPath = paramWebPath;
		}
		String paramCacheControl = cfg.getInitParameter("cacheControl");
		if (paramCacheControl != null && !paramCacheControl.isEmpty()) {
			this.cacheControl = paramCacheControl;
		}
		String paramIndexNames = cfg.getInitParameter("indexNames");
		if (paramIndexNames != null && !paramIndexNames.isEmpty()) {
			String[] names = paramIndexNames.split(",");
			Set<String> s = new HashSet<String>();
			for (String n : names) {
				if (n != null) {
					n = n.replaceFirst("^[\\s//]+", ""); // left trim
					n = n.replaceFirst("[\\s//]+$", ""); // right trim
					if (!n.isEmpty()) {
						s.add(n);
					}
				}
			}
			if (!s.isEmpty()) {
				this.indexNames = s;
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest req, HttpServletResponse res)
	 *      throws IOException, ServletException
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		doCommon(req, res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest req, HttpServletResponse res)
	 *      throws IOException, ServletException
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		doCommon(req, res);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest req, HttpServletResponse
	 *      res) throws IOException, ServletException
	 */
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		doCommon(req, res);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest req, HttpServletResponse
	 *      res) throws IOException, ServletException
	 */
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		doCommon(req, res);
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
		ServletContext ctx = getServletContext();
		String filePath = ctx.getRealPath(DEFAULT_PATH_SEPARATOR + this.webPath
				+ DEFAULT_PATH_SEPARATOR + req.getPathInfo());
		if (filePath != null) {
			String ifModifiedSince = req.getHeader("If-Modified-Since");
			SimpleDateFormat df0 = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
			df0.setTimeZone(TimeZone.getTimeZone("GMT"));
			long since = -1;
			if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
				try {
					since = df0.parse(ifModifiedSince).getTime();
				} catch (ParseException e) {
				}
			}
			File f = new File(filePath);
			if (f.isDirectory() && f.canRead()) {
				String newFilePath = null;
				for (String indexName : this.indexNames) {
					newFilePath = filePath + File.separator + indexName;
					File fIndex = new File(newFilePath);
					if (fIndex.isFile() && fIndex.canRead()) {
						break;
					} else {
						newFilePath = null;
					}
				}
				if (newFilePath == null) {
					res.sendError(HttpServletResponse.SC_NOT_FOUND,
							"File not found");
					return;
				} else {
					filePath = newFilePath;
					f = new File(filePath);
				}
			}
			if (f.isFile() && f.canRead()) {
				try {
					// Get file last modified date
					long mod = f.lastModified();
					if (mod <= since) {
						// Return 304 if not modified
						res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
						return;
					}
					// Set last modified in RFC 1123 date
					SimpleDateFormat df = new SimpleDateFormat(
							"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
					df.setTimeZone(TimeZone.getTimeZone("GMT"));
					String lastModified = df.format(new Date(mod));
					res.setHeader("Last-Modified", lastModified);
					// Set cache control
					boolean noCache = false;
					String reqCacheControl = req.getHeader("Cache-Control");
					if (reqCacheControl != null && !reqCacheControl.isEmpty()) {
						noCache = reqCacheControl.indexOf("no-cache") >= 0;
					}
					// String reqPragma = req.getHeader("Pragma");
					// if(reqPragma != null && !reqPragma.isEmpty()) {
					// noCache = reqPragma.indexOf("no-cache") >= 0;
					// }
					if (noCache) {
						res.setHeader("Cache-Control", "no-cache");
					} else {
						res.setHeader("Cache-Control", this.cacheControl);
					}
					// Set content type
					String contentType = getContentType(f.getName());
					res.setHeader("Content-Type", contentType);
					// Set status
					res.setStatus(HttpServletResponse.SC_OK);
					// res.setHeader("Access-Control-Allow-Origin", "*");
					// Copy file to response stream
					Files.copy(f.toPath(), res.getOutputStream());
					return;
				} catch (IOException e) {
					super.log(e.getMessage());
				}
			}
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Internal server error");
			return;
		}
		res.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
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
		return new MimetypesFileTypeMap().getContentType(filename);
	}
}
