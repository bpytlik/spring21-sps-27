package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.Matrix;
import com.google.sps.InfectionProbability;

/** Handles requests sent to the /hello URL. Try running a server and navigating to /hello! */
@WebServlet("/updateMatrix")
public class UpdateMatrixServlet extends HttpServlet {

  	@Override
  	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    	String stringMatrix = request.getParameter("stringMatrix");

		Double 	IP_NO_PROTECTION = Double.parseDouble(request.getParameter("IP_NO_PROTECTION"))/100.0, //TODO: Implement validation
				IP_WITH_MASK = Double.parseDouble(request.getParameter("IP_WITH_MASK"))/100.0,
				IP_WITH_VACCINE = Double.parseDouble(request.getParameter("IP_WITH_VACCINE"))/100.0;

		InfectionProbability infectionProbability = new InfectionProbability(IP_NO_PROTECTION, IP_WITH_MASK, IP_WITH_VACCINE);

    	System.out.println(infectionProbability);
		response.getWriter().println(infectionProbability);


		Matrix matrix = new Matrix(stringMatrix, infectionProbability);
        System.out.println(matrix.getStringMatrix());
		response.getWriter().println(matrix.getStringMatrix());

        matrix.updateMatrix();
        System.out.println(matrix.getStringMatrix());
		response.getWriter().println(matrix.getStringMatrix());
  	}
}