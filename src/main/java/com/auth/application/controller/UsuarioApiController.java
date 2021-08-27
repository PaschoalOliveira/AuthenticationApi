package com.auth.application.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.auth.application.dto.CredenciaisDto;
import com.auth.application.dto.TokenDto;
import com.auth.application.model.UsuarioApi;
import com.auth.application.service.JwtService;
import com.auth.application.service.UsuarioServiceApi;

@RestController
@RequestMapping("/api/usuariosApi")
@CrossOrigin
public class UsuarioApiController {

	@Autowired
	private UsuarioServiceApi usuarioService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping
	//Método no controller responsável por salvar um novo usuário de api
	public ResponseEntity<UsuarioApi> salvar(@RequestBody UsuarioApi usuario){
		
		//Codifica a senha passada
		String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		return new ResponseEntity<UsuarioApi>(usuarioService.salvar(usuario), HttpStatus.CREATED);
	}
	
	//Método responsável por gerar um token para um usuário válido
	@PostMapping("/auth")
	public TokenDto autenticar(@RequestBody CredenciaisDto credenciais, HttpServletResponse response) {
		try {
			UsuarioApi usuario = new UsuarioApi();
			usuario.setLogin(credenciais.getLogin());
			usuario.setSenha(credenciais.getSenha());
			
			UserDetails userDetails = usuarioService.autenticar(usuario);
			
			String token = jwtService.gerarToken(usuario);
			
		    // create a cookie
		    Cookie cookie = new Cookie("username", "Jovan");
		    //add cookie to response
		    cookie.setPath("/");
		    cookie.setDomain("localhost");
		    response.addCookie(cookie);
		    
		    response.addHeader("Set-Cookie", cookie.toString());
		    response.addHeader("access-control-expose-headers","Set-Cookie");
		    
		    
		    ResponseCookie resCookie = ResponseCookie.from("edu", "1")
		            .httpOnly(true)
		            .sameSite("None")
		            .domain("127.0.0.1")
		            .secure(true)
		            .path("/")
		            .build();
		    response.addHeader("Set-Cookie", resCookie.toString());
		    
		    response.addHeader("Access-Control-Allow-Credentials", "true");
		    
		    
			return new TokenDto(usuario.getLogin(), token);
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		}
	}
}
