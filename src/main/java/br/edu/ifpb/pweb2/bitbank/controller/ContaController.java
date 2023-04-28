package br.edu.ifpb.pweb2.bitbank.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.bitbank.model.Conta;
import br.edu.ifpb.pweb2.bitbank.model.Correntista;
import br.edu.ifpb.pweb2.bitbank.model.Transacao;
import br.edu.ifpb.pweb2.bitbank.repository.ContaRepository;
import br.edu.ifpb.pweb2.bitbank.repository.CorrentistaRepository;

@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    CorrentistaRepository correntistaRepository;

    @Autowired
    ContaRepository contaRepository;

    @RequestMapping("/form")
    public ModelAndView getForm(ModelAndView model) {
        model.addObject("conta", new Conta(new Correntista()));
        model.setViewName("contas/form");
        return model;
    }

    // Rota para relacionamento class A com Class B para Form
    @ModelAttribute("correntistaItems")
    public List<Correntista> getCorrentistas() {
        return correntistaRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView save(Conta conta, ModelAndView model, RedirectAttributes attrs) {
        Correntista correntista = null;
        //testar antes de salvar verificar se ja existe no banco com o if, com a query do repository
        Optional<Correntista> opCorrentista = correntistaRepository.findById(conta.getCorrentista().getId());
        if (opCorrentista.isPresent()) {
            correntista = opCorrentista.get();
            conta.setCorrentista(correntista);
            contaRepository.save(conta);
            attrs.addFlashAttribute("mensagem", "Conta cadastrada com sucesso!");
            model.setViewName("redirect:contas");
        } else {
            model.addObject("mensagem", "Correntista com id = " + conta.getCorrentista().getId() + " não encontrado.");
            model.setViewName("contas/form");
        }
        return model;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String listAll(Model model) {
        model.addAttribute("contas", contaRepository.findAll());
        return "contas/list";
    }

    @RequestMapping("/{id}")
    public ModelAndView getContaById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        Optional<Conta> opConta = contaRepository.findById(id);
        if (opConta.isPresent()) {
            model.setViewName("contas/form");
            model.addObject("conta", opConta.get());
        } else {
            model.setViewName("contas/list");
            model.addObject("mensagem", "Conta com id " + id + " não encontrado.");
        }
        return model;
    }

    @RequestMapping("/nuconta")
    public String getNuConta() {
        return "contas/operacao";
    }

    @RequestMapping(value = "/operacao")
    public ModelAndView operacaoConta(String nuConta, Transacao transacao, ModelAndView model) {
        String proxPagina = "";
        if (nuConta != null && transacao.getValor() == null) {
            Conta conta = contaRepository.findByNumeroWithTransacoes(nuConta);
            if (conta != null) {
                // addObject(versão anterior - com retorno de String) addAttribute(versão atual - com retorno ModelAndView)
                model.addObject("conta", conta);
                // model.addAttribute("conta", conta);
                model.addObject("transacao", transacao);
                // model.addAttribute("transacao", transacao);
                proxPagina = "contas/operacao";
            } else {
                model.addObject("mensagem", "Conta inexistente!");
                // model.addAttribute("mensagem", "Conta inexistente!");
                proxPagina = "contas/operacao";
            }
        } else {
            Conta conta = contaRepository.findByNumeroWithTransacoes(nuConta);
            conta.addTransacao(transacao);
            contaRepository.save(conta);
            proxPagina = "redirect:/contas/" + conta.getId() + "/transacoes";
            // proxPagina = addTransacaoConta(conta.getId(), model);
        }
        //model.addObject("conta", conta);
        model.setViewName(proxPagina);
        return model;
        // return proxPagina;
    }

    @RequestMapping(value = "/{id}/transacoes")
    public String addTransacaoConta(@PathVariable("id") Integer idConta, Model model) {
        Conta conta = contaRepository.findByIdWithTransacoes(idConta);
        model.addAttribute("conta", conta);
        //model.addObject("conta", conta);
        //model.setViewName("contas/transacoes");
        //return model;
        return "contas/transacoes";
    }

    @RequestMapping("{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id") Integer id, ModelAndView model, RedirectAttributes redAtt) {
        Optional<Conta> listaConta = contaRepository.findById(id);
        if (listaConta.isPresent()) {
            Conta conta = listaConta.get();
            contaRepository.deleteById(id);
            redAtt.addFlashAttribute("mensagem", "Conta "+conta.getCorrentista().getNome()+" deletado com sucesso!!");
        } else {
            redAtt.addFlashAttribute("mensagem", "Conta com id " + id + " não encontrado.");
        }
        model.setViewName("redirect:/correntistas");
        return model;
    }
}
