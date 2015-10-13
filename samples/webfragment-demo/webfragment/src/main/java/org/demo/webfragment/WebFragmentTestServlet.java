package org.demo.webfragment;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebFragmentTestServlet extends HttpServlet {

	private static final long serialVersionUID = 1752273485067052128L;

	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Teste de acesso ao servlet");
		PrintWriter pw = resp.getWriter();
		pw.write("Teste de acesso ao servlet");
		pw.flush();
		pw.close();
	}
	
}
