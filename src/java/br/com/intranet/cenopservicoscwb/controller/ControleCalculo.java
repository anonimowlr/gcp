/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.intranet.cenopservicoscwb.controller;

import br.com.intranet.cenopservicoscwb.dao.CalculoDAO;
import br.com.intranet.cenopservicoscwb.dao.ClienteDAO;
import br.com.intranet.cenopservicoscwb.dao.ExpurgoDAO;
import br.com.intranet.cenopservicoscwb.dao.FuncionarioDAO;
import br.com.intranet.cenopservicoscwb.dao.IndiceDAO;
import br.com.intranet.cenopservicoscwb.dao.MetodologiaDAO;
import br.com.intranet.cenopservicoscwb.dao.NpjDAO;
import br.com.intranet.cenopservicoscwb.dao.PlanoEconomicoDAO;
import br.com.intranet.cenopservicoscwb.dao.ProtocoloGsvDAO;
import br.com.intranet.cenopservicoscwb.model.calculo.MotorCalculoPoupanca;
import br.com.intranet.cenopservicoscwb.model.entidade.Arquivo;
import br.com.intranet.cenopservicoscwb.model.entidade.Atualizacao;
import br.com.intranet.cenopservicoscwb.model.entidade.Calculo;
import br.com.intranet.cenopservicoscwb.model.entidade.Cliente;
import br.com.intranet.cenopservicoscwb.model.entidade.Expurgo;
import br.com.intranet.cenopservicoscwb.model.entidade.Funcionario;
import br.com.intranet.cenopservicoscwb.model.entidade.Honorario;
import br.com.intranet.cenopservicoscwb.model.entidade.Indice;
import br.com.intranet.cenopservicoscwb.model.entidade.JuroRemuneratorio;
import br.com.intranet.cenopservicoscwb.model.entidade.Metodologia;
import br.com.intranet.cenopservicoscwb.model.entidade.Mora;
import br.com.intranet.cenopservicoscwb.model.entidade.Multa;
import br.com.intranet.cenopservicoscwb.model.entidade.Npj;
import br.com.intranet.cenopservicoscwb.model.entidade.PeriodoCalculo;
import br.com.intranet.cenopservicoscwb.model.entidade.PlanoEconomico;
import br.com.intranet.cenopservicoscwb.model.entidade.ProtocoloGsv;
import br.com.intranet.cenopservicoscwb.model.pdf.GerarPdf;
import br.com.intranet.cenopservicoscwb.model.util.Utils;
import br.com.intranet.cenopservicoscwb.util.Util;
import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Calendar;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author f5078775
 */
@ManagedBean
@ViewScoped
public class ControleCalculo implements Serializable {

    
    private Part file;
    private CalculoDAO<Calculo, Object> calculoDAO;
    private ExpurgoDAO<Expurgo, Object> expurgoDAO;
    private ProtocoloGsvDAO<ProtocoloGsv, Object> protocoloGsvDAO;
    private NpjDAO<Npj, Object> npjDAO;
    private PlanoEconomicoDAO<PlanoEconomico, Object> planoEconomicoDAO;
    private MetodologiaDAO<Metodologia, Object> metodologiaDAO;
    private IndiceDAO<Indice, Object> indiceDAO;
    private FuncionarioDAO<Funcionario, Object> funcionarioDAO;
    private ClienteDAO<Cliente, Object> clienteDAO;
    private Npj npj;
    private ProtocoloGsv protocoloGsv;
    private Calculo calculo;
    private Cliente cliente;
    private Expurgo expurgo;
    private Metodologia metodologia;
    private JuroRemuneratorio juroRemuneratorio;
    private Funcionario funcionario;
    private PlanoEconomico planoEconomico;
    private Multa multa;
    private Honorario honorario;
    private Mora mora;
    private Arquivo arquivo;
    private Atualizacao atualizacao;
    private PeriodoCalculo periodoCalculo;

    public ControleCalculo() {
        npj = new Npj();
        protocoloGsv = new ProtocoloGsv();
        arquivo = new Arquivo();
        npjDAO = new NpjDAO<>();
        cliente = new Cliente();
        mora = new Mora();
        juroRemuneratorio = new JuroRemuneratorio();
        honorario = new Honorario();
        multa = new Multa();
        calculoDAO = new CalculoDAO<>();
        expurgoDAO = new ExpurgoDAO<>();
        protocoloGsvDAO = new ProtocoloGsvDAO<>();
        planoEconomicoDAO = new PlanoEconomicoDAO<>();
        metodologiaDAO = new MetodologiaDAO<>();
        indiceDAO = new IndiceDAO<>();
        funcionarioDAO = new FuncionarioDAO<>();
        clienteDAO = new ClienteDAO<>();

    }

