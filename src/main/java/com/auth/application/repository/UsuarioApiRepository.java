package com.auth.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.application.model.UsuarioApi;

public interface UsuarioApiRepository extends JpaRepository<UsuarioApi, Integer>{

	Optional<UsuarioApi> findByLogin(String login);
}
