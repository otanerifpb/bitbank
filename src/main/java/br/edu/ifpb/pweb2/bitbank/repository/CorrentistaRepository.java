package br.edu.ifpb.pweb2.bitbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.bitbank.model.Correntista;

//@Repository
public interface CorrentistaRepository extends JpaRepository<Correntista, Integer> {

}