    public void novo() {

        try {

            Npj npj = getNpjDAO().localizar(getNpj().getNrPrc());
            ProtocoloGsv protocoloGsv = getProtocoloGsvDAO().localizar(getProtocoloGsv().getCdPrc());

            if (npj != null) {
                setNpj(npj);
            }

            if (protocoloGsv != null) {

                setHonorario(new Honorario());
                setProtocoloGsv(protocoloGsv);
                setCalculo(new Calculo());
                setMora(new Mora());
                setMulta(new Multa());
                setCliente(new Cliente());
                setJuroRemuneratorio(new JuroRemuneratorio());
                setArquivo(new Arquivo());

                getCalculo().setCliente(getCliente());
                getCliente().adicionarCalculo(getCalculo());
                getCalculo().setMora(getMora());
                getCalculo().setMulta(getMulta());
                getCalculo().setHonorario(getHonorario());
                getCalculo().setJuroRemuneratorio(getJuroRemuneratorio());
                getCalculo().setArquivo(getArquivo());
                getProtocoloGsv().adicionarCalculo(getCalculo());

            } else {
                getNpj().adicionarProtocolo(getProtocoloGsv());
                salvar();
                setCalculo(new Calculo());
                getCalculo().setCliente(getCliente());
                getCliente().adicionarCalculo(getCalculo());
                getCalculo().setMora(getMora());
                getCalculo().setMulta(getMulta());
                getCalculo().setHonorario(getHonorario());
                getCalculo().setJuroRemuneratorio(getJuroRemuneratorio());
                getCalculo().setArquivo(getArquivo());
                getProtocoloGsv().adicionarCalculo(getCalculo());
            }

            setPeriodoCalculo(new PeriodoCalculo());
            getCalculo().adicionarPeriodoCalculo(getPeriodoCalculo());

//            setCalculo(new Calculo());
//            getProtocoloGsv().adicionarCalculo(getCalculo());
        } catch (Exception e) {
            Util.mensagemErro(Util.getMensagemErro(e));
        }

    }

    public void duplicar() {

        setCalculo(new Calculo());
        setMora(new Mora());
        setCliente(new Cliente());
        setHonorario(new Honorario());
        setMulta(new Multa());
        setJuroRemuneratorio(new JuroRemuneratorio());
        setArquivo(new Arquivo());

        getCalculo().setCliente(getCliente());
        getCliente().adicionarCalculo(getCalculo());
        getCalculo().setMora(getMora());
        getCalculo().setHonorario(getHonorario());
        getCalculo().setMulta(getMulta());
        getCalculo().setJuroRemuneratorio(getJuroRemuneratorio());
        getProtocoloGsv().adicionarCalculo(getCalculo());
        setPeriodoCalculo(new PeriodoCalculo());
        getCalculo().setArquivo(getArquivo());
        getCalculo().adicionarPeriodoCalculo(getPeriodoCalculo());
    }

    public void salvar() {

        if (getNpjDAO().atualizar(getNpj())) {
            Util.mensagemInformacao(getNpjDAO().getMensagem());

        } else {

            Util.mensagemErro(getNpjDAO().getMensagem());
        }

    }
    
    
    public void upload(){
     
        try(InputStream is = file.getInputStream()){
            
           String tipoArquivo = file.getContentType();
           
           getCalculo().getArquivo().setNomeArquivo(file.getName());
           getCalculo().getArquivo().setTipoArquivo(tipoArquivo);
           
           
           if(!tipoArquivo.equals("image/jpeg")){
               Util.mensagemErro("Selecione um arquivo do tipo image/jpeg ");
              return;
           }
           
           this.arquivo.setNomeArquivo(file.getSubmittedFileName());
           //this.tamanhoArquivo=file.getSize();
           
           
           
           if(!Utils.criarDiretorio(getCalculo())){
             Util.mensagemErro("Não foi possivel criar diretório para a imagem!!");
             return;
           }
            
             Files.copy(is, new java.io.File("/usr/local/apache-tomcat-8.0.15/webapps/docsPoupanca/" + calculo.getProtocoloGsv().getNpj().getNrPrc().toString() + "/" + calculo.getProtocoloGsv().getCdPrc().toString() + "/" + calculo.getCliente().getCpf() + "/" + calculo.getNumeroConta() ,getCalculo().getArquivo().getNomeArquivo()).toPath());
              // Files.copy(is, new java.io.File("C:\\Users\\f5078775\\Desktop\\DistribuidorPoupancaTeste\\" + calculo.getProtocoloGsv().getNpj().getNrPrc().toString() + "/" + calculo.getProtocoloGsv().getCdPrc().toString() + "/" + calculo.getCliente().getCpf() + "/" + calculo.getNumeroConta() ,getCalculo().getArquivo().getNomeArquivo()).toPath());
             //Files.copy(is, new java.io.File("/home/jocimar/Área de Trabalho/TestePlanilha/" + calculo.getProtocoloGsv().getNpj().getNrPrc().toString() + "/" + calculo.getProtocoloGsv().getCdPrc().toString() + "/" + calculo.getCliente().getCpf() + "/" + calculo.getNumeroConta() ,getCalculo().getArquivo().getNomeArquivo()).toPath());
           //getComplementoPoupanca().setImagemExtrato(fileName);
           
           Util.mensagemInformacao(getArquivo().getNomeArquivo() + tipoArquivo + " Enviado com sucesso!!");
           
        } catch(Exception ex){
         Util.mensagemErro(Util.getMensagemErro(ex));
        }
       
        
    }

