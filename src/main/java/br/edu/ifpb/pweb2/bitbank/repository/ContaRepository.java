package br.edu.ifpb.pweb2.bitbank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.bitbank.model.Conta;
import br.edu.ifpb.pweb2.bitbank.model.Correntista;

//@Repository
public interface ContaRepository extends JpaRepository<Conta, Integer> {
    Conta findByCorrentista(Correntista correntista);

    @Query(value = "from Conta c left join fetch c.transacoes t where c.numero = :numero")
    Conta findByNumeroWithTransacoes(String numero);

    @Query(value = "select distinct c from Conta c left join fetch c.transacoes t where c.id = :id")
    Conta findByIdWithTransacoes(Integer id);

    // Verifica no banco se tem antes de cadastrar
    Optional<Conta> findByNumero(String numero);
}
