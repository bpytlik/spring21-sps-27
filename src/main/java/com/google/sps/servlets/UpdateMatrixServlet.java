package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.Matrix;
import com.google.sps.InfectionProbability;

import com.google.gson.Gson;

import java.lang.Object;

import java.lang.StringBuffer;
import java.io.BufferedReader;

import org.json.*;

/** Handles requests sent to the /hello URL. Try running a server and navigating to /hello! */
@WebServlet("/updateMatrix")
public class UpdateMatrixServlet extends HttpServlet {

  	@Override
  	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) jb.append(line);
		} catch (Exception e) { /*report an error*/ }

		try {
			JSONObject jsonObject = new JSONObject(jb.toString());

			String stringMatrix = jsonObject.getString("stringMatrix");


			Double 	IP_NO_PROTECTION = Double.parseDouble(jsonObject.getString("IP_NO_PROTECTION"))/100.0, //TODO: Implement validation
					IP_WITH_MASK = Double.parseDouble(jsonObject.getString("IP_WITH_MASK"))/100.0,
					IP_WITH_VACCINE = Double.parseDouble(jsonObject.getString("IP_WITH_VACCINE"))/100.0;

			InfectionProbability infectionProbability = new InfectionProbability(IP_NO_PROTECTION, IP_WITH_MASK, IP_WITH_VACCINE);


			Matrix matrix = new Matrix(stringMatrix, infectionProbability);

			matrix.updateMatrix();

			Gson gson = new Gson();

			String updatedStringMatrix = matrix.getStringMatrix();

			response.setContentType("application/json;");
			response.getWriter().println(gson.toJson(updatedStringMatrix));
		} catch (JSONException e) {
		// crash and burn
			Gson gson = new Gson();

			response.setStatus(400);
			response.getWriter().println(gson.toJson("Error parsing JSON request string"));

			throw new IOException("Error parsing JSON request string");
		} catch (NumberFormatException e) {
			Gson gson = new Gson();

			response.setStatus(400);
			response.getWriter().println(gson.toJson("One or more probability is empty!"));

			throw new IOException("One or more probability is empty!");
		}

		
			

			
		

		

		

	}
}