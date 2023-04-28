package br.edu.ifpb.pweb2.bitbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Conta implements Serializable {
    // Para garantir que a assinatura de um número seja única , para o uso do @Id
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String numero;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date data;

    // Se tem uma coleção usar o "mappedBy"
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL)
    private Set<Transacao> transacoes = new HashSet<Transacao>();

    public void addTransacao(Transacao transacao) {
        this.transacoes.add(transacao);
        transacao.setConta(this);
    }

    @OneToOne
    // Indica a criação de uma coluna para a chave estrangeira onde o nome da coluna é "id_correntista"
    @JoinColumn(name = "id_correntista")
    private Correntista correntista;

    // Fazer a ligação de uma conta com um correntista
    public Conta(Correntista correntista) {
        this.correntista = correntista;
    }

    public BigDecimal getSaldo() {
        BigDecimal total = BigDecimal.ZERO;
        for (Transacao t : this.transacoes) {
            total = total.add(t.getValor());
        }
        return total;
    }

}
