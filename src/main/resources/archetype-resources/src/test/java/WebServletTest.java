#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class WebServletTest {
	private WebServlet srv;
	private ServletConfig cfg;
	private ServletContext ctx;
	private HttpServletRequest req;
	private HttpServletResponse res;

	@Before
	public void before() throws IOException {
		this.cfg = mock(ServletConfig.class);
		when(this.cfg.getInitParameter("webPath")).thenReturn("web");
		when(this.cfg.getInitParameter("cacheControl")).thenReturn(
				"no-transform,public,max-age=3600,s-maxage=7200");
		when(this.cfg.getInitParameter("indexNames")).thenReturn(
				"index.html,index.htm");
		when(this.cfg.getInitParameter("methods")).thenReturn(
				"get,post,put,delete");
		this.ctx = mock(ServletContext.class);
		// when(this.ctx.getRealPath(anyString())).then(returnsFirstArg());
		when(this.ctx.getRealPath(anyString()))
				.then(new StringAnswer("web/%s"));
		try {
			this.srv = new WebServlet();
			this.srv.init(this.cfg);
			when(this.srv.getServletContext()).thenReturn(this.ctx);
		} catch (ServletException e) {
			fail(e.getMessage());
		}
		this.req = mock(HttpServletRequest.class);
		this.res = mock(HttpServletResponse.class);
		when(this.res.getOutputStream()).thenReturn(
				mock(ServletOutputStream.class));
	}

	@After
	public void after() {
	}

	@Test
	public void testInit1() {
		HttpServlet srv2 = new WebServlet();
		try {
			srv2.init(this.cfg);
			assertTrue(true);
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoCommon1() {
		when(this.req.getPathInfo()).thenReturn("/");
		try {
			this.srv.doCommon(this.req, this.res);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoGet1() {
		when(this.req.getPathInfo()).thenReturn("/");
		try {
			this.srv.doGet(this.req, this.res);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoPost1() {
		when(this.req.getPathInfo()).thenReturn("/");
		try {
			this.srv.doPost(this.req, this.res);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoPut1() {
		when(this.req.getPathInfo()).thenReturn("/");
		try {
			this.srv.doPut(this.req, this.res);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoDelete1() {
		when(this.req.getPathInfo()).thenReturn("/");
		try {
			this.srv.doDelete(this.req, this.res);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ServletException e) {
			fail(e.getMessage());
		}
	}

	public class StringAnswer implements Answer<String> {
		private String format = "%s";

		public StringAnswer() {
		}

		public StringAnswer(String format) {
			this.format = format;
		}

		@Override
		public String answer(InvocationOnMock invocation) throws Throwable {
			Object[] args = invocation.getArguments();
			return String.format(this.format, args);
		}
	}
}