    public void removeLinhaCalculo(Calculo calculo) {

      getProtocoloGsv().getListaCalculo().remove(calculo);

    }
    
    
    public void atribuirDataInicialPlano(Calculo calculo){
        
        
        Util.mensagemInformacao("Teste...");
        
    }
    
    

    public void removerProtocolo() {

        getProtocoloGsvDAO().deletar(getProtocoloGsv());

    }

    public void avaliarParaSalvar(Calculo calculo) throws ParseException, IOException {

        if (calculo.getId() == null) {
            complementarDadosCalculo();
            MotorCalculoPoupanca motorCalculoPoupanca = new MotorCalculoPoupanca();
            motorCalculoPoupanca.calcular(calculo);
            salvarCalculo(calculo);

        } else {

            complementarDadosCalculo();
            MotorCalculoPoupanca motorCalculoPoupanca = new MotorCalculoPoupanca();
            motorCalculoPoupanca.calcular(calculo);

            atualizarCalculo(calculo);
        }

    }

    public void salvarCalculo(Calculo calculo) {

        if (getCalculoDAO().salvar(calculo)) {
            Util.mensagemInformacao(getCalculoDAO().getMensagem());

        } else {

            Util.mensagemErro(getCalculoDAO().getMensagem());
        }

    }

    public void atualizarCalculo(Calculo calculo) {

        if (getCalculoDAO().atualizar(calculo)) {

            Util.mensagemInformacao(getCalculoDAO().getMensagem());
        } else {
            Util.mensagemErro(getCalculoDAO().getMensagem());

        }

    }

    public void teste() {
        Util.mensagemInformacao("Desenvolver o método");
    }

    public void gerarPdf(Calculo calculo) throws DocumentException, ParseException {

        try {
            GerarPdf gerarPdf = new GerarPdf();
            gerarPdf.gerarDocumento(calculo);
        } catch (IOException ex) {

            Util.mensagemErro(Util.getMensagemErro(ex));
        }

    }
    
    
    public void downloadPdf(Calculo calculo) throws DocumentException, ParseException, FileNotFoundException, IOException {

            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext externalContext = fc.getExternalContext();

            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + calculo.getCliente().getNomeCliente() + " - " + Utils.tratarConta(calculo.getNumeroConta().toString()) +  " - "  + calculo.getPlanoEconomico().getNomePlanoEconomico() + " - "  + Utils.converterToMoney(calculo.getValorFinal().toString()) +  ".pdf\"");


            FileInputStream inputStream = new FileInputStream(new File("/usr/local/apache-tomcat-8.0.15/webapps/docsPoupanca/" + calculo.getCliente().getNomeCliente() + " - " + Utils.tratarConta(calculo.getNumeroConta().toString()) +  " - "  + calculo.getPlanoEconomico().getNomePlanoEconomico() + " - "  + Utils.converterToMoney(calculo.getValorFinal().toString()) +  ".pdf"));
            //FileInputStream inputStream = new FileInputStream(new File("/opt/apache-tomcat-8.5.39/webapps/utilitario/Resumo Poupador CPF - " + Utils.tratarVariavel(complemento.getCpf()) + ".pdf"));
            //FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\f5078775\\Desktop\\DistribuidorPoupancaTeste\\" + calculo.getCliente().getNomeCliente() + " - " + Utils.tratarConta(calculo.getNumeroConta().toString()) +  " - "  + calculo.getPlanoEconomico().getNomePlanoEconomico() + " - "  + Utils.converterToMoney(calculo.getValorFinal().toString()) +   ".pdf"));            
            OutputStream out = externalContext.getResponseOutputStream();
            byte[] buffer = new byte[1024];
            int lenght;

