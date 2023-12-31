package com.mfdesenvolvimento.marketplace.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mfdesenvolvimento.marketplace.employee.IEmployeeRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterProductAuth extends OncePerRequestFilter {

	@Autowired
	private IEmployeeRepository employeeRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var servletPath = request.getServletPath();

		if (servletPath.startsWith("/products/")) {

			// Obter o Auth(usuario e senha)
			var authorization = request.getHeader("Authorization");

			var authEncoded = authorization.substring("basic".length()).trim();

			byte[] authDecode = Base64.getDecoder().decode(authEncoded);

			var authString = new String(authDecode);

			String[] credentials = authString.split(":");
			String username = credentials[0];
			String password = credentials[1];

			// Validar o user
			var user = this.employeeRepository.findByUsername(username);
			if (user == null) {
				response.sendError(401);
			} else {
				var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
				if (passwordVerify.verified) {
					request.setAttribute("idEmployee", user.getId());
					filterChain.doFilter(request, response);
				} else {
					response.sendError(401);
				}
			}
		} else {
			filterChain.doFilter(request, response);
		}

	}

}