            while ((lenght = inputStream.read(buffer)) > 0) {
                out.write(buffer);
            }

            out.flush();
            fc.responseComplete();
        
        
        
        
        
        
        
        
    }

    public void complementarDadosCalculo() {
        
     FacesContext fc = FacesContext.getCurrentInstance();
     HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);

     Funcionario usuario = (Funcionario) session.getAttribute("usuarioLogado");
        

        try {

            getCalculo().setDataRealizacaoCalculo(Calendar.getInstance().getTime());

            Cliente cliente = getClienteDAO().localizarCliente(getCalculo().getCliente().getCpf());
            if (cliente != null) {
                getCalculo().setCliente(cliente);
            }

//            Arquivo arquivo = new Arquivo();
//            arquivo.setEnderecoArquivo("/qqcoisa");
//            arquivo.setNomeArquivo("arquivoX");
//            arquivo.setNpjArquivo(new Long("555555555"));
//            arquivo.setTipoArquivo(".pdf");
//            getCalculo().adicionarArquivo(arquivo);
//            
            
            Funcionario funcionario =  getFuncionarioDAO().localizarFuncionarioPorChave(usuario.getChaveFunci());
            
            if(usuario.getNomeGerente()==null){
            funcionario.setNomeGerente("");
                
            }else{
                funcionario.setNomeGerente(usuario.getNomeGerente());
            }
            funcionario.setNomeFunci(usuario.getNomeFunci());
            funcionario.setNomeFuncao(usuario.getNomeFuncao());
            
            getCalculo().setFuncionario(funcionario);

        } catch (Exception e) {
            Util.mensagemErro(Util.getMensagemErro(e));
        }

    }

    /**
     * @return the calculoDAO
     */
    public CalculoDAO<Calculo, Object> getCalculoDAO() {
        return calculoDAO;
    }

    /**
     * @param calculoDAO the calculoDAO to set
     */
    public void setCalculoDAO(CalculoDAO<Calculo, Object> calculoDAO) {
        this.calculoDAO = calculoDAO;
    }

    /**
     * @return the npjDAO
     */
    public NpjDAO<Npj, Object> getNpjDAO() {
        return npjDAO;
    }

    /**
     * @param npjDAO the npjDAO to set
     */
    public void setNpjDAO(NpjDAO<Npj, Object> npjDAO) {
        this.npjDAO = npjDAO;
    }

    /**
     * @return the planoEconomicoDAO
     */
    public PlanoEconomicoDAO<PlanoEconomico, Object> getPlanoEconomicoDAO() {
        return planoEconomicoDAO;
    }

    /**
     * @param planoEconomicoDAO the planoEconomicoDAO to set
     */
    public void setPlanoEconomicoDAO(PlanoEconomicoDAO<PlanoEconomico, Object> planoEconomicoDAO) {
        this.planoEconomicoDAO = planoEconomicoDAO;
    }

    /**
     * @return the metodologiaDAO
     */
    public MetodologiaDAO<Metodologia, Object> getMetodologiaDAO() {
        return metodologiaDAO;
    }

    /**
     * @param metodologiaDAO the metodologiaDAO to set
     */
    public void setMetodologiaDAO(MetodologiaDAO<Metodologia, Object> metodologiaDAO) {
        this.metodologiaDAO = metodologiaDAO;
    }

    /**
     * @return the indiceDAO
     */
    public IndiceDAO<Indice, Object> getIndiceDAO() {
        return indiceDAO;
    }

    /**
     * @param indiceDAO the indiceDAO to set
     */
    public void setIndiceDAO(IndiceDAO<Indice, Object> indiceDAO) {
        this.indiceDAO = indiceDAO;
    }

    /**
     * @return the funcionarioDAO
     */
    public FuncionarioDAO<Funcionario, Object> getFuncionarioDAO() {
        return funcionarioDAO;
    }

    /**
     * @param funcionarioDAO the funcionarioDAO to set
     */
    public void setFuncionarioDAO(FuncionarioDAO<Funcionario, Object> funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

    /**
     * @return the clienteDAO
     */
    public ClienteDAO<Cliente, Object> getClienteDAO() {
        return clienteDAO;
    }

    /**
     * @param clienteDAO the clienteDAO to set
     */
    public void setClienteDAO(ClienteDAO<Cliente, Object> clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    /**
     * @return the npj
     */
    public Npj getNpj() {
        return npj;
    }

    /**
     * @param npj the npj to set
     */
    public void setNpj(Npj npj) {
        this.npj = npj;
    }

    /**
     * @return the protocoloGsv
     */
    public ProtocoloGsv getProtocoloGsv() {
        return protocoloGsv;
    }

    /**
     * @param protocoloGsv the protocoloGsv to set
     */
    public void setProtocoloGsv(ProtocoloGsv protocoloGsv) {
        this.protocoloGsv = protocoloGsv;
    }

    /**
     * @return the calculo
     */
    public Calculo getCalculo() {
        return calculo;
    }

    /**
     * @param calculo the calculo to set
     */
    public void setCalculo(Calculo calculo) {
        this.calculo = calculo;
    }

    /**
     * @return the cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * @return the expurgo
     */
    public Expurgo getExpurgo() {
        return expurgo;
    }

    /**
     * @param expurgo the expurgo to set
     */
    public void setExpurgo(Expurgo expurgo) {
        this.expurgo = expurgo;
    }

    /**
     * @return the metodologia
     */
    public Metodologia getMetodologia() {
        return metodologia;
    }

    /**
     * @param metodologia the metodologia to set
     */
    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    /**
     * @return the funcionario
     */
    public Funcionario getFuncionario() {
        return funcionario;
    }

    /**
     * @param funcionario the funcionario to set
     */
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    /**
     * @return the planoEconomico
     */
    public PlanoEconomico getPlanoEconomico() {
        return planoEconomico;
    }

    /**
     * @param planoEconomico the planoEconomico to set
     */
    public void setPlanoEconomico(PlanoEconomico planoEconomico) {
        this.planoEconomico = planoEconomico;
    }

    /**
     * @return the multa
     */
    public Multa getMulta() {
        return multa;
    }

    /**
     * @param multa the multa to set
     */
    public void setMulta(Multa multa) {
        this.multa = multa;
    }

    /**
     * @return the honorario
     */
    public Honorario getHonorario() {
        return honorario;
    }

    /**
     * @param honorario the honorario to set
     */
    public void setHonorario(Honorario honorario) {
        this.honorario = honorario;
    }

    /**
     * @return the mora
     */
    public Mora getMora() {
        return mora;
    }

    /**
     * @param mora the mora to set
     */
    public void setMora(Mora mora) {
        this.mora = mora;
    }

    /**
     * @return the arquivo
     */
    public Arquivo getArquivo() {
        return arquivo;
    }

    /**
     * @param arquivo the arquivo to set
     */
    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    /**
     * @return the atualizacao
     */
    public Atualizacao getAtualizacao() {
        return atualizacao;
    }

    /**
     * @param atualizacao the atualizacao to set
     */
    public void setAtualizacao(Atualizacao atualizacao) {
        this.atualizacao = atualizacao;
    }

    /**
     * @return the periodoCalculo
     */
    public PeriodoCalculo getPeriodoCalculo() {
        return periodoCalculo;
    }

    /**
     * @param periodoCalculo the periodoCalculo to set
     */
    public void setPeriodoCalculo(PeriodoCalculo periodoCalculo) {
        this.periodoCalculo = periodoCalculo;
    }

    /**
     * @return the protocoloGsvDAO
     */
    public ProtocoloGsvDAO<ProtocoloGsv, Object> getProtocoloGsvDAO() {
        return protocoloGsvDAO;
    }

    /**
     * @param protocoloGsvDAO the protocoloGsvDAO to set
     */
    public void setProtocoloGsvDAO(ProtocoloGsvDAO<ProtocoloGsv, Object> protocoloGsvDAO) {
        this.protocoloGsvDAO = protocoloGsvDAO;
    }

    /**
     * @return the expurgoDAO
     */
    public ExpurgoDAO<Expurgo, Object> getExpurgoDAO() {

        return expurgoDAO;
    }

    /**
     * @param expurgoDAO the expurgoDAO to set
     */
    public void setExpurgoDAO(ExpurgoDAO<Expurgo, Object> expurgoDAO) {
        this.expurgoDAO = expurgoDAO;
    }

    /**
     * @return the juroRemuneratorio
     */
    public JuroRemuneratorio getJuroRemuneratorio() {
        return juroRemuneratorio;
    }

    /**
     * @param juroRemuneratorio the juroRemuneratorio to set
     */
    public void setJuroRemuneratorio(JuroRemuneratorio juroRemuneratorio) {
        this.juroRemuneratorio = juroRemuneratorio;
    }

    /**
     * @return the file
     */
    public Part getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(Part file) {
        this.file = file;
    }